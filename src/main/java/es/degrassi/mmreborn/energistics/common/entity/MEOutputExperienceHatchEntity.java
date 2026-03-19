package es.degrassi.mmreborn.energistics.common.entity;

import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ExperienceComponent;
import es.degrassi.mmreborn.energistics.client.container.MEOutputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEExperienceHatchEntity;
import es.degrassi.mmreborn.energistics.common.util.AEExperienceHolder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEOutputExperienceHatchEntity extends MEExperienceHatchEntity {

  public MEOutputExperienceHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
  }

  public BasicExperienceTank copyTank() {
    return createInventory();
  }

  @Nullable
  public ExperienceComponent provideComponent() {
    return new ExperienceComponent(new es.degrassi.experiencelib.impl.capability.BasicExperienceHandler(1, inventory.getExperienceCapacity(), null) {
      @Override public void setExperience(int tank, long amount) { inventory.setExperience(amount); }
      @Override public long receiveExperience(int tank, long amount, boolean simulate) { return inventory.receiveExperience(amount, simulate); }
      @Override public long extractExperience(int tank, long amount, boolean simulate) { return inventory.extractExperience(amount, simulate); }
      @Override public long getExperience() { return inventory.getExperience(); }
    }, IOType.OUTPUT);
  }

  protected BasicExperienceTank createInventory() {
    return new AEExperienceHolder(this);
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEOutputExperienceHatchContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEOutputExperienceHatchContainer.TYPE, player, locator);
    }
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEOutputExperienceHatchContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEOutputExperienceHatchContainer.TYPE, player, subMenu.getLocator());
  }
}
