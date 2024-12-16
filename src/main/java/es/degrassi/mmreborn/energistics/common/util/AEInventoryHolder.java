package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputBusEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AEInventoryHolder extends IOInventory {
  private final MEOutputBusEntity owner;

  public AEInventoryHolder(MEOutputBusEntity entity, int[] outSlots, int[] inSlots, Direction... sides) {
    super(entity, inSlots, outSlots, sides);
    this.owner = entity;
  }

  public AEInventoryHolder(MEOutputBusEntity entity, int[] outSlots, int[] inSlots) {
    super(entity, outSlots, inSlots);
    this.owner = entity;
  }

  @Override
  public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) return ItemStack.EMPTY;
    long inserted = owner.getStorage().insert(slot, AEItemKey.of(stack), stack.getCount(), simulate ? Actionable.SIMULATE :
        Actionable.MODULATE);
    if (inserted > owner.getStorage().getCapacity(AEKeyType.items()))
      return stack.copyWithCount(stack.getCount() - (int) owner.getStorage().getCapacity(AEKeyType.items()));
    return stack.copyWithCount(stack.getCount() - (int) inserted);
  }

  @Override
  public int getSlots() {
    return owner.getStorage().size();
  }

  @Override
  public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
    long extracted = owner.getStorage().extract(slot, AEItemKey.of(getStackInSlot(slot)), amount, simulate ?
        Actionable.SIMULATE : Actionable.MODULATE);
    return getStackInSlot(slot).copyWithCount((int) extracted);
  }

  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    GenericStack stack = owner.getStorage().getStack(slot);
    if (stack == null) return ItemStack.EMPTY;
    if (AEItemKey.is(stack.what())) {
      AEItemKey key = (AEItemKey) stack.what();
      return key.toStack((int) stack.amount());
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void setStackInSlot(int slot, @NotNull ItemStack stack) {
    owner.getStorage().setStack(slot, new GenericStack(AEItemKey.of(stack), stack.getCount()));
  }
}
