package es.degrassi.mmreborn.energistics.mixin;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import es.degrassi.mmreborn.client.integration.athena.model.MMRAthenaUnbakedModel;
import es.degrassi.mmreborn.energistics.client.integration.athena.MEHatchBakedModel;
import es.degrassi.mmreborn.energistics.client.integration.athena.MEHatchBlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(MMRAthenaUnbakedModel.class)
public abstract class MMRAthenaUnbakedModelMixin {
  @Shadow
  @Final
  private Supplier<AthenaBlockModel> model;

  @Inject(at = @At(value = "HEAD"), method = "bake", cancellable = true)
  private void bake(
      @NotNull ModelBaker baker,
      @NotNull Function<Material, TextureAtlasSprite> function,
      @NotNull ModelState modelState,
      CallbackInfoReturnable<BakedModel> cir) {
    if (model.get() instanceof MEHatchBlockModel m) {
      cir.setReturnValue(new MEHatchBakedModel(baker, m, function));
      cir.cancel();
    }
  }
}
