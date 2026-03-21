package es.degrassi.mmreborn.energistics.data.blockstate;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.data.MMRTags;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.IGeneratedBlockState;
import org.jetbrains.annotations.NotNull;

public class StateMEHatchBuilder implements IGeneratedBlockState {
  private TagKey<Block> connectTo;

  private ResourceLocation center;
  private ResourceLocation empty;
  private ResourceLocation horizontal;
  private ResourceLocation particle;
  private ResourceLocation vertical;
  private ResourceLocation overlay = null;
  private ResourceLocation ovCenter = null;
  private ResourceLocation ovVertical = null;
  private ResourceLocation ovHorizontal = null;
  private ResourceLocation ovParticle = null;
  
  private ResourceLocation center_active;
  private ResourceLocation empty_active;
  private ResourceLocation horizontal_active;
  private ResourceLocation vertical_active;
  private ResourceLocation overlay_active = null;
  private ResourceLocation ovCenter_active = null;
  private ResourceLocation ovVertical_active = null;
  private ResourceLocation ovHorizontal_active = null;
  private ResourceLocation ovParticle_active = null;
  
  protected ResourceLocation loader() {
    return ModularMachineryRebornEnergistics.rl("me_hatch");
  }

  @Override
  public @NotNull JsonObject toJson() throws IllegalArgumentException {
    JsonObject blockState = new JsonObject();
    JsonObject variants = new JsonObject();
    JsonObject empty = new JsonObject();
    empty.addProperty("model", "minecraft:block/air");
    variants.add("", empty);
    blockState.add("variants", variants);
    blockState.addProperty("athena:loader", loader().toString());
    addCtmTextures(blockState);
    addConnectTo(blockState);
    return blockState;
  }

  protected void addCtmTextures(JsonObject blockState) {
    if (center == null) throw new NullTextureException("Center");
    if (empty == null) throw new NullTextureException("Empty");
    if (horizontal == null) throw new NullTextureException("Horizontal");
    if (particle == null) throw new NullTextureException("Particle");
    if (vertical == null) throw new NullTextureException("Vertical");

    if (center_active == null) throw new NullTextureException("CenterActive");
    if (empty_active == null) throw new NullTextureException("EmptyActive");
    if (horizontal_active == null) throw new NullTextureException("HorizontalActive");
    if (vertical_active == null) throw new NullTextureException("VerticalActive");
    JsonObject ctmTextures = new JsonObject();
    ctmTextures.addProperty("center", center.toString());
    ctmTextures.addProperty("empty", empty.toString());
    ctmTextures.addProperty("horizontal", horizontal.toString());
    ctmTextures.addProperty("particle", particle.toString());
    ctmTextures.addProperty("vertical", vertical.toString());
    if (overlay != null)
      ctmTextures.addProperty("overlay", overlay.toString());
    if (ovCenter != null)
      ctmTextures.addProperty("ov_center", ovCenter.toString());
    if (ovVertical != null)
      ctmTextures.addProperty("ov_vertical", ovVertical.toString());
    if (ovHorizontal != null)
      ctmTextures.addProperty("ov_horizontal", ovHorizontal.toString());
    if (ovParticle != null)
      ctmTextures.addProperty("ov_particle", ovParticle.toString());
    ctmTextures.addProperty("center_active", center_active.toString());
    ctmTextures.addProperty("empty_active", empty_active.toString());
    ctmTextures.addProperty("horizontal_active", horizontal_active.toString());
    ctmTextures.addProperty("vertical_active", vertical_active.toString());
    if (overlay_active != null)
      ctmTextures.addProperty("overlay_active", overlay_active.toString());
    if (ovCenter_active != null)
      ctmTextures.addProperty("ov_center_active", ovCenter_active.toString());
    if (ovVertical_active != null)
      ctmTextures.addProperty("ov_vertical_active", ovVertical_active.toString());
    if (ovHorizontal_active != null)
      ctmTextures.addProperty("ov_horizontal_active", ovHorizontal_active.toString());
    if (ovParticle_active != null)
      ctmTextures.addProperty("ov_particle_active", ovParticle_active.toString());
    blockState.add("ctm_textures", ctmTextures);
  }

  protected void addConnectTo(JsonObject blockState) {
    if (connectTo == null) throw new IllegalArgumentException("Connect to can not be null");
    JsonObject tag = new JsonObject();
    tag.addProperty("type", "tag");
    tag.addProperty("tag", connectTo());
    blockState.add("connect_to", tag);
  }

  protected @NotNull String connectTo() {
    return connectTo.location().toString();
  }

  protected StateMEHatchBuilder self() {
    return this;
  }

  public StateMEHatchBuilder connectToPlain() {
    connectTo = MMRTags.Blocks.PLAIN_CONNECTABLE;
    return self();
  }

  public StateMEHatchBuilder connectToReinforced() {
    connectTo = MMRTags.Blocks.REINFORCED_CONNECTABLE;
    return self();
  }

  public StateMEHatchBuilder center(ResourceLocation center) {
    this.center = center;
    return self();
  }

  public StateMEHatchBuilder empty(ResourceLocation empty) {
    this.empty = empty;
    return self();
  }

  public StateMEHatchBuilder horizontal(ResourceLocation horizontal) {
    this.horizontal = horizontal;
    return self();
  }

  public StateMEHatchBuilder particle(ResourceLocation particle) {
    this.particle = particle;
    return self();
  }

  public StateMEHatchBuilder vertical(ResourceLocation vertical) {
    this.vertical = vertical;
    return self();
  }

  public StateMEHatchBuilder ovCenter(ResourceLocation ovCenter) {
    this.ovCenter = ovCenter;
    return self();
  }

  public StateMEHatchBuilder ovVertical(ResourceLocation ovVertical) {
    this.ovVertical = ovVertical;
    return self();
  }

  public StateMEHatchBuilder ovHorizontal(ResourceLocation ovHorizontal) {
    this.ovHorizontal = ovHorizontal;
    return self();
  }

  public StateMEHatchBuilder ovParticle(ResourceLocation ovParticle) {
    this.ovParticle = ovParticle;
    return self();
  }

  public StateMEHatchBuilder overlay(ResourceLocation overlay) {
    this.overlay = overlay;
    return self();
  }

  public StateMEHatchBuilder centerActive(ResourceLocation center) {
    this.center_active = center;
    return self();
  }

  public StateMEHatchBuilder emptyActive(ResourceLocation empty) {
    this.empty_active = empty;
    return self();
  }

  public StateMEHatchBuilder horizontalActive(ResourceLocation horizontal) {
    this.horizontal_active = horizontal;
    return self();
  }

  public StateMEHatchBuilder verticalActive(ResourceLocation vertical) {
    this.vertical_active = vertical;
    return self();
  }

  public StateMEHatchBuilder ovCenterActive(ResourceLocation ovCenter) {
    this.ovCenter_active = ovCenter;
    return self();
  }

  public StateMEHatchBuilder ovVerticalActive(ResourceLocation ovVertical) {
    this.ovVertical_active = ovVertical;
    return self();
  }

  public StateMEHatchBuilder ovHorizontalActive(ResourceLocation ovHorizontal) {
    this.ovHorizontal_active = ovHorizontal;
    return self();
  }

  public StateMEHatchBuilder ovParticleActive(ResourceLocation ovParticle) {
    this.ovParticle_active = ovParticle;
    return self();
  }

  public StateMEHatchBuilder overlayActive(ResourceLocation overlay) {
    this.overlay_active = overlay;
    return self();
  }

  protected static class NullTextureException extends IllegalArgumentException {
    private NullTextureException(String texture) {
      super(texture + " texture can not be null");
    }
  }
}
