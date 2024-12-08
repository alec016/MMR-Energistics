package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.energistics.common.entity.MEInputBusEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(RequirementItem.class)
public abstract class RequirementItemMixin extends ComponentRequirement<ItemStack, RequirementItem> implements ComponentRequirement.ChancedRequirement {
  public RequirementItemMixin(IOType actionType, PositionedRequirement position) {
    super(RequirementTypeRegistration.ITEM.get(), actionType, position);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/ItemUtils;consumeFromInventory(Lnet/neoforged/neoforge/items/IItemHandlerModifiable;Lnet/minecraft/world/item/ItemStack;Z)Z"), method = "canStartCrafting")
  public boolean consumeFromInventory(IItemHandlerModifiable handler, ItemStack toConsume, boolean simulate) {
    Map<Integer, ItemStack> contents = ItemUtils.findItemsIndexedInInventory(handler, toConsume, true);
    int cAmt = toConsume.getCount();
    if (handler instanceof IOInventory inv) {
      if (inv.getOwner() instanceof MEInputBusEntity) {
        for (int slot : contents.keySet()) {
          ItemStack inSlot = contents.get(slot);
          int toRemove = Math.min(cAmt, inSlot.getCount());
          cAmt -= toRemove;
          if (!simulate) {
            inv.extractItem(slot, toRemove, false);
          }
          if (cAmt <= 0) return true;
        }
      }
    }
    return ItemUtils.consumeFromInventory(handler, toConsume, simulate);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/ItemUtils;consumeFromInventory" +
      "(Lnet/neoforged/neoforge/items/IItemHandlerModifiable;Lnet/minecraft/world/item/ItemStack;Z)Z"), method = "startCrafting")
  public boolean consumeFromInventoryStartCrafting(IItemHandlerModifiable handler, ItemStack toConsume, boolean simulate) {
    return consumeFromInventory(handler, toConsume, simulate);
  }
}
