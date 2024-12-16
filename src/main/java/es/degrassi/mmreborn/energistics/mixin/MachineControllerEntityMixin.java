package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.energistics.api.controller.ComponentMapper;
import es.degrassi.mmreborn.energistics.common.entity.base.ControllerAccessible;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(MachineControllerEntity.class)
public abstract class MachineControllerEntityMixin extends BlockEntityRestrictedTick implements ComponentMapper {
  @Shadow public abstract DynamicMachine getFoundMachine();

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

  @Override
  public Map<BlockPos, MachineComponent<?>> mmre$getFoundComponentsMap() {
    Map<BlockPos, MachineComponent<?>> map = new LinkedHashMap<>();
    for(BlockPos potentialPosition : getFoundMachine().getPattern().getBlocks(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet()) {
      BlockPos realPos = this.getBlockPos().offset(potentialPosition);
      BlockEntity te = this.getLevel().getBlockEntity(realPos);
      if (te instanceof MachineComponentEntity entity) {
        MachineComponent<?> component = entity.provideComponent();
        if (component != null) {
          map.put(realPos, component);
        }
      }
    }
    return map;
  }
}
