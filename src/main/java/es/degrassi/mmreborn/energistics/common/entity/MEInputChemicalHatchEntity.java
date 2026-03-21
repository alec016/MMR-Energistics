package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
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
import es.degrassi.mmreborn.energistics.client.container.MEInputChemicalHatchContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.mekanism.common.machine.component.ChemicalComponent;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEInputChemicalHatchEntity extends MEEntity<ChemicalComponent, BasicChemicalTank> {

  public MEInputChemicalHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
    craftingRequester();
    gridTickable(new Ticker());
  }

  @Override
  protected void registerCapacities() {
    this.getConfig().setCapacity(MekanismKeyType.TYPE, (size.isAdvanced() ? 4 : 1) * 16000);
    this.getStorage().setCapacity(MekanismKeyType.TYPE, (size.isAdvanced() ? 4 : 1) * 16000);
  }

  @Override
  protected @Nullable IConfigManager createConfigManager() {
    return IConfigManager.builder(this::onConfigChanged).registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL).build();
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
    return ConfigInventory
        .configStacks(size.getSlots())
        .supportedType(MekanismKeyType.TYPE)
        .changeListener(this::onConfigRowChanged)
        .build();
  }

  @Override
  protected @Nullable IUpgradeInventory createUpgrades() {
    return UpgradeInventories.forMachine(getBlockState().getBlock().asItem(), 6, this::onUpgradesChanged);
  }

  @Nullable
  public ChemicalComponent provideComponent() {
    return new ChemicalComponent(getInventory(), IOType.INPUT);
  }

  private void onConfigChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private boolean isAllowedInStorageSlot(int slot, AEKey what) {
    if (slot >= this.getConfig().size()) {
      return true;
    } else {
      AEKey configured = this.getConfig().getKey(slot);
      return configured == null || configured.equals(what);
    }
  }

  private void onInventoryChanged() {
    this.saveChanges();
    this.updatePlan();
  }

  private void onConfigRowChanged() {
    this.saveChanges();
    this.readConfig();
  }

  private void onUpgradesChanged() {
    this.saveChanges();
    if (!this.getUpgrades().isInstalled(AEItems.CRAFTING_CARD)) {
      this.cancelCrafting();
    }

    this.updatePlan();
  }

  @SuppressWarnings("removal")
  protected BasicChemicalTank createInventory() {
    return new BasicChemicalTank(
        16000,
        ((chemical, automationType) -> getSize().isInput() || automationType == AutomationType.INTERNAL),
        ((chemical, automationType) -> !getSize().isInput() || automationType == AutomationType.INTERNAL),
        ConstantPredicates.alwaysTrue(),
        ChemicalAttributeValidator.ALWAYS_ALLOW,
        null
    ) {
      private static Actionable actionableFromAction(Action action) {
        return switch (action) {
          case SIMULATE -> Actionable.SIMULATE;
          case EXECUTE -> Actionable.MODULATE;
        };
      }

      @Override
      public int getChemicalTanks() {
        return getStorage().size();
      }

      @Override
      public boolean isValid(ChemicalStack stack) {
        for (int i = 0; i < getStorage().size(); i++) {
          if (getStorage().isAllowedIn(i, MekanismKey.of(stack)))
            return true;
        }
        return false;
      }

      @Override
      public ChemicalStack getChemicalInTank(int tank) {
        if (tank < 0 || tank >= getChemicalTanks()) {
          return ChemicalStack.EMPTY;
        }
        GenericStack stack = getStorage().getStack(tank);
        if (stack == null) return ChemicalStack.EMPTY;
        return ((MekanismKey) stack.what()).getStack();
      }

      @Override
      public long getChemicalTankCapacity(int tank) {
        return getStorage().getCapacity(MekanismKeyType.TYPE);
      }

      public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        getStorage().readFromChildTag(nbt, "storage", lookupProvider);
      }

      @Override
      public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        getStorage().writeToChildTag(nbt, "storage", lookupProvider);
        return nbt;
      }

      @Override
      public boolean isValid(int tank, ChemicalStack stack) {
        return getStorage().isAllowedIn(tank, MekanismKey.of(stack));
      }

      @Override
      public ChemicalStack insert(ChemicalStack resource, Action action, AutomationType automationType) {
        if (resource == null || resource.isEmpty()) return ChemicalStack.EMPTY;
        return resource.copyWithAmount(getStorage().insert(MekanismKey.of(resource), resource.getAmount(), actionableFromAction(action), actionSource));
      }

      @Override
      public ChemicalStack extract(long amount, Action action, AutomationType automationType) {
        if (amount <= 0) return ChemicalStack.EMPTY;
        for (AEKey key : getStorage().getAvailableStacks().keySet()) {
          if (key instanceof MekanismKey mkey) {
            long extracted = getStorage().extract(mkey, amount, actionableFromAction(action), actionSource);
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
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEInputChemicalHatchContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEInputChemicalHatchContainer.TYPE, player, subMenu.getLocator());
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
