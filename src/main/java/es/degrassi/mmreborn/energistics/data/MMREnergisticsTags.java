package es.degrassi.mmreborn.energistics.data;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MMREnergisticsTags {
  private static TagKey<Block> blockTag(String name, boolean isNeoForge) {
    return BlockTags.create(isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) :
        ModularMachineryRebornEnergistics.rl(name));
  }

  private static TagKey<Item> itemTag(String name, boolean isNeoForge) {
    return ItemTags.create(isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) : ModularMachineryRebornEnergistics.rl(name));
  }

  private static class Tag<T> {
    private final TagKey<T> tag;

    protected Tag(TagKey<T> tag) {
      this.tag = tag;
    }

    public TagKey<T> get() {
      return tag;
    }
  }

  public static class Blocks extends Tag<Block> {
    public static final TagKey<Block> ME_PATTERN = new Blocks(false, "me_pattern_bus").get();

    public static final TagKey<Block> ME_BUS = new Blocks(false, "me_itembus").get();
    public static final TagKey<Block> ME_INPUT_BUS = new Blocks(false, "me_iteminputbus").get();
    public static final TagKey<Block> ME_OUTPUT_BUS = new Blocks(false, "me_itemoutputbus").get();

    public static final TagKey<Block> ME_FLUID = new Blocks(false, "me_fluidhatch").get();
    public static final TagKey<Block> ME_FLUID_INPUT = new Blocks(false, "me_fluidinputhatch").get();
    public static final TagKey<Block> ME_FLUID_OUTPUT = new Blocks(false, "me_fluidoutputhatch").get();

    public static final TagKey<Block> ME_EXPERIENCE = new Blocks(false, "me_experiencehatch").get();
    public static final TagKey<Block> ME_EXPERIENCE_INPUT = new Blocks(false, "me_experienceinputhatch").get();
    public static final TagKey<Block> ME_EXPERIENCE_OUTPUT = new Blocks(false, "me_experienceoutputhatch").get();

    public static final TagKey<Block> ME_CHEMICAL = new Blocks(false, "me_chemical_hatch").get();
    public static final TagKey<Block> ME_CHEMICAL_INPUT = new Blocks(false, "me_chemical_inputhatch").get();
    public static final TagKey<Block> ME_CHEMICAL_OUTPUT = new Blocks(false, "me_chemical_outputhatch").get();

    public static final TagKey<Block> ME_SOURCE = new Blocks(false, "me_source_hatch").get();
    public static final TagKey<Block> ME_SOURCE_INPUT = new Blocks(false, "me_source_inputhatch").get();
    public static final TagKey<Block> ME_SOURCE_OUTPUT = new Blocks(false, "me_source_outputhatch").get();

    private Blocks(boolean isNeoForge, String name) {
      super(blockTag(name, isNeoForge));
    }
  }

  public static class Items extends Tag<Item> {
    private Items(boolean isNeoForge, String name) {
      super(itemTag(name, isNeoForge));
    }
  }
}
