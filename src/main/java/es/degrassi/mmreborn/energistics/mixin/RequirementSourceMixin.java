package es.degrassi.mmreborn.energistics.mixin;

import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import es.degrassi.mmreborn.ars.common.crafting.requirement.RequirementSource;
import es.degrassi.mmreborn.ars.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.ars.common.util.CopyHandlerHelper;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.energistics.common.util.AESourceHolder;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RequirementSource.class)
public abstract class RequirementSourceMixin extends ComponentRequirement<Integer, RequirementSource> implements ComponentRequirement.ChancedRequirement  {
  public RequirementSourceMixin(IOType actionType, PositionedRequirement position) {
    super(RequirementTypeRegistration.SOURCE.get(), actionType, position);
  }
}
