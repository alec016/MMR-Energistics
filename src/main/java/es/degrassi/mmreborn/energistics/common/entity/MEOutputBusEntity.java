package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.MultiCraftingTracker;
import appeng.me.helpers.MachineSource;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import com.google.common.collect.ImmutableSet;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ItemBus;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.energistics.client.container.MEOutputBusContainer;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.util.AEInventoryHolder;
import es.degrassi.mmreborn.energistics.common.util.KeyStorage;
import es.degrassi.mmreborn.energistics.common.util.reflect.AEReflect;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEOutputBusEntity extends MEEntity {
  private final KeyStorage internalBuffer; // Do not use KeyCounter, use our simple implementation
  private final IOInventory inventory;
  private final IUpgradeInventory upgrades;
  private final ConfigInventory storage;
  private final IConfigManager cm;
  private final InterfaceLogic logic;
  private int priority;
  private final GenericStack[] plannedWork;
  protected final IActionSource requestActionSource;
  private final MultiCraftingTracker craftingTracker;

  public MEOutputBusEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
    this.internalBuffer = new KeyStorage();
    this.inventory = createInventory();
    int slots = size.getSlots();
    this.requestActionSource = new MEOutputBusEntity.RequestActionSource(this, getMainNode()::getNode);
    this.upgrades = UpgradeInventories.forMachine(blockState.getBlock().asItem(), 4, this::onUpgradesChanged);
    this.storage = ConfigInventory
        .storage(slots)
        .supportedType(AEKeyType.items())
        .slotFilter(this::isAllowedInStorageSlot)
        .changeListener(this::onStorageChanged)
        .build();
    this.craftingTracker = new MultiCraftingTracker(this, slots);
    this.plannedWork = new GenericStack[slots];
    this.cm = IConfigManager.builder(this::onConfigChanged).build();
    this.logic = new InterfaceLogic(getMainNode(), this, ((MEBlock) getBlockState().getBlock()).item());
    inventory.setListener(this::onInventoryChanged);
    getMainNode()
        .addService(ICraftingRequester.class, this)
        .addService(IGridTickable.class, new MEOutputBusEntity.Ticker());
    this.getStorage().useRegisteredCapacities();
  }

  @Nullable
  public ItemBus provideComponent() {
    return new ItemBus(IOType.OUTPUT) {
      public IOInventory getContainerProvider() {
        return inventory;
      }
    };
  }

  private void readConfig() {
    this.updatePlan();
    this.notifyNeighbors();
  }

  public void notifyNeighbors() {
    if (getMainNode().isActive()) {
      this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    this.invalidateCapabilities();
  }

  @Override
  public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.storage.readFromChildTag(tag, "storage", registries);
    this.upgrades.readFromNBT(tag, "upgrades", registries);
    this.cm.readFromNBT(tag, registries);
    this.priority = tag.getInt("priority");
    this.readConfig();
  }

  @Override
  public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    this.storage.writeToChildTag(tag, "storage", registries);
    this.upgrades.writeToNBT(tag, "upgrades", registries);
    this.cm.writeToNBT(tag, registries);
    tag.putInt("priority", this.priority);
  }

  @Override
  public void setPriority(int priority) {
    this.priority = priority;
    saveChanges();
  }

  private void updatePlan() {
    boolean hadWork = this.hasWorkToDo();

    for (int x = 0; x < this.storage.size(); ++x) {
      this.updatePlan(x);
    }

    boolean hasWork = this.hasWorkToDo();
    if (hadWork != hasWork) {
      getMainNode().ifPresent((grid, node) -> {
        if (hasWork) {
          grid.getTickManager().alertDevice(node);
        } else {
          grid.getTickManager().sleepDevice(node);
        }

      });
    }

  }

  private void onConfigChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  protected final boolean hasWorkToDo() {
    for (GenericStack requiredWork : this.plannedWork) {
      if (requiredWork != null) {
        return true;
      }
    }

    return false;
  }

  private boolean updateStorage() {
    boolean didSomething = false;

    for (int x = 0; x < this.plannedWork.length; ++x) {
      GenericStack work = this.plannedWork[x];
      if (work != null) {
        int amount = (int) work.amount();
        didSomething = this.usePlan(x, work.what(), amount) || didSomething;
        if (didSomething) {
          this.onStorageChanged();
        }
      }
    }

    return didSomething;
  }

  private boolean tryUsePlan(int slot, AEKey what, int amount) {
    IGrid grid = this.getMainNode().getGrid();
    if (grid == null) {
      return false;
    } else {
      MEStorage networkInv = grid.getStorageService().getInventory();
      IEnergyService energySrc = grid.getEnergyService();
      if (amount < 0) {
        amount = -amount;
        GenericStack inSlot = this.storage.getStack(slot);
        if (what.matches(inSlot) && inSlot.amount() >= (long) amount) {
          int inserted = (int) StorageHelper.poweredInsert(energySrc, networkInv, what, amount, this.requestActionSource);
          if (inserted > 0) {
            this.storage.extract(slot, what, inserted, Actionable.MODULATE);
          }

          return inserted > 0;
        } else {
          return true;
        }
      } else if (AEReflect.isBusy(craftingTracker, slot)) {
        return this.handleCrafting(slot, what, amount);
      } else if (amount == 0) {
        return false;
      } else if (this.storage.insert(slot, what, amount, Actionable.SIMULATE) != (long) amount) {
        return true;
      } else if (this.acquireFromNetwork(energySrc, networkInv, slot, what, amount)) {
        return true;
      } else {
        if (this.storage.getStack(slot) == null && this.upgrades.isInstalled(AEItems.FUZZY_CARD)) {
          FuzzyMode fuzzyMode = this.getConfigManager().getSetting(Settings.FUZZY_MODE);

          for (Object2LongMap.Entry<AEKey> aeKeyEntry : grid.getStorageService().getCachedInventory().findFuzzy(what, fuzzyMode)) {
            long maxAmount = this.storage.insert(slot, aeKeyEntry.getKey(), amount, Actionable.SIMULATE);
            if (this.acquireFromNetwork(energySrc, networkInv, slot, aeKeyEntry.getKey(), maxAmount)) {
              return true;
            }
          }
        }

        return this.handleCrafting(slot, what, amount);
      }
    }
  }

  private boolean acquireFromNetwork(IEnergyService energySrc, MEStorage networkInv, int slot, AEKey what, long amount) {
    long acquired = StorageHelper.poweredExtraction(energySrc, networkInv, what, amount, this.requestActionSource);
    if (acquired > 0L) {
      long inserted = this.storage.insert(slot, what, acquired, Actionable.MODULATE);
      if (inserted < acquired) {
        throw new IllegalStateException("bad attempt at managing inventory. Voided items: " + inserted);
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  private boolean handleCrafting(int x, AEKey key, long amount) {
    IGrid grid = this.getMainNode().getGrid();
    return grid != null && this.upgrades.isInstalled(AEItems.CRAFTING_CARD) && this.craftingTracker.handleCrafting(x, key, amount, this.getLevel(), grid.getCraftingService(), this.actionSource);
  }

  private boolean usePlan(int x, AEKey what, int amount) {
    boolean changed = this.tryUsePlan(x, what, amount);
    if (changed) {
      this.updatePlan(x);
    }

    return changed;
  }

  private void updatePlan(int slot) {
    GenericStack stored = this.storage.getStack(slot);
    if (stored != null) {
      this.plannedWork[slot] = new GenericStack(stored.what(), -stored.amount());
    } else {
      this.plannedWork[slot] = null;
    }

  }

  private boolean isAllowedInStorageSlot(int slot, AEKey what) {
    return true;
  }

  private void onInventoryChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private void onStorageChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private void onUpgradesChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  public IOInventory copyInventory() {
    return createInventory();
  }

  protected IOInventory createInventory() {
    return new AEInventoryHolder(this, generateSlots(getSize().getSlots()), generateSlots(0), getOrientation().getSide(RelativeSide.FRONT));
  }

  protected void syncME() {
    onStorageChanged();
  }

  @Override
  public ImmutableSet<ICraftingLink> getRequestedJobs() {
    return ImmutableSet.of();
  }

  @Override
  public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
    return 0;
  }

  @Override
  public void jobStateChange(ICraftingLink link) {
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEOutputBusContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEOutputBusContainer.TYPE, player, locator);
    }
  }

  @Override
  public IConfigManager getConfigManager() {
    return cm;
  }

  @Override
  public InterfaceLogic getInterfaceLogic() {
    return logic;
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEOutputBusContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEOutputBusContainer.TYPE, player, subMenu.getLocator());
  }

  private class RequestActionSource extends MachineSource {
    private final MEOutputBusEntity.RequestContext context;

    RequestActionSource(final MEOutputBusEntity machine, IActionHost host) {
      super(host);
      this.context = machine.new RequestContext();
    }

    public <T> Optional<T> context(Class<T> key) {
      return key == MEOutputBusEntity.RequestContext.class ? Optional.of(key.cast(this.context)) : super.context(key);
    }
  }

  private class RequestContext {
    private RequestContext() {
    }

    public int getPriority() {
      return MEOutputBusEntity.this.getPriority();
    }
  }

  private class Ticker implements IGridTickable {
    private Ticker() {
    }

    public TickingRequest getTickingRequest(IGridNode node) {
      return new TickingRequest(TickRates.Interface, !MEOutputBusEntity.this.hasWorkToDo());
    }

    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
      if (!MEOutputBusEntity.this.getMainNode().isActive()) {
        return TickRateModulation.SLEEP;
      } else {
        boolean couldDoWork = MEOutputBusEntity.this.updateStorage();
        return MEOutputBusEntity.this.hasWorkToDo() ? (couldDoWork ? TickRateModulation.URGENT : TickRateModulation.SLOWER) : TickRateModulation.SLEEP;
      }
    }
  }
}
