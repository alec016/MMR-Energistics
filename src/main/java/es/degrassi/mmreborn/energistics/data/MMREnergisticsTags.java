package es.degrassi.mmreborn.energistics.data;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Locale;

public class MMREnergisticsTags {
  private static TagKey<Block> blockTag(String name, boolean isNeoForge) {
    return BlockTags.create(isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) : ModularMachineryRebornEnergistics.rl(name));
  }

  private static TagKey<Item> itemTag(String name, boolean isNeoForge) {
    return ItemTags.create(isNeoForge ? ResourceLocation.fromNamespaceAndPath("c", name) : ModularMachineryRebornEnergistics.rl(name));
  }

  public static List<Pair<TagKey<?>, String>> getAllTags() {
    return Tag.tags.stream().filter(pair -> pair.getFirst().location().getNamespace().equals(ModularMachineryRebornEnergistics.MODID)).toList();
  }

  private static class Tag<T> {
    private static final List<Pair<TagKey<?>, String>> tags = Lists.newArrayList();
    private final TagKey<T> tag;

    protected Tag(TagKey<T> tag, String engTranslation) {
      this.tag = tag;
      tags.add(Pair.of(tag, engTranslation));
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
      this(isNeoForge, name, capitalize(name));
    }

    private Blocks(boolean isNeoForge, String name, String enTranslation) {
      super(blockTag(name, isNeoForge), enTranslation);
    }
  }

  public static class Items extends Tag<Item> {
    public static final TagKey<Item> ME_PATTERN = new Items(false, "me_pattern_bus").get();

    public static final TagKey<Item> ME_BUS = new Items(false, "me_itembus").get();
    public static final TagKey<Item> ME_INPUT_BUS = new Items(false, "me_iteminputbus").get();
    public static final TagKey<Item> ME_OUTPUT_BUS = new Items(false, "me_itemoutputbus").get();

    public static final TagKey<Item> ME_FLUID = new Items(false, "me_fluidhatch").get();
    public static final TagKey<Item> ME_FLUID_INPUT = new Items(false, "me_fluidinputhatch").get();
    public static final TagKey<Item> ME_FLUID_OUTPUT = new Items(false, "me_fluidoutputhatch").get();

    public static final TagKey<Item> ME_EXPERIENCE = new Items(false, "me_experiencehatch").get();
    public static final TagKey<Item> ME_EXPERIENCE_INPUT = new Items(false, "me_experienceinputhatch").get();
    public static final TagKey<Item> ME_EXPERIENCE_OUTPUT = new Items(false, "me_experienceoutputhatch").get();

    public static final TagKey<Item> ME_CHEMICAL = new Items(false, "me_chemical_hatch").get();
    public static final TagKey<Item> ME_CHEMICAL_INPUT = new Items(false, "me_chemical_inputhatch").get();
    public static final TagKey<Item> ME_CHEMICAL_OUTPUT = new Items(false, "me_chemical_outputhatch").get();

    public static final TagKey<Item> ME_SOURCE = new Items(false, "me_source_hatch").get();
    public static final TagKey<Item> ME_SOURCE_INPUT = new Items(false, "me_source_inputhatch").get();
    public static final TagKey<Item> ME_SOURCE_OUTPUT = new Items(false, "me_source_outputhatch").get();

    private Items(boolean isNeoForge, String name) {
      this(isNeoForge, name, capitalize(name));
    }
    private Items(boolean isNeoForge, String name, String enTranslation) {
      super(itemTag(name, isNeoForge), enTranslation);
    }
  }

  private static String capitalize(String toCapitalize) {
    if (toCapitalize.trim().isEmpty()) return toCapitalize;
    String[] splitted = toCapitalize.split("_");
    StringBuilder builder = new StringBuilder();
    for (var part : splitted) {
      if (part.trim().isEmpty()) continue;
      if (part.trim().length() == 1) {
        builder.append(part.trim().toUpperCase(Locale.ENGLISH))
            .append(" ");
        continue;
      }
      String first = (part.trim().charAt(0) + "").toUpperCase(Locale.ENGLISH);
      builder.append(first)
          .append(part.substring(1))
          .append(" ");
    }
    return builder.toString().trim();
  }
}
