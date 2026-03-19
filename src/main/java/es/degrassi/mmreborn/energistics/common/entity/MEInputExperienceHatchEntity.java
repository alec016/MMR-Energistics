package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ExperienceComponent;
import es.degrassi.mmreborn.energistics.client.container.MEInputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEExperienceHatchEntity;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEInputExperienceHatchEntity extends MEExperienceHatchEntity {

  public MEInputExperienceHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
  }

  public ExperienceComponent provideComponent() {
    return new ExperienceComponent(new es.degrassi.experiencelib.impl.capability.BasicExperienceHandler(1, inventory.getExperienceCapacity(), null) {
      @Override public void setExperience(int tank, long amount) { inventory.setExperience(amount); }
      @Override public long receiveExperience(int tank, long amount, boolean simulate) { return inventory.receiveExperience(amount, simulate); }
      @Override public long extractExperience(int tank, long amount, boolean simulate) { return inventory.extractExperience(amount, simulate); }
      @Override public long getExperience() { return inventory.getExperience(); }
    }, IOType.INPUT);
  }

  protected BasicExperienceTank createInventory() {
    return new BasicExperienceTank(storage.getCapacity(ExperienceKeyType.TYPE), null) {
      @Override
      public long receiveExperience(long toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) return 0;
        return storage.insert(0, AEExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
      }

      @Override
      public long extractExperience(long toExtract, boolean simulate) {
        if (!canExtract() || toExtract <= 0) return 0;
        return storage.extract(0, AEExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
      }

      @Override
      public void setExperience(long source) {
        GenericStack stack = storage.getStack(0);
        if (stack == null) stack = new GenericStack(AEExperienceKey.KEY, source);
        else stack = new GenericStack(stack.what(), source);
        storage.setStack(0, stack);
      }

      @Override
      public long getExperience() {
        GenericStack stack = storage.getStack(0);
        if (stack == null) return 0;
        return stack.amount();
      }

      public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        super.deserializeNBT(lookupProvider, tag);
        storage.readFromChildTag(tag, "storage", lookupProvider);
      }

      @Override
      public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = super.serializeNBT(lookupProvider);
        storage.writeToChildTag(nbt, "storage", lookupProvider);
        return nbt;
      }

      @Override
      public long receiveExperienceRecipe(long toReceive, boolean simulate) {
        if (toReceive <= 0) return 0;
        return storage.insert(0, AEExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
      }

      @Override
      public long extractExperienceRecipe(long toExtract, boolean simulate) {
        if (toExtract <= 0) return 0;
        return storage.extract(0, AEExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
      }
    };
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEInputExperienceHatchContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEInputExperienceHatchContainer.TYPE, player, locator);
    }
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEInputExperienceHatchContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEInputExperienceHatchContainer.TYPE, player, subMenu.getLocator());
  }
}
