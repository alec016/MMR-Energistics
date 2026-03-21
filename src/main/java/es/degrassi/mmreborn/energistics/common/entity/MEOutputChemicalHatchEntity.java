package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.stacks.AEKey;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.energistics.client.container.MEOutputChemicalHatchContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.util.AEChemicalTankHolder;
import es.degrassi.mmreborn.mekanism.common.machine.component.ChemicalComponent;
import lombok.Getter;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.BasicChemicalTank;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEOutputChemicalHatchEntity extends MEEntity<ChemicalComponent, BasicChemicalTank> {

  public MEOutputChemicalHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
    craftingRequester();
    gridTickable(new Ticker());
  }

  @Override
  protected void registerCapacities() {
    this.getStorage().setCapacity(MekanismKeyType.TYPE, (size.isAdvanced() ? 4 : 1) * 16000);
  }

  @Override
  protected @Nullable IUpgradeInventory createUpgrades() {
    return UpgradeInventories.forMachine(getBlockState().getBlock().asItem(), 4, this::onUpgradesChanged);
  }

  @Override
  protected @Nullable ConfigInventory createStorage() {
    return ConfigInventory
        .storage(size.getSlots())
        .supportedType(MekanismKeyType.TYPE)
        .slotFilter(this::isAllowedInStorageSlot)
        .changeListener(this::onStorageChanged)
        .build();
  }

  @Override
  protected @Nullable ConfigInventory createConfig() {
    return null;
  }

  @Override
  protected @Nullable IConfigManager createConfigManager() {
    return IConfigManager.builder(this::onConfigChanged).registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL).build();
  }

  @Nullable
  public ChemicalComponent provideComponent() {
    return new ChemicalComponent(getInventory(), IOType.OUTPUT);
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
    if (!this.getUpgrades().isInstalled(AEItems.CRAFTING_CARD)) {
      this.cancelCrafting();
    }

    this.updatePlan();
  }

  protected BasicChemicalTank createInventory() {
    return new AEChemicalTankHolder(this);
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEOutputChemicalHatchContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEOutputChemicalHatchContainer.TYPE, player, locator);
    }
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEOutputChemicalHatchContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEOutputChemicalHatchContainer.TYPE, player, subMenu.getLocator());
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
