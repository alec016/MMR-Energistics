package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.stacks.AEKey;
import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuHostLocator;
import com.google.common.collect.ImmutableSet;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEFluidHatchEntity extends MEEntity {
  public MEFluidHatchEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
  }

  protected void syncME() {
    saveChanges();
  }

  @Nullable
  public MachineComponent provideComponent() {
    return new MachineComponent.FluidHatch(getSize().isInput() ? IOType.INPUT : IOType.OUTPUT) {
      public HybridTank getContainerProvider() {
//        return FluidGridConnectedMachine.this.getAeHandler();
        return null;
      }
    };
  }


  @Override
  public ImmutableSet<ICraftingLink> getRequestedJobs() {
    return null;
  }

  @Override
  public long insertCraftedItems(ICraftingLink iCraftingLink, AEKey aeKey, long l, Actionable actionable) {
    return 0;
  }

  @Override
  public void jobStateChange(ICraftingLink iCraftingLink) {

  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if(getLevel().isClientSide()) return;
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {

  }

  @Override
  public InterfaceLogic getInterfaceLogic() {
    return null;
  }
}
