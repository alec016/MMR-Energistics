package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputBusEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputBusEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEItemBusBlock extends MEBlock {
  public MEItemBusBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return size.isInput()
        ? new MEInputBusEntity(pos, state, size)
        : new MEOutputBusEntity(pos, state, size);
  }
}
