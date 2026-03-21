package es.degrassi.mmreborn.energistics.mixin;

import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.models.neoforge.FactoryManagerImpl;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.integration.athena.MMRAthenaUnbakedModelLoader;
import es.degrassi.mmreborn.client.integration.athena.model.MMRAthenaUnbakedModel;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(FactoryManagerImpl.class)
public abstract class AthenaFactoryManagerMixin {
  @Shadow
  @Final
  private static Map<ResourceLocation, AthenaUnbakedModelLoader> FACTORIES;

  @Inject(
      at = @At(
          value = "INVOKE",
          target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
      ),
      method = "register",
      cancellable = true
  )
  private static void register(ResourceLocation type, AthenaModelFactory factory, CallbackInfo ci) {
    if (type.getNamespace().equals(ModularMachineryRebornEnergistics.MODID)) {
      FACTORIES.put(type, new MMRAthenaUnbakedModelLoader(type, factory, MMRAthenaUnbakedModel::new));
      ci.cancel();
    }
  }
}
