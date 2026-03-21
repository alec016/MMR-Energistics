package es.degrassi.mmreborn.energistics.common.block;

import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import es.degrassi.mmreborn.ars.common.machine.component.SourceComponent;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputSourceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MESourceHatchBlock extends MEBlock<SourceComponent, SourceStorage> {
  public MESourceHatchBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return size.isInput()
        ? new MEInputSourceHatchEntity(pos, state, size)
        : new MEOutputSourceHatchEntity(pos, state, size);
  }
}
