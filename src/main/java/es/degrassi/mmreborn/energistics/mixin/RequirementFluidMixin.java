package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.CopyHandlerHelper;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.energistics.common.util.AETankHolder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RequirementFluid.class)
public abstract class RequirementFluidMixin extends ComponentRequirement<FluidStack, RequirementFluid> implements ComponentRequirement.ChancedRequirement {
  public RequirementFluidMixin(IOType actionType, PositionedRequirement position) {
    super(RequirementTypeRegistration.FLUID.get(), actionType, position);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/CopyHandlerHelper;copyTank(Les/degrassi/mmreborn/common/util/HybridTank;Lnet/minecraft/core/HolderLookup$Provider;)Les/degrassi/mmreborn/common/util/HybridTank;"), method = "canStartCrafting")
  public HybridTank copyTank(HybridTank inventory, HolderLookup.Provider pRegistries) {
    if (inventory instanceof AETankHolder holder) {
      return holder.getOwner().copyTank();
    }
    return CopyHandlerHelper.copyTank(inventory, pRegistries);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/common/util/HybridTank;fill" +
      "(Lnet/neoforged/neoforge/fluids/FluidStack;" +
      "Lnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)I", ordinal = 1), method =
      "canStartCrafting")
  public int fill(HybridTank instance, FluidStack fluidStack, IFluidHandler.FluidAction fluidAction) {
    if (instance instanceof AETankHolder holder) {
      return holder.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
    }
    return instance.fill(fluidStack, fluidAction);
  }
}
