package es.degrassi.mmreborn.energistics.client.integration.athena;

import earth.terrarium.athena.api.client.neoforge.WrappedGetter;
import earth.terrarium.athena.api.client.utils.NullableEnumMap;
import es.degrassi.mmreborn.client.integration.athena.model.MMRBakedModel;
import es.degrassi.mmreborn.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmreborn.client.integration.athena.utils.MMRAthenaQuad;
import es.degrassi.mmreborn.client.model.hatch.DefaultHatchBakedModel;
import es.degrassi.mmreborn.common.block.BlockMachineComponent;
import es.degrassi.mmreborn.common.util.MMRLogger;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class MEHatchBakedModel implements MMRBakedModel {

  private static final Direction[] DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};

  public static final ModelProperty<NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>>> DATA = new ModelProperty<>();
  public static final ModelProperty<HatchTextureData> TEXTURE_DATA = new ModelProperty<>();

  private final MEHatchBlockModel model;
  @Getter
  private final Int2ObjectMap<TextureAtlasSprite> textures;

  private final ModelBaker baker;
  private final Function<Material, TextureAtlasSprite> spriteGetter;

  public MEHatchBakedModel(ModelBaker baker, MEHatchBlockModel model, Function<Material, TextureAtlasSprite> function) {
    this.model = model;
    this.textures = this.model.getTextures(function);
    this.baker = baker;
    this.spriteGetter = function;
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction side,
                                           @NotNull RandomSource randomSource, @NotNull ModelData data, @Nullable RenderType renderType) {
    try {
      var textureData = data.get(TEXTURE_DATA);
      if ((blockState != null && !blockState.getValue(BlockMachineComponent.CONNECT_TEXTURES)) || (textureData != null && !textureData.hasDefaultTextures())) {
        DefaultHatchBakedModel defaultHatchBakedModel = new DefaultHatchBakedModel(baker, spriteGetter);
        assert textureData != null;
        return defaultHatchBakedModel.getQuads(
            blockState,
            side,
            randomSource,
            ModelData
                .builder()
                .with(DefaultHatchBakedModel.MODEL, textureData.defaultModel())
                .with(DefaultHatchBakedModel.BASE_TEXTURE_NAME, textureData.baseTextureName())
                .with(DefaultHatchBakedModel.OVERLAY_TEXTURE_NAME, textureData.overlayTextureName())
                .with(DefaultHatchBakedModel.BASE_TEXTURE, textureData.baseTexture())
                .with(DefaultHatchBakedModel.OVERLAY_TEXTURE, textureData.overlayTexture())
                .build(),
            renderType
        );
      }
      List<BakedQuad> quads = new ArrayList<>();
      Map<Direction, List<MMRAthenaQuad>> values = data.has(DATA) ?
          Objects.requireNonNull(data.get(DATA)).getOrDefault(side, Map.of()) :
          this.model.getDefaultQuads(side, null);
      var index = blockState.getValue(MEBlock.ACTIVE) ? 15 : 5;
      values.forEach((dir, quadList) -> {
        quads.addAll(bakeQuads(quadList, dir));
        int texture = this.model.materials.getTexture(dir, index);
        if (texture != 0) {
          if (blockState.hasProperty(BlockStateProperties.FACING)) {
            var facing = blockState.getValue(BlockStateProperties.FACING);
            if (facing == dir) {
              quads.addAll(bakeQuads(List.of(MMRAthenaQuad.withSprite(texture, -0.0001F, -1)), dir));
            }
          } else {
            quads.addAll(bakeQuads(List.of(MMRAthenaQuad.withSprite(texture, -0.0001F, -1)), dir));
          }
        }
      });
      return quads;
    } catch (Exception e) {
      MMRLogger.INSTANCE.error("Error occurred while getting quads of Hatch Athena block model", e);
      throw e;
    }
  }

  @Override
  public @NotNull ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
    var textureData = data.get(TEXTURE_DATA);
    if (textureData != null && !textureData.hasDefaultTextures()) return data.derive().build();
    WrappedGetter getter = new WrappedGetter(level);
    final NullableEnumMap<Direction, Map<Direction, List<MMRAthenaQuad>>> quads = new NullableEnumMap<>(Direction.class);
    Map<Direction, List<MMRAthenaQuad>> nonCullQuads = new HashMap<>();
    for (Direction direction : DIRECTIONS) {
      List<MMRAthenaQuad> culledQuads = new ArrayList<>();
      List<MMRAthenaQuad> unculledQuads = new ArrayList<>();
      for (MMRAthenaQuad quad : this.model.getQuads(getter, state, pos, direction, null)) {
        if (quad.cull()) {
          culledQuads.add(quad);
        } else {
          unculledQuads.add(quad);
        }
      }
      quads.put(direction, Map.of(direction, culledQuads));
      nonCullQuads.put(direction, unculledQuads);
    }
    quads.put(null, nonCullQuads);
    return data.derive().with(DATA, quads).build();
  }
}
