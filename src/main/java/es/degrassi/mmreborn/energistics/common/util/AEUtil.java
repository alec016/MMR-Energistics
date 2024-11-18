package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AEUtil {

  public static @Nullable GenericStack fromFluidStack(FluidStack stack) {
    if (stack == null || stack.isEmpty()) return null;
    var key = AEFluidKey.of(stack);
    return new GenericStack(key, stack.getAmount());
  }

  public static FluidStack toFluidStack(GenericStack stack) {
    var key = stack.what();
    if (key instanceof AEFluidKey fluidKey) {
      return toFluidStack(fluidKey, stack.amount());
    }
    return FluidStack.EMPTY;
  }

  public static FluidStack toFluidStack(AEFluidKey key, long amount) {
    return key.toStack((int) amount);
  }

  public static ItemStack[] toItemStacks(GenericStack stack) {
    var key = stack.what();
    if (key instanceof AEItemKey itemKey) {
      return toItemStacks(itemKey, stack.amount());
    }
    return new ItemStack[0];
  }

  public static ItemStack[] toItemStacks(AEItemKey key, long amount) {
    var ints = split(amount);
    var itemStacks = new ItemStack[ints.length];
    for (int i = 0; i < ints.length; i++) {
      itemStacks[i] = key.toStack(ints[i]);
    }
    return itemStacks;
  }

  public static boolean matches(AEFluidKey key, FluidStack stack) {
    return !stack.isEmpty() && key.getFluid().isSame(stack.getFluid()) &&
        Objects.equals(key.toStack(1).getComponents(), stack.getComponents());
  }

  public static int[] split(long value) {
    IntArrayList result = new IntArrayList();
    while (value > 0) {
      int intValue = (int) Math.min(value, Integer.MAX_VALUE);
      result.add(intValue);
      value -= intValue;
    }
    return result.toIntArray();
  }
}
