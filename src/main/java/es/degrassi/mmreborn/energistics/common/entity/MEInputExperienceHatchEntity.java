package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ExperienceHatch;
import es.degrassi.mmreborn.energistics.client.container.MEInputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEExperienceHatchEntity;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

  public ExperienceHatch provideComponent() {
    return new ExperienceHatch(IOType.INPUT) {
      public IExperienceHandler getContainerProvider() {
        return inventory;
      }
    };
  }

  protected BasicExperienceTank createInventory() {
    return new BasicExperienceTank(storage.getCapacity(ExperienceKeyType.TYPE), null) {
      @Override
      public long receiveExperience(long toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) return 0;
        return storage.insert(0, ExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
      }

      @Override
      public long extractExperience(long toExtract, boolean simulate) {
        if (!canExtract() || toExtract <= 0) return 0;
        return storage.extract(0, ExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
      }

      @Override
      public void setExperience(long source) {
        GenericStack stack = storage.getStack(0);
        if (stack == null) stack = new GenericStack(ExperienceKey.KEY, source);
        else stack = new GenericStack(stack.what(), source);
        storage.setStack(0, stack);
      }

      @Override
      public long getExperience() {
        GenericStack stack = storage.getStack(0);
        if (stack == null) return 0;
        return (int) stack.amount();
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
        return storage.insert(0, ExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
      }

      @Override
      public long extractExperienceRecipe(long toExtract, boolean simulate) {
        if (toExtract <= 0) return 0;
        return storage.extract(0, ExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
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
