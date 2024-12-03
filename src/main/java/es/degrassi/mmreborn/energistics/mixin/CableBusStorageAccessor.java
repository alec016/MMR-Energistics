package es.degrassi.mmreborn.energistics.mixin;

import appeng.api.implementations.parts.ICablePart;
import appeng.parts.CableBusStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CableBusStorage.class)
public interface CableBusStorageAccessor {
  @Accessor
  ICablePart getCenter();
}
