package es.degrassi.mmreborn.energistics.mixin;

import earth.terrarium.athena.api.client.models.FactoryManager;
import es.degrassi.mmreborn.client.integration.athena.MMRAthenaModels;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.client.integration.athena.MEHatchBlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MMRAthenaModels.class)
public abstract class MMRAthenaModelsMixin {

  @Inject(at = @At("HEAD"), method ="init")
  private static void init(CallbackInfo ci) {
    FactoryManager.register(ModularMachineryRebornEnergistics.rl("me_hatch"), MEHatchBlockModel.FACTORY);
  }
}
