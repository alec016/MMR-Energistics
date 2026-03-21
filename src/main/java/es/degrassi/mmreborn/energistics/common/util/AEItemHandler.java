package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.util.ConfigInventory;
import com.google.common.primitives.Ints;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import es.degrassi.mmreborn.common.manager.handler.slot.ItemSlot;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AEItemHandler extends ItemHandler {
  private final ConfigInventory storage;

  public AEItemHandler(ConfigInventory storage, int[] outSlots, int[] inSlots, Direction... sides) {
    super(inSlots, outSlots, (int) storage.getCapacity(AEKeyType.items()), sides);
    this.storage = storage;
  }

  protected List<ItemSlot> generateInventory(Predicate<ItemStack> filter) {
    List<ItemSlot> inventory = new ArrayList<>();
    for (Integer slot : inSlots) {
      var itemSlot = createSlot(slot, getSlotLimit(slot), getSlotLimit(slot), 0, filter);
      this.getInputs().add(itemSlot);
      inventory.add(itemSlot);
    }
    for (Integer slot : outSlots) {
      var itemSlot = createSlot(slot, getSlotLimit(slot), 0, getSlotLimit(slot), filter);
      this.getOutputs().add(itemSlot);
      inventory.add(itemSlot);
    }
    return inventory;
  }

  protected ItemSlot createSlot(int finalI, int capacity, int maxIn, int maxOut, Predicate<ItemStack> filter) {
    MMRLogger.INSTANCE.debug("[AEItemHandler] creating slot {}", finalI);
    return new ItemSlot(finalI, this, capacity, maxIn, maxOut, filter) {
      @Override
      public ItemStack getStackInSlot(int slot) {
        return getItemStack();
      }

      @Override
      public ItemStack getItemStack() {
        var stack = storage.getStack(finalI);
        if (stack == null) return ItemStack.EMPTY;
        if (stack.what() instanceof AEItemKey item) {
          return item.toStack(Ints.saturatedCast(stack.amount()));
        }
        return ItemStack.EMPTY;
      }

      @Override
      public void setItemStack(ItemStack stack) {
        storage.setStack(finalI, GenericStack.fromItemStack(stack));
        setChanged();
      }

      @Override
      public boolean isEmpty() {
        var stack = storage.getStack(finalI);
        return stack == null || stack.amount() <= 0 || !(stack.what() instanceof AEItemKey);
      }

      @Override
      public void setStackInSlot(int slot, ItemStack stack) {
        setItemStack(stack);
      }

      public ItemStack insertItemBypassLimit(ItemStack stack, boolean simulate) {
        this.bypassLimit = true;
        ItemStack remainder = insertItem(0, stack, simulate);
        this.bypassLimit = false;
        return remainder;
      }

      public ItemStack extractItemBypassLimit(int amount, boolean simulate) {
        this.bypassLimit = true;
        ItemStack extracted = extractItem(0, amount, simulate);
        this.bypassLimit = false;
        return extracted;
      }

      @Override
      public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) return ItemStack.EMPTY;
        var generic = GenericStack.fromItemStack(stack);
        if (generic == null || !(generic.what() instanceof AEItemKey item) || item.toStack().isEmpty())
          return ItemStack.EMPTY;
        var inserted = storage.insert(finalI, generic.what(), generic.amount(), Actionable.ofSimulate(simulate));
        if (!simulate) setChanged();
        return stack.copyWithCount(stack.getCount() - Ints.saturatedCast(inserted));
      }

      @Override
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
        var item = getItemStack();

        MMRLogger.INSTANCE.debug("[AEItemHandler] Extracting...");
        if (amount < 0 || item.isEmpty() || !this.canOutput())
          return ItemStack.EMPTY;
        MMRLogger.INSTANCE.debug("[AEItemHandler] Trying to extract {}x {}", amount, item.getDisplayName());
        //Check output limit
        if (!this.bypassLimit)
          amount = Math.min(amount, this.maxOutput);
        //Check current stack size
        amount = Math.min(amount, Ints.saturatedCast(storage.getAmount(finalI)));
        MMRLogger.INSTANCE.debug("[AEItemHandler] Extracting {}x {}", amount, item.getDisplayName());

        var extracted = storage.extract(finalI, AEUtil.aeKey(item), amount, Actionable.ofSimulate(simulate));
        if (!simulate) setChanged();
        MMRLogger.INSTANCE.debug("[AEItemHandler] Extracted(%s) {}x {}", simulate ? "simulated" : "executed", extracted, item.getDisplayName());
        return item.copyWithCount(Ints.saturatedCast(extracted));
      }

      @Override
      public boolean isItemValid(int slot, ItemStack stack) {
        return super.isItemValid(slot, stack) && storage.isAllowedIn(finalI, AEItemKey.of(stack));
      }

      @Override
      public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag nbt = super.serializeNBT(registries);
        var stack = storage.getStack(finalI);
        if (stack != null && stack.what() instanceof AEItemKey item && !item.toStack().isEmpty())
          nbt.put("AEItem", stack.what().toTag(registries));
        return nbt;
      }
    };
  }

  public ItemSlot createSlot(HolderLookup.Provider pRegistries, CompoundTag nbt) {
    return createSlot(nbt.getInt("slot"), nbt.getInt("capacity"), nbt.getInt("maxInput"), nbt.getInt("maxOutput"), getDefaultFilter());
  }
}
