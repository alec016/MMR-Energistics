package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.jei.IJeiRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.CopyHandlerHelper;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.energistics.common.entity.MEInputBusEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputBusEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(RequirementItem.class)
public abstract class RequirementItemMixin extends ComponentRequirement<ItemStack, RequirementItem> implements ComponentRequirement.ChancedRequirement {
  public RequirementItemMixin(IOType actionType, IJeiRequirement.JeiPositionedRequirement position) {
    super(RequirementTypeRegistration.ITEM.get(), actionType, position);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/CopyHandlerHelper;copyInventory(Les/degrassi/mmreborn/common/util/IOInventory;Lnet/minecraft/core/HolderLookup$Provider;)Les/degrassi/mmreborn/common/util/IOInventory;"), method = "canStartCrafting")
  public IOInventory copyInventory(IOInventory inventory, HolderLookup.Provider pRegistries) {
    if (inventory.getOwner() instanceof MEOutputBusEntity entity)
      return entity.copyInventory();
    return CopyHandlerHelper.copyInventory(inventory, pRegistries);
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

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/ItemUtils;tryPlaceItemInInventory(Lnet/minecraft/world/item/ItemStack;Lnet/neoforged/neoforge/items/IItemHandlerModifiable;Z)I"), method = "canStartCrafting")
  public int tryPlaceItemInInventory(ItemStack item, IItemHandlerModifiable handler, boolean simulate) {
    final int start = 0;
    final int end = handler.getSlots();
    ItemStack stack = item.copy();

    if (handler instanceof IOInventory inv) {
      if (inv.getOwner() instanceof MEOutputBusEntity) {
        ItemStack toAdd = stack.copy();
        if (!ItemUtils.hasInventorySpace(toAdd, inv, start, end)) {
          return 0;
        }
        int insertedAmt = 0;
        int max = toAdd.getMaxStackSize();
        for (int i = start; i < end; i++) {
          ItemStack in = inv.getStackInSlot(i);
          if (in.isEmpty()) {
            int added = Math.min(stack.getCount(), max);
            stack.setCount(stack.getCount() - added);
            if(!simulate) {
              inv.setStackInSlot(i, ItemUtils.copyStackWithSize(toAdd, added));
            }
            insertedAmt += added;
            if (stack.getCount() <= 0)
              return insertedAmt;
          } else {
            if (ItemUtils.matchTags(toAdd, in)) {
              int space = max - in.getCount();
              int added = Math.min(stack.getCount(), space);
              insertedAmt += added;
              stack.setCount(stack.getCount() - added);
              if(!simulate) {
                inv.getStackInSlot(i).setCount(inv.getStackInSlot(i).getCount() + added);
              }
              if (stack.getCount() <= 0)
                return insertedAmt;
            }
          }
        }
        return insertedAmt;
      }
    }
    return ItemUtils.tryPlaceItemInInventory(stack, handler, simulate);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/ItemUtils;consumeFromInventory" +
      "(Lnet/neoforged/neoforge/items/IItemHandlerModifiable;Lnet/minecraft/world/item/ItemStack;Z)Z"), method = "startCrafting")
  public boolean consumeFromInventoryStartCrafting(IItemHandlerModifiable handler, ItemStack toConsume, boolean simulate) {
    return consumeFromInventory(handler, toConsume, simulate);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/ItemUtils;tryPlaceItemInInventory(Lnet/minecraft/world/item/ItemStack;Lnet/neoforged/neoforge/items/IItemHandlerModifiable;Z)I"), method = "finishCrafting")
  public int tryPlaceItemInInventoryFinishCrafting(ItemStack stack, IItemHandlerModifiable handler, boolean simulate) {
    return tryPlaceItemInInventory(stack, handler, simulate);
  }
}
