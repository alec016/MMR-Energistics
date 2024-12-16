package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEPatternBus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MEPatternBusBlock extends MEBlock {
  public MEPatternBusBlock(MEHatchSize size) {
    super(size);
  }


  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new MEPatternBus(pos, state, size);
  }

  @Override
  protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

    var be = this.getBlockEntity(level, pos);
    if (be instanceof MEPatternBus bus) {
      bus.getLogic().updateRedstoneState();
    }
  }
}
