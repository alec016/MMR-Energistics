package es.degrassi.mmreborn.energistics.client.integration.athena;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelAttributes;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.models.TintProvider;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.impl.client.models.ctm.ConnectedTextureMap;
import es.degrassi.mmreborn.client.integration.athena.model.MMRAthenaBlockModel;
import es.degrassi.mmreborn.client.integration.athena.utils.CtmUtils;
import es.degrassi.mmreborn.client.integration.athena.utils.MMRAthenaQuad;
import es.degrassi.mmreborn.common.block.BlockMachineComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import static es.degrassi.mmreborn.client.integration.athena.utils.CtmUtils.blockMat;

public class MEHatchBlockModel implements MMRAthenaBlockModel {
  public static final AthenaModelFactory FACTORY = new MEHatchBlockModel.Factory();
  final ConnectedTextureMap materials;
  private final BiPredicate<BlockState, BlockState> connectTo;
  @Getter
  private final AthenaModelAttributes attributes;

  public MEHatchBlockModel(ConnectedTextureMap materials, BiPredicate<BlockState, BlockState> connectTo,
                         AthenaModelAttributes attributes) {
    this.materials = materials;
    this.connectTo = connectTo;
    this.attributes = attributes;
  }

  @Override
  public List<MMRAthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState state,
                                      BlockPos pos, Direction direction, Void ignored) {

    if (CtmUtils.checkRelative(level, state, pos, direction)) return List.of();

    final CtmState ctm = CtmState.from(level, state, pos, direction, CtmUtils.check(level, state, pos, direction,
        (other, self) -> connectTo.test(other, self) && other.getValue(BlockMachineComponent.CONNECT_TEXTURES) && self.getValue(BlockMachineComponent.CONNECT_TEXTURES)));

    if (ctm.allTrue()) return List.of(MMRAthenaQuad.withSprite(materials.getTexture(direction, 1), 4));

    List<MMRAthenaQuad> quads = Lists.newArrayList();

    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.up(), ctm.left(), ctm.upLeft(), 0, 0.5f, 1f, 0.5f, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.up(), ctm.right(), ctm.upRight(), 0.5f, 1f, 1f, 0.5f, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.down(), ctm.left(), ctm.downLeft(), 0, 0.5f, 0.5f, 0f, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, materials, direction, ctm.down(), ctm.right(), ctm.downRight(), 0.5f, 1f, 0.5f, 0f, 4));

    if (
        materials.getTexture(direction, 6) != 0 &&
            materials.getTexture(direction, 7) != 0 &&
            materials.getTexture(direction, 8) != 0 &&
            materials.getTexture(direction, 9) != 0
    ) {
      var quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.up(), ctm.left(), ctm.upLeft(), 0, 0.5f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.up(), ctm.right(), ctm.upRight(), 0.5f, 1f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.down(), ctm.left(), ctm.downLeft(), 0, 0.5f, 0.5f, 0f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, ctm.down(), ctm.right(), ctm.downRight(), 0.5f, 1f, 0.5f, 0f, -1);
      quads.add(quad);
    }

    return quads;
  }

  public Map<Direction, List<MMRAthenaQuad>> getDefaultQuads(Direction direction, Void ignored) {
    if (direction == null) return Map.of();
    Map<Direction, List<MMRAthenaQuad>> defaultQuads = Maps.newHashMap();

    var quads = defaultQuads.computeIfAbsent(direction, key -> Lists.newArrayList());

    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.0F, 0.5F, 1.0F, 0.5F, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.5F, 1.0F, 1.0F, 0.5F, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.0F, 0.5F, 0.5F, 0.0F, 4));
    quads.add(CtmUtils.athenaQuadWithState(0, this.materials, direction, false, false, false, 0.5F, 1.0F, 0.5F, 0.0F, 4));

    if (
        materials.getTexture(direction, 6) != 0 &&
            materials.getTexture(direction, 7) != 0 &&
            materials.getTexture(direction, 8) != 0 &&
            materials.getTexture(direction, 9) != 0
    ) {
      var quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0, 0.5f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0.5f, 1f, 1f, 0.5f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0, 0.5f, 0.5f, 0f, -1);
      quads.add(quad);

      quad = CtmUtils.athenaQuadWithState(5, materials, direction, false, false, false, 0.5f, 1f, 0.5f, 0f, -1);
      quads.add(quad);
    }

    return defaultQuads;
  }

  @Override
  public Int2ObjectMap<TextureAtlasSprite> getTextures(Function<Material, TextureAtlasSprite> function) {
    return this.materials.getTextures(function);
  }

  public static class Factory implements AthenaModelFactory {
    private Factory() {
    }

    public Supplier<AthenaBlockModel> create(JsonObject json) {
      ConnectedTextureMap materials = CtmUtils.tryParse(GsonHelper.getAsJsonObject(json, "ctm_textures"), MEHatchBlockModel.Factory::parseDefaultMaterials);
      if (materials == null) {
        materials = CtmUtils.tryParse(GsonHelper.getAsJsonObject(json, "ctm_textures"), MEHatchBlockModel.Factory::parseMaterials);
      }

      if (materials == null) {
        throw new JsonSyntaxException("Expected either ctm_textures to have 5-6 entries for all textures or have " +
            "directional textures for each direction or to have some directions and a default textures object.");
      } else {
        BiPredicate<BlockState, BlockState> conditions = CtmUtils.parseCondition(json);
        ConnectedTextureMap finalMaterials = materials;
        return () -> new MEHatchBlockModel(finalMaterials, conditions, new AthenaModelAttributes(new TintProvider.Index(4), RenderType.CUTOUT));
      }
    }

    private static ConnectedTextureMap parseMaterials(JsonObject json) {
      ConnectedTextureMap materials = new ConnectedTextureMap();

      for(Direction direction : Direction.values()) {
        if (GsonHelper.isStringValue(json, direction.getSerializedName())) {
          materials.put(direction, blockMat(GsonHelper.getAsString(json, direction.getSerializedName())));
        } else {
          Int2ObjectMap<Material> directionMaterials = CtmUtils.parseCtmMaterials(GsonHelper.getAsJsonObject(json, direction.getSerializedName(), GsonHelper.getAsJsonObject(json, "default")));
          materials.put(direction, directionMaterials);
        }
      }

      return materials;
    }

    private static ConnectedTextureMap parseDefaultMaterials(JsonObject json) {
      Int2ObjectMap<Material> materials = parseCtmMaterials(json);
      ConnectedTextureMap connectedTextureMap = new ConnectedTextureMap();

      for(Direction direction : Direction.values()) {
        connectedTextureMap.put(direction, materials);
      }

      return connectedTextureMap;
    }

    public static Int2ObjectMap<Material> parseCtmMaterials(JsonObject json) {
      Int2ObjectMap<Material> materials = new Int2ObjectArrayMap<>();

      materials.put(0, blockMat(GsonHelper.getAsString(json, "particle")));
      materials.put(1, blockMat(GsonHelper.getAsString(json, "empty")));
      materials.put(2, blockMat(GsonHelper.getAsString(json, "center")));
      materials.put(3, blockMat(GsonHelper.getAsString(json, "vertical")));
      materials.put(4, blockMat(GsonHelper.getAsString(json, "horizontal")));

      if (json.has("overlay"))
        materials.put(5, blockMat(GsonHelper.getAsString(json, "overlay")));

      if (json.has("ov_particle"))
        materials.put(6, blockMat(GsonHelper.getAsString(json, "ov_particle")));

      if (json.has("ov_center"))
        materials.put(7, blockMat(GsonHelper.getAsString(json, "ov_center")));

      if (json.has("ov_vertical"))
        materials.put(8, blockMat(GsonHelper.getAsString(json, "ov_vertical")));

      if (json.has("ov_horizontal"))
        materials.put(9, blockMat(GsonHelper.getAsString(json, "ov_horizontal")));

      materials.put(11, blockMat(GsonHelper.getAsString(json, "empty_active")));
      materials.put(12, blockMat(GsonHelper.getAsString(json, "center_active")));
      materials.put(13, blockMat(GsonHelper.getAsString(json, "vertical_active")));
      materials.put(14, blockMat(GsonHelper.getAsString(json, "horizontal_active")));

      if (json.has("overlay_active"))
        materials.put(15, blockMat(GsonHelper.getAsString(json, "overlay_active")));

      if (json.has("ov_particle_active"))
        materials.put(16, blockMat(GsonHelper.getAsString(json, "ov_particle_active")));

      if (json.has("ov_center_active"))
        materials.put(17, blockMat(GsonHelper.getAsString(json, "ov_center_active")));

      if (json.has("ov_vertical_active"))
        materials.put(18, blockMat(GsonHelper.getAsString(json, "ov_vertical_active")));

      if (json.has("ov_horizontal_active"))
        materials.put(19, blockMat(GsonHelper.getAsString(json, "ov_horizontal_active")));

      return materials;
    }
  }
}
