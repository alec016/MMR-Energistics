package es.degrassi.mmreborn.energistics.mixin;

import appeng.parts.CableBusContainer;
import appeng.parts.CableBusStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CableBusContainer.class)
public interface CableContainerAccessor {
  @Accessor
  CableBusStorage getStorage();
}
