package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.component.FluidHatch;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RequirementFluid.class)
public abstract class RequirementFluidMixin extends ComponentRequirement<FluidStack, RequirementFluid> implements ComponentRequirement.ChancedRequirement {
  @Shadow
  @Final
  public HybridFluid required;

  public RequirementFluidMixin(IOType actionType, PositionedRequirement position) {
    super(RequirementTypeRegistration.FLUID.get(), actionType, position);
  }

  /**
   * @author Alec_016GG
   * @reason needed for AE2 compatibility
   */
  @Overwrite
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    return cmp.getComponentType().equals(ComponentRegistration.COMPONENT_FLUID.get()) &&
        cmp instanceof FluidHatch hatch &&
        hatch.getIOType() == this.getActionType() &&
        mme$containsFluid(hatch.getContainerProvider(), getActionType().isInput());
  }

  @Unique
  private boolean mme$containsFluid(HybridTank tank, boolean input) {
    for (int i = 0; i < tank.getTanks(); i++) {
      if (FluidStack.isSameFluidSameComponents(tank.getFluidInTank(i), this.required.asFluidStack())) {
        if (input) {
          if (tank.getFluidInTank(i).getAmount() >= required.getAmount())
            return true;
        } else {
          if (tank.getTankCapacity(i) - tank.getFluidInTank(i).getAmount() >= required.getAmount())
            return true;
        }
      } else if (tank.getFluidInTank(i).isEmpty() && tank.getTankCapacity(i) >= required.getAmount())
        return true;
    }
    return false;
  }
}
