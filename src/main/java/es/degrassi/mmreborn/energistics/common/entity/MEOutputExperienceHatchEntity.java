package es.degrassi.mmreborn.energistics.common.entity;

import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import es.degrassi.experiencelib.impl.capability.BasicExperienceHandler;
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

  @Nullable
  public ExperienceComponent provideComponent() {
    return new ExperienceComponent(getInventory(), IOType.OUTPUT);
  }

  protected BasicExperienceHandler createInventory() {
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
