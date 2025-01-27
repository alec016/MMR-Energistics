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
import appeng.api.stacks.AEKey;
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
import es.degrassi.mmreborn.energistics.client.container.MEInputChemicalHatchContainer;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.util.KeyStorage;
import es.degrassi.mmreborn.energistics.common.util.reflect.AEReflect;
import es.degrassi.mmreborn.mekanism.common.machine.ChemicalHatch;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.functions.ConstantPredicates;
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
public class MEInputChemicalHatchEntity extends MEEntity {
  private final KeyStorage internalBuffer; // Do not use KeyCounter, use our simple implementation
  private final BasicChemicalTank inventory;
  private final IUpgradeInventory upgrades;
  private final GenericStack[] plannedWork;
  private final MultiCraftingTracker craftingTracker;
  private final ConfigInventory config;
  private final ConfigInventory storage;
  private boolean hasConfig;
  protected final IActionSource requestActionSource;
  private final IConfigManager cm;
  private final InterfaceLogic logic;
  private int priority;
  private @Nullable MEStorage networkStorage;

  public MEInputChemicalHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
    this.internalBuffer = new KeyStorage();
    this.inventory = createInventory();
    int slots = size.getSlots();
    this.requestActionSource = new RequestActionSource(this, getMainNode()::getNode);
    this.upgrades = UpgradeInventories.forMachine(blockState.getBlock().asItem(), 6, this::onUpgradesChanged);
    this.config = ConfigInventory
        .configStacks(slots)
        .supportedType(MekanismKeyType.TYPE)
        .changeListener(this::onConfigRowChanged)
        .build();
    this.storage = ConfigInventory
        .storage(slots)
        .supportedType(MekanismKeyType.TYPE)
        .slotFilter(this::isAllowedInStorageSlot)
        .changeListener(this::onStorageChanged)
        .build();
    this.craftingTracker = new MultiCraftingTracker(this, slots);
    this.plannedWork = new GenericStack[slots];
    this.cm = IConfigManager.builder(this::onConfigChanged).registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL).build();
    this.logic = new InterfaceLogic(getMainNode(), this, ((MEBlock) getBlockState().getBlock()).item());
    getMainNode()
        .addService(ICraftingRequester.class, this)
        .addService(IGridTickable.class, new Ticker());
    this.getConfig().useRegisteredCapacities();
    this.getStorage().useRegisteredCapacities();
    this.getConfig().setCapacity(MekanismKeyType.TYPE, (size.isAdvanced() ? 4 : 1) * 16000);
    this.getStorage().setCapacity(MekanismKeyType.TYPE, (size.isAdvanced() ? 4 : 1) * 16000);
  }

  @Nullable
  public ChemicalHatch provideComponent() {
    return new ChemicalHatch(IOType.INPUT) {
      public BasicChemicalTank getContainerProvider() {
        return inventory;
      }
    };
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
    return grid != null && this.upgrades.isInstalled(AEItems.CRAFTING_CARD) && key != null && this.craftingTracker.handleCrafting(x, key, amount, this.getLevel(), grid.getCraftingService(), this.actionSource);
  }

  @Override
  public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.config.readFromChildTag(tag, "config", registries);
    this.storage.readFromChildTag(tag, "storage", registries);
    this.upgrades.readFromNBT(tag, "upgrades", registries);
    this.cm.readFromNBT(tag, registries);
    this.craftingTracker.readFromNBT(tag);
    this.priority = tag.getInt("priority");
    this.readConfig();
  }

  @Override
  public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    this.config.writeToChildTag(tag, "config", registries);
    this.storage.writeToChildTag(tag, "storage", registries);
    this.upgrades.writeToNBT(tag, "upgrades", registries);
    this.cm.writeToNBT(tag, registries);
    this.craftingTracker.writeToNBT(tag);
    tag.putInt("priority", this.priority);
  }

  private boolean usePlan(int x, AEKey what, int amount) {
    boolean changed = this.tryUsePlan(x, what, amount);
    if (changed) {
      this.updatePlan(x);
    }

    return changed;
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

  public void gridChanged() {
    this.networkStorage = this.getMainNode().getGrid().getStorageService().getInventory();
    this.notifyNeighbors();
  }

  @Override
  public void setPriority(int priority) {
    this.priority = priority;
    saveChanges();
  }

  private void onConfigChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private boolean isAllowedInStorageSlot(int slot, AEKey what) {
    if (slot >= this.config.size()) {
      return true;
    } else {
      AEKey configured = this.config.getKey(slot);
      return configured == null || configured.equals(what);
    }
  }

  private void onInventoryChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private void onStorageChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private void readConfig() {
    this.hasConfig = !this.config.isEmpty();
    this.updatePlan();
    this.notifyNeighbors();
  }

  public void notifyNeighbors() {
    if (getMainNode().isActive()) {
      this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    this.invalidateCapabilities();
  }

  private void onConfigRowChanged() {
    this.saveChanges();
    this.readConfig();
  }

  private void onUpgradesChanged() {
    this.saveChanges();
    if (!this.upgrades.isInstalled(AEItems.CRAFTING_CARD)) {
      this.cancelCrafting();
    }

    this.updatePlan();
  }

  private void cancelCrafting() {
    AEReflect.cancel(craftingTracker);
  }

  private void updatePlan() {
    boolean hadWork = this.hasWorkToDo();

    for (int x = 0; x < this.config.size(); ++x) {
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

  private void updatePlan(int slot) {
    GenericStack req = this.config.getStack(slot);
    GenericStack stored = this.storage.getStack(slot);
    if (req == null && stored != null) {
      this.plannedWork[slot] = new GenericStack(stored.what(), -stored.amount());
    } else if (req != null) {
      if (stored == null) {
        this.plannedWork[slot] = req;
      } else if (this.storedRequestEquals(req.what(), stored.what())) {
        if (req.amount() != stored.amount()) {
          this.plannedWork[slot] = new GenericStack(req.what(), req.amount() - stored.amount());
        } else {
          this.plannedWork[slot] = null;
        }
      } else {
        this.plannedWork[slot] = new GenericStack(stored.what(), -stored.amount());
      }
    } else {
      this.plannedWork[slot] = null;
    }

  }

  private boolean storedRequestEquals(AEKey request, AEKey stored) {
    return this.upgrades.isInstalled(AEItems.FUZZY_CARD) && request.supportsFuzzyRangeSearch() ?
        request.fuzzyEquals(stored, this.cm.getSetting(Settings.FUZZY_MODE)) : request.equals(stored);
  }

  protected final boolean hasWorkToDo() {
    for (GenericStack requiredWork : this.plannedWork) {
      if (requiredWork != null) {
        return true;
      }
    }

    return false;
  }

  protected BasicChemicalTank createInventory() {
    return new BasicChemicalTank(
        16000,
        ((chemical, automationType) -> getSize().isInput() || automationType == AutomationType.INTERNAL),
        ((chemical, automationType) -> !getSize().isInput() || automationType == AutomationType.INTERNAL),
        ConstantPredicates.alwaysTrue(),
        ChemicalAttributeValidator.ALWAYS_ALLOW,
        null
    ) {
      @Override
      public int getChemicalTanks() {
        return storage.size();
      }

      @Override
      public boolean isValid(ChemicalStack stack) {
        for (int i = 0; i < storage.size(); i++) {
          if (storage.isAllowedIn(i, MekanismKey.of(stack)))
            return true;
        }
        return false;
      }

      @Override
      public ChemicalStack getChemicalInTank(int tank) {
        if (tank < 0 || tank >= getChemicalTanks()) {
          return ChemicalStack.EMPTY;
        }
        GenericStack stack = storage.getStack(tank);
        if (stack == null) return ChemicalStack.EMPTY;
        return ((MekanismKey) stack.what()).getStack();
      }

      @Override
      public long getChemicalTankCapacity(int tank) {
        return storage.getCapacity(MekanismKeyType.TYPE);
      }

      public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        storage.readFromChildTag(nbt, "storage", lookupProvider);
      }

      @Override
      public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        storage.writeToChildTag(nbt, "storage", lookupProvider);
        return nbt;
      }

      @Override
      public boolean isValid(int tank, ChemicalStack stack) {
        return storage.isAllowedIn(tank, MekanismKey.of(stack));
      }

      private static Actionable actionableFromAction(Action action) {
        return switch (action) {
          case SIMULATE -> Actionable.SIMULATE;
          case EXECUTE -> Actionable.MODULATE;
        };
      }

      @Override
      public ChemicalStack insert(ChemicalStack resource, Action action, AutomationType automationType) {
        if (resource == null || resource.isEmpty()) return ChemicalStack.EMPTY;
        return resource.copyWithAmount(storage.insert(MekanismKey.of(resource), resource.getAmount(), actionableFromAction(action), actionSource));
      }

      @Override
      public ChemicalStack extract(long amount, Action action, AutomationType automationType) {
        if (amount <= 0) return ChemicalStack.EMPTY;
        for (AEKey key : storage.getAvailableStacks().keySet()) {
          if (key instanceof MekanismKey mkey) {
            long extracted = storage.extract(mkey, amount, actionableFromAction(action), actionSource);
            if (extracted == amount)
              return mkey.getStack().copyWithAmount(extracted);
          }

        }

        return ChemicalStack.EMPTY;
      }

      @Override
      public void onContentsChanged() {
        onInventoryChanged();
      }

      @Override
      public void setStack(ChemicalStack stack) {
      }

      @Override
      public boolean isEmpty() {
        return true;
      }
    };
  }

  protected void syncME() {
    onStorageChanged();
  }

  @Override
  public ImmutableSet<ICraftingLink> getRequestedJobs() {
    return this.craftingTracker.getRequestedJobs();
  }

  @Override
  public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
    int slot = AEReflect.getSlot(this.craftingTracker, link);
    return this.storage.insert(slot, what, amount, mode);
  }

  @Override
  public void jobStateChange(ICraftingLink link) {
    this.craftingTracker.jobStateChange(link);
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEInputChemicalHatchContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEInputChemicalHatchContainer.TYPE, player, locator);
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
      MenuOpener.open(MEInputChemicalHatchContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEInputChemicalHatchContainer.TYPE, player, subMenu.getLocator());
  }

  private class RequestActionSource extends MachineSource {
    private final RequestContext context;

    RequestActionSource(final MEInputChemicalHatchEntity machine, IActionHost host) {
      super(host);
      this.context = machine.new RequestContext();
    }

    public <T> Optional<T> context(Class<T> key) {
      return key == RequestContext.class ? Optional.of(key.cast(this.context)) : super.context(key);
    }
  }

  private class RequestContext {
    private RequestContext() {
    }

    public int getPriority() {
      return MEInputChemicalHatchEntity.this.getPriority();
    }
  }

  private class Ticker implements IGridTickable {
    private Ticker() {
    }

    public TickingRequest getTickingRequest(IGridNode node) {
      return new TickingRequest(TickRates.Interface, !MEInputChemicalHatchEntity.this.hasWorkToDo());
    }

    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
      if (!MEInputChemicalHatchEntity.this.getMainNode().isActive()) {
        return TickRateModulation.SLEEP;
      } else {
        boolean couldDoWork = MEInputChemicalHatchEntity.this.updateStorage();
        return MEInputChemicalHatchEntity.this.hasWorkToDo() ? (couldDoWork ? TickRateModulation.URGENT : TickRateModulation.SLOWER) : TickRateModulation.SLEEP;
      }
    }
  }
}
