package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputChemicalHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputChemicalHatchEntity;
import es.degrassi.mmreborn.mekanism.common.machine.component.ChemicalComponent;
import mekanism.api.chemical.BasicChemicalTank;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEChemicalHatchBlock extends MEBlock<ChemicalComponent, BasicChemicalTank> {
  public MEChemicalHatchBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return size.isInput()
        ? new MEInputChemicalHatchEntity(pos, state, size)
        : new MEOutputChemicalHatchEntity(pos, state, size);
  }
}
