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

  @Redirect(method = "canStartCrafting", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common" +
      "/capability/SourceStorage;extractSource(IZ)I", ordinal = 1))
  public int extract(SourceStorage instance, int toExtract, boolean simulate) {
    return instance.extractSource(toExtract, true);
  }

  @Redirect(method = "canStartCrafting", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common" +
      "/capability/SourceStorage;receiveSource(IZ)I", ordinal = 1))
  public int receive(SourceStorage instance, int toReceive, boolean simulate) {
    return instance.receiveSource(toReceive, true);
  }

  @Redirect(method = "canStartCrafting", at = @At(value = "INVOKE", target = "Les/degrassi/mmreborn/ars/common/util/CopyHandlerHelper;copyTank(Lcom/hollingsworth/arsnouveau/common/capability/SourceStorage;Lnet/minecraft/core/RegistryAccess;)Lcom/hollingsworth/arsnouveau/common/capability/SourceStorage;"))
  public SourceStorage copyTank(SourceStorage handler, RegistryAccess registryAccess) {
    if (handler instanceof AESourceHolder holder) {
      // TODO: add copy holder
      return null;
    }
    return CopyHandlerHelper.copyTank(handler, registryAccess);
  }
}
