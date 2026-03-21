package es.degrassi.mmreborn.energistics.common.block;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.common.machine.component.ExperienceComponent;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputExperienceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputExperienceHatchEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEExperienceHatchBlock extends MEBlock<ExperienceComponent, IExperienceHandler> {
  public MEExperienceHatchBlock(MEHatchSize size) {
    super(size);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return size.isInput()
        ? new MEInputExperienceHatchEntity(pos, state, size)
        : new MEOutputExperienceHatchEntity(pos, state, size);
  }
}
