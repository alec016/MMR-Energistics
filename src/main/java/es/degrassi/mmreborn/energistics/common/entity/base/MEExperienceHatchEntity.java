package es.degrassi.mmreborn.energistics.common.entity.base;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.stacks.AEKey;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.impl.capability.BasicExperienceHandler;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.machine.component.ExperienceComponent;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MEExperienceHatchEntity extends MEEntity<ExperienceComponent, IExperienceHandler> {

  public MEExperienceHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);

    craftingRequester();
    gridTickable(new Ticker());
  }

  @Override
  protected void registerCapacities() {
    this.getStorage().setCapacity(ExperienceKeyType.TYPE, (size.isAdvanced() ? 4 : 1) * 16000);
  }

  @Override
  protected @Nullable ConfigInventory createConfig() {
    return null;
  }

  @Override
  protected IConfigManager createConfigManager() {
    return IConfigManager.builder(this::onConfigChanged).registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL).build();
  }

  @Override
  protected ConfigInventory createStorage() {
    return ConfigInventory
        .storage(size.getSlots())
        .supportedType(ExperienceKeyType.TYPE)
        .slotFilter(this::isAllowedInStorageSlot)
        .changeListener(this::onStorageChanged)
        .build();
  }

  @Override
  protected IUpgradeInventory createUpgrades() {
    return UpgradeInventories.forMachine(getBlockState().getBlock().asItem(), size.isInput() ? 6 : 4, this::onUpgradesChanged);
  }

  private void onConfigChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private boolean isAllowedInStorageSlot(int slot, AEKey what) {
    return what instanceof AEExperienceKey;
  }

  private void onUpgradesChanged() {
    this.saveChanges();
    if (!this.getUpgrades().isInstalled(AEItems.CRAFTING_CARD)) {
      this.cancelCrafting();
    }

    this.updatePlan();
  }

  protected abstract BasicExperienceHandler createInventory();

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
