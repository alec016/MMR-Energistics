package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputChemicalHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputChemicalHatchEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEChemicalHatchBlock extends MEBlock {
  public MEChemicalHatchBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return size.isInput()
        ? new MEInputChemicalHatchEntity(pos, state, size)
        : new MEOutputChemicalHatchEntity(pos, state, size);
  }
}
