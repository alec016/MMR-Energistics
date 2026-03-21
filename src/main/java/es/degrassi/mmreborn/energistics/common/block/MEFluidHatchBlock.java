package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.common.machine.component.FluidComponent;
import es.degrassi.mmreborn.common.manager.handler.FluidHandler;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputHatchEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEFluidHatchBlock extends MEBlock<FluidComponent, FluidHandler> {
  public MEFluidHatchBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return size.isInput()
        ? new MEInputHatchEntity(pos, state, size)
        : new MEOutputHatchEntity(pos, state, size);
  }
}
