package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputSourceHatchEntity;
// import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MESourceHatchBlock extends MEBlock {
  public MESourceHatchBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return size.isInput()
        ? new MEInputSourceHatchEntity(pos, state, size)
        : new MEOutputSourceHatchEntity(pos, state, size);
  }
}
