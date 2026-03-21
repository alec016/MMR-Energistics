package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.manager.handler.AbstractHandler;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import es.degrassi.mmreborn.energistics.client.container.MEOutputBusContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.util.AEItemHandler;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEOutputBusEntity extends MEEntity<ItemComponent, ItemHandler> {

  public MEOutputBusEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
    craftingRequester();
    gridTickable(new Ticker());
    getInventory().setListener(new AbstractHandler.HandlerUpdateListener<>() {
      @Override
      public void onChange(int slot, ItemStack itemStack) {
        getControllerPosSet().forEach(p -> {
          if (getLevel() == null) return;
          if (getLevel().isClientSide()) return;
          if (getLevel().getBlockEntity(p) instanceof MachineControllerEntity controller) {
            controller.getProcessor().setMachineInventoryChanged();
          }
        });
      }

      @Override
      public void onChange() {
        for (int slot = 0; slot < getInventory().getSlots(); ++slot) {
          this.onChange(slot, getInventory().getStackInSlot(slot));
        }
      }
    });
  }

  @Override
  protected void registerCapacities() {
  }

  @Override
  protected @Nullable IConfigManager createConfigManager() {
    return IConfigManager.builder(this::onConfigChanged).build();
  }

  @Override
  protected @Nullable ConfigInventory createConfig() {
    return null;
  }

  @Override
  protected @Nullable ConfigInventory createStorage() {
    return ConfigInventory
        .storage(size.getSlots())
        .supportedType(AEKeyType.items())
        .slotFilter(this::isAllowedInStorageSlot)
        .changeListener(this::onStorageChanged)
        .build();
  }

  @Override
  protected @Nullable IUpgradeInventory createUpgrades() {
    return UpgradeInventories.forMachine(getBlockState().getBlock().asItem(), 4, this::onUpgradesChanged);
  }

  @Nullable
  public ItemComponent provideComponent() {
    return new ItemComponent(getInventory(), IOType.OUTPUT);
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

  private void onConfigChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private boolean isAllowedInStorageSlot(int slot, AEKey what) {
    return true;
  }

  private void onUpgradesChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  protected ItemHandler createInventory() {
    return new AEItemHandler(this.getStorage(), generateSlots(getSize().getSlots()), generateSlots(0),
        getOrientation().getSide(RelativeSide.FRONT));
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
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEOutputBusContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEOutputBusContainer.TYPE, player, subMenu.getLocator());
  }
}
