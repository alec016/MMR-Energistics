package es.degrassi.mmreborn.energistics.mixin;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.energistics.common.util.AEChemicalTankHolder;
import es.degrassi.mmreborn.mekanism.common.crafting.requirement.RequirementChemical;
import es.degrassi.mmreborn.mekanism.common.machine.ChemicalHatch;
import es.degrassi.mmreborn.mekanism.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.mekanism.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.mekanism.common.util.CopyHandlerHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RequirementChemical.class)
public abstract class RequirementChemicalMixin extends ComponentRequirement<ChemicalStack, RequirementChemical> implements ComponentRequirement.ChancedRequirement {
  @Shadow
  @Final
  public ChemicalStack required;

  public RequirementChemicalMixin(IOType actionType, PositionedRequirement position) {
    super(RequirementTypeRegistration.CHEMICAL.get(), actionType, position);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/mekanism/common/util/CopyHandlerHelper;copyTank(Lmekanism/api/chemical/BasicChemicalTank;Lnet/minecraft/core/HolderLookup$Provider;)Lmekanism/api/chemical/BasicChemicalTank;"), method = "canStartCrafting")
  public BasicChemicalTank copyTank(BasicChemicalTank inventory, HolderLookup.Provider pRegistries) {
    if (inventory instanceof AEChemicalTankHolder holder) {
      return holder.getOwner().copyTank();
    }
    return CopyHandlerHelper.copyTank(inventory, pRegistries);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/BasicChemicalTank;extract" +
      "(JLmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/chemical/ChemicalStack;", ordinal = 1),
      method = "startCrafting")
  public ChemicalStack extract(BasicChemicalTank instance, long amount, Action action, AutomationType automationType) {
    return instance.extract(amount, Action.SIMULATE, automationType);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/BasicChemicalTank;insert" +
      "(Lmekanism/api/chemical/ChemicalStack;Lmekanism/api/Action;Lmekanism/api/AutomationType;)" +
      "Lmekanism/api/chemical/ChemicalStack;", ordinal = 1), method = "canStartCrafting")
  public ChemicalStack insert(BasicChemicalTank instance, ChemicalStack stack, Action action, AutomationType automationType) {
    if (instance instanceof AEChemicalTankHolder holder) {
      return holder.insert(stack, Action.SIMULATE, automationType);
    }
    return instance.insert(stack, action, automationType);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/BasicChemicalTank;getStack()Lmekanism/api/chemical/ChemicalStack;"), method = "canStartCrafting")
  public ChemicalStack getStack(BasicChemicalTank instance) {
    for (int i = 0; i < instance.getChemicalTanks(); i++) {
      if (ChemicalStack.isSameChemical(instance.getChemicalInTank(i), required))
        return instance.getChemicalInTank(i);
    }
    return ChemicalStack.EMPTY;
  }

  /**
   * @author Alec_016GG
   * @reason needed for AE2 compatibility
   */
  @Overwrite
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    return cmp.getComponentType().equals(ComponentRegistration.COMPONENT_CHEMICAL.get()) &&
        cmp instanceof ChemicalHatch hatch &&
        hatch.getIOType() == this.getActionType() &&
        mme$containsChemical(hatch.getContainerProvider(), getActionType().isInput());
  }

  @Unique
  private boolean mme$containsChemical(BasicChemicalTank tank, boolean input) {
    for (int i = 0; i < tank.getChemicalTanks(); i++) {
      if (ChemicalStack.isSameChemical(tank.getChemicalInTank(i), this.required)) {
        if (input) {
          if (tank.getChemicalInTank(i).getAmount() >= required.getAmount())
            return true;
        } else {
          if (tank.getChemicalTankCapacity(i) - tank.getChemicalInTank(i).getAmount() >= required.getAmount())
            return true;
        }
      } else if (tank.getChemicalInTank(i).isEmpty() && tank.getChemicalTankCapacity(i) >= required.getAmount())
        return true;
    }
    return false;
  }
}
