package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.util.ConfigInventory;
import es.degrassi.mmreborn.common.manager.handler.FluidHandler;
import es.degrassi.mmreborn.common.manager.handler.slot.HybridTank;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AEFluidHandler extends FluidHandler {
  private final ConfigInventory storage;

  public AEFluidHandler(ConfigInventory storage, int[] inSlots, int[] outSlots) {
    super(inSlots, outSlots, (int) storage.getCapacity(AEKeyType.fluids()));
    this.storage = storage;
  }

  @Override
  protected List<HybridTank> generateInventory(Predicate<FluidStack> filter) {
    List<HybridTank> inventory = new ArrayList<>();
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

  protected HybridTank createSlot(int finalI, int capacity, int maxIn, int maxOut, Predicate<FluidStack> filter) {
    return new HybridTank(
        finalI,
        this,
        capacity,
        maxIn,
        maxOut,
        filter
    ) {
      @Override
      public boolean isFluidValid(FluidStack fluidStack) {
        return storage.isAllowedIn(finalI, AEFluidKey.of(fluidStack));
      }

      @Override
      public boolean isFluidValid(int i, FluidStack fluidStack) {
        if (i > 0) return false;
        return storage.isAllowedIn(finalI, AEFluidKey.of(fluidStack));
      }

      @Override
      public int getTankCapacity(int i) {
        return (int) storage.getCapacity(AEKeyType.fluids());
      }

      @Override
      public FluidStack getFluidInTank(int i) {
        if (i > 0) return FluidStack.EMPTY;
        return getFluid();
      }

      @Override
      public FluidStack getFluid() {
        GenericStack possibleFluid = storage.getStack(finalI);
        if (possibleFluid != null && possibleFluid.what() instanceof AEFluidKey fKey) {
          return fKey.toStack((int) possibleFluid.amount());
        }
        return FluidStack.EMPTY;
      }

      @Override
      public int fill(FluidStack resource, FluidAction action) {
        if (!resource.isEmpty() && this.isFluidValid(resource)) {
          return (int) storage.insert(finalI, AEFluidKey.of(resource), resource.getAmount(), Actionable.of(action));
        } else {
          return 0;
        }
      }

      @Override
      public FluidStack drain(int maxDrain, FluidAction action) {
        var fluid = getFluid();
        var drained = (int) storage.extract(finalI, AEFluidKey.of(fluid.getFluid()), maxDrain, Actionable.of(action));
        if (drained > 0) {
          return fluid.copyWithAmount(drained);
        }
        return FluidStack.EMPTY;
      }

      @Override
      public FluidStack getValue() {
        return getFluid();
      }

      @Override
      public FluidStack drain(FluidStack resource, FluidAction action) {
        return !resource.isEmpty() && FluidStack.isSameFluidSameComponents(resource, this.getFluid()) ?
            this.drain(resource.getAmount(), action) : FluidStack.EMPTY;
      }

      @Override
      public int getSpace() {
        return Math.max(0, this.capacity - this.getFluid().getAmount());
      }
    };
  }

  @Override
  protected HybridTank createSlot(HolderLookup.Provider pRegistries, CompoundTag nbt) {
    return createSlot(nbt.getInt("slot"), nbt.getInt("capacity"), nbt.getInt("maxInput"), nbt.getInt("maxOutput"), getDefaultFilter());
  }
}
