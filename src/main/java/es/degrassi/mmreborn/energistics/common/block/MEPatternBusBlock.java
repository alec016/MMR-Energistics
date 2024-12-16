package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEPatternBus;
import net.minecraft.core.BlockPos;
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
}
