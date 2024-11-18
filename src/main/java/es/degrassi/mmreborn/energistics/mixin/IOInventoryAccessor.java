package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.InventoryUpdateListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IOInventory.class)
public interface IOInventoryAccessor {

  @Accessor
  InventoryUpdateListener getListener();
}
