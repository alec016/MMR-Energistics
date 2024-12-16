package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.api.controller.ComponentMapper;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MachineControllerEntity.class)
public abstract class MachineControllerEntityMixin extends BlockEntityRestrictedTick implements ComponentMapper {

  public MachineControllerEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/entity/base/MachineComponentEntity;provideComponent()Les/degrassi/mmreborn/common/machine/MachineComponent;"), method = "updateComponents")
  public MachineComponent<?> updateComponents(MachineComponentEntity instance) {
    if (instance instanceof ControllerAccessible accessible) {
      accessible.setControllerPos(getBlockPos());
    }
    return instance.provideComponent();
  }
}
