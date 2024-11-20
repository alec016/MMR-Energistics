package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import com.google.common.primitives.Ints;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputHatchEntity;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AETankHolder extends HybridTank {
  private final MEOutputHatchEntity owner;
  public AETankHolder(MEOutputHatchEntity owner) {
    super((int) owner.getStorage().getCapacity(AEKeyType.fluids()));
    this.owner = owner;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("AETankHolder{storage:[");

    for (int i = 0; i < owner.getStorage().size(); i++, builder.append(", ")) {
      GenericStack stack = owner.getStorage().getStack(i);
      if (stack != null) {
        builder.append(stack.amount()).append("x ").append(stack.what().getDisplayName().getString());
      } else {
        builder.append("null");
      }
    }
    builder.append("]}");
    return builder.toString();
  }

  @Override
  public int getTanks() {
    return owner.getStorage().size();
  }

  @Override
  public boolean isFluidValid(FluidStack stack) {
    for (int i = 0; i < owner.getStorage().size(); i++) {
      if (owner.getStorage().isAllowedIn(i, AEFluidKey.of(stack)))
        return true;
    }
    return false;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    AEKey var3 = owner.getStorage().getKey(tank);
    if (var3 instanceof AEFluidKey what) {
      int amount = Ints.saturatedCast(owner.getStorage().getAmount(tank));
      return what.toStack(amount);
    } else {
      return FluidStack.EMPTY;
    }
  }

  @Override
  public int getTankCapacity(int tank) {
    return Ints.saturatedCast(owner.getStorage().getCapacity(AEKeyType.fluids()));
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    if (stack.isEmpty()) {
      return true;
    } else {
      AEFluidKey what = AEFluidKey.of(stack);
      return what != null && owner.getStorage().isAllowedIn(tank, what);
    }
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    AEFluidKey what = AEFluidKey.of(resource);
    if (what == null) {
      return 0;
    } else {
      int inserted = 0;

      for (int i = 0; i < owner.getStorage().size() && inserted < resource.getAmount(); ++i) {
        inserted += (int) owner.getStorage().insert(i, what, resource.getAmount() - inserted, Actionable.of(action));
      }

      return inserted;
    }
  }

  private FluidStack extract(AEFluidKey what, int amount, IFluidHandler.FluidAction action) {
    int extracted = 0;

    for (int i = 0; i < owner.getStorage().size() && extracted < amount; ++i) {
      extracted += (int) owner.getStorage().extract(i, what, amount - extracted, Actionable.of(action));
    }

    return extracted > 0 ? what.toStack(extracted) : FluidStack.EMPTY;
  }

  public @NotNull FluidStack drain(FluidStack
                                       resource, IFluidHandler.FluidAction action) {
    AEFluidKey what = AEFluidKey.of(resource);
    return what == null ? FluidStack.EMPTY : this.extract(what, resource.getAmount(), action);
  }

  public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
    for (int i = 0; i < owner.getStorage().size(); ++i) {
      owner.getStorage().getKey(i);
      AEKey var6 = owner.getStorage().getKey(i);
      if (var6 instanceof AEFluidKey fluidKey) {
        return this.extract(fluidKey, maxDrain, action);
      }
    }

    return FluidStack.EMPTY;
  }
}
