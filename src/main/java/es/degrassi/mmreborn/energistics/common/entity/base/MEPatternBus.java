package es.degrassi.mmreborn.energistics.common.entity.base;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.helpers.InterfaceLogic;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.energistics.api.services.crafting.PatternBusLogic;
import es.degrassi.mmreborn.energistics.api.services.crafting.PatternBusLogicHost;
import es.degrassi.mmreborn.energistics.client.container.MEPatternBusContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEPatternBus extends MEEntity<MachineComponent<Void>, Void> implements PatternBusLogicHost {
  private static final IGridNodeListener<MEEntity<?, ?>> NODE_LISTENER = new BlockEntityNodeListener<>() {
    @Override
    public void onGridChanged(MEEntity<?, ?> nodeOwner, IGridNode node) {
      ((MEPatternBus) nodeOwner).getLogic().gridChanged();
    }
  };
  protected final PatternBusLogic logic;

  public MEPatternBus(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
    this.logic = createLogic();
  }

  @Override
  protected @Nullable InterfaceLogic createInterfaceLogic() {
    return null;
  }

  @Override
  protected void registerCapacities() {
  }

  @Override
  protected @Nullable IUpgradeInventory createUpgrades() {
    return null;
  }

  @Override
  protected @Nullable ConfigInventory createStorage() {
    return null;
  }

  @Override
  protected @Nullable ConfigInventory createConfig() {
    return null;
  }

  @Override
  protected @Nullable IConfigManager createConfigManager() {
    return null;
  }

  @Override
  protected IGridNodeListener<MEEntity<?, ?>> getNodeListener() {
    return NODE_LISTENER;
  }

  protected PatternBusLogic createLogic() {
    return new PatternBusLogic(this.getMainNode(), this, getSize().getSlots());
  }

  @Override
  protected Void createInventory() {
    return null;
  }

  public void updateSubscription() {
    this.logic.onMainNodeStateChanged();
    super.updateSubscription();
  }

  public void onReady() {
    super.onReady();
    this.logic.updatePatterns();
  }

  @Override
  protected void syncME() {
    this.saveChanges();
  }

  @Override
  public IConfigManager getConfigManager() {
    return logic.getConfigManager();
  }

  @Override
  public IUpgradeInventory getUpgrades() {
    return UpgradeInventories.empty();
  }

  @Override
  public int getPriority() {
    return logic.getPriority();
  }

  @Override
  public void setPriority(int newValue) {
    logic.setPriority(newValue);
  }

  @Override
  public ConfigInventory getStorage() {
    ConfigInventory inv =
        ConfigInventory.storage(logic.getPatternInventory().size())
            .allowOverstacking(true)
            .supportedTypes(AEKeyTypes.getAll())
            .build();

    AppEngInternalInventory internal = logic.getPatternInventory();
    for (int i = 0; i < internal.size(); i++) {
      ItemStack stack = internal.getStackInSlot(i);
      inv.insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, logic.getActionSource());
    }

    return inv;
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEPatternBusContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEPatternBusContainer.TYPE, player, locator);
    }
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEPatternBusContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEPatternBusContainer.TYPE, player, subMenu.getLocator());
  }

  public EnumSet<Direction> getTargets() {
    return EnumSet.allOf(Direction.class);
  }

  public void addDrops(List<ItemStack> list) {
    super.addDrops(list);
    logic.addDrops(list);
  }

  @Nullable
  public AEItemKey getTerminalIcon() {
    if (getController() == null) return AEItemKey.of(getItemFromBlockEntity());
    return AEItemKey.of(ControllerItem.makeMachineItem(getController().getId()));
  }

  @Override
  public final MEPatternBus getBlockEntity() {
    return this;
  }

  @Override
  @Nullable
  public MachineComponent<Void> provideComponent() {
    return null;
  }

  @Override
  public void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    this.logic.writeToNBT(nbt, pRegistries);
  }

  @Override
  public void loadTag(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadTag(nbt, pRegistries);
    this.logic.readFromNBT(nbt, pRegistries);
  }

  @Override
  protected void saveBaseTexture(CompoundTag compound, HolderLookup.Provider registries) {
    if (baseTexture != null)
      compound.putString("baseTexture", baseTexture.toString());
  }

  @Override
  protected void saveOverlayTexture(CompoundTag compound, HolderLookup.Provider registries) {
    if (overlayTexture != null)
      compound.putString("overlayTexture", overlayTexture.toString());
  }

  @Override
  protected void loadBaseTexture(CompoundTag compound, HolderLookup.Provider registries) {
    this.baseTexture = compound.contains("baseTexture") ? ResourceLocation.parse(compound.getString("baseTexture")) : defaultBaseTexture;
  }

  @Override
  protected void loadOverlayTexture(CompoundTag compound, HolderLookup.Provider registries) {
    this.defaultOverlayTexture = ModularMachineryReborn.rl("block/overlay_inputbus_" + size.getSerializedName());
    this.overlayTexture = compound.contains("overlayTexture") ? ResourceLocation.parse(compound.getString("overlayTexture")) : defaultOverlayTexture;
  }
}
