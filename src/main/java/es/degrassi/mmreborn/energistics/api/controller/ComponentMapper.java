package es.degrassi.mmreborn.energistics.api.controller;

import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface ComponentMapper {
  Map<BlockPos, MachineComponent<?>> mmre$getFoundComponentsMap();
}
