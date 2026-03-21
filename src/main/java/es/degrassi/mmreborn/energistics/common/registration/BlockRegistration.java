package es.degrassi.mmreborn.energistics.common.registration;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import com.google.common.base.Preconditions;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEChemicalHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEExperienceHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEFluidHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEItemBusBlock;
import es.degrassi.mmreborn.energistics.common.block.MEPatternBusBlock;
import es.degrassi.mmreborn.energistics.common.block.MESourceHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.item.MEItem;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlockRegistration {
  public static final DeferredRegister.Blocks DR =
      DeferredRegister.createBlocks(ModularMachineryRebornEnergistics.MODID);
  public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

  public static final BlockDefinition<MEItemBusBlock> ME_INPUT_BUS =
      block(MEHatchSize.ME_INPUT_BUS.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_INPUT_BUS.getSerializedName()),
          () -> new MEItemBusBlock(MEHatchSize.ME_INPUT_BUS),
          (block, props) -> new MEItem<>((MEItemBusBlock) block, MEHatchSize.ME_INPUT_BUS));
  public static final BlockDefinition<MEItemBusBlock> ME_OUTPUT_BUS =
      block(MEHatchSize.ME_OUTPUT_BUS.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_OUTPUT_BUS.getSerializedName()),
          () -> new MEItemBusBlock(MEHatchSize.ME_OUTPUT_BUS),
          (block, props) -> new MEItem<>((MEItemBusBlock) block, MEHatchSize.ME_OUTPUT_BUS));
  public static final BlockDefinition<MEItemBusBlock> ME_ADVANCED_INPUT_BUS =
      block(MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName()),
          () -> new MEItemBusBlock(MEHatchSize.ME_ADVANCED_INPUT_BUS),
          (block, props) -> new MEItem<>((MEItemBusBlock) block, MEHatchSize.ME_ADVANCED_INPUT_BUS));
  public static final BlockDefinition<MEItemBusBlock> ME_ADVANCED_OUTPUT_BUS =
      block(MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName()),
          () -> new MEItemBusBlock(MEHatchSize.ME_ADVANCED_OUTPUT_BUS),
          (block, props) -> new MEItem<>((MEItemBusBlock) block, MEHatchSize.ME_ADVANCED_OUTPUT_BUS));

  public static final BlockDefinition<MEFluidHatchBlock> ME_INPUT_HATCH =
      block(MEHatchSize.ME_INPUT_HATCH.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_INPUT_HATCH.getSerializedName()),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_INPUT_HATCH),
          (block, props) -> new MEItem<>((MEFluidHatchBlock) block, MEHatchSize.ME_INPUT_HATCH));
  public static final BlockDefinition<MEFluidHatchBlock> ME_OUTPUT_HATCH =
      block(MEHatchSize.ME_OUTPUT_HATCH.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_OUTPUT_HATCH.getSerializedName()),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_OUTPUT_HATCH),
          (block, props) -> new MEItem<>((MEFluidHatchBlock) block, MEHatchSize.ME_OUTPUT_HATCH));
  public static final BlockDefinition<MEFluidHatchBlock> ME_ADVANCED_INPUT_HATCH =
      block(MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName()),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_HATCH),
          (block, props) -> new MEItem<>((MEFluidHatchBlock) block, MEHatchSize.ME_ADVANCED_INPUT_HATCH));
  public static final BlockDefinition<MEFluidHatchBlock> ME_ADVANCED_OUTPUT_HATCH =
      block(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName()),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH),
          (block, props) -> new MEItem<>((MEFluidHatchBlock) block, MEHatchSize.ME_ADVANCED_OUTPUT_HATCH));

  public static final BlockDefinition<MEPatternBusBlock> ME_PATTERN_BUS =
      block(MEHatchSize.ME_PATTERN_BUS.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_PATTERN_BUS.getSerializedName()),
          () -> new MEPatternBusBlock(MEHatchSize.ME_PATTERN_BUS),
          (block, props) -> new MEItem<>((MEPatternBusBlock) block, MEHatchSize.ME_PATTERN_BUS));
  public static final BlockDefinition<MEPatternBusBlock> ME_ADVANCED_PATTERN_BUS =
      block(MEHatchSize.ME_ADVANCED_PATTERN_BUS.getSerializedName(),
          ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_PATTERN_BUS.getSerializedName()),
          () -> new MEPatternBusBlock(MEHatchSize.ME_ADVANCED_PATTERN_BUS),
          (block, props) -> new MEItem<>((MEPatternBusBlock) block, MEHatchSize.ME_ADVANCED_PATTERN_BUS));

  public static final BlockDefinition<MEChemicalHatchBlock> ME_INPUT_CHEMICAL_HATCH;
  public static final BlockDefinition<MEChemicalHatchBlock> ME_ADVANCED_INPUT_CHEMICAL_HATCH;
  public static final BlockDefinition<MEChemicalHatchBlock> ME_OUTPUT_CHEMICAL_HATCH;
  public static final BlockDefinition<MEChemicalHatchBlock> ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;

  public static final BlockDefinition<MESourceHatchBlock> ME_INPUT_SOURCE_HATCH;
  public static final BlockDefinition<MESourceHatchBlock> ME_ADVANCED_INPUT_SOURCE_HATCH;
  public static final BlockDefinition<MESourceHatchBlock> ME_OUTPUT_SOURCE_HATCH;
  public static final BlockDefinition<MESourceHatchBlock> ME_ADVANCED_OUTPUT_SOURCE_HATCH;

  public static final BlockDefinition<MEExperienceHatchBlock> ME_INPUT_EXPERIENCE_HATCH;
  public static final BlockDefinition<MEExperienceHatchBlock> ME_ADVANCED_INPUT_EXPERIENCE_HATCH;
  public static final BlockDefinition<MEExperienceHatchBlock> ME_OUTPUT_EXPERIENCE_HATCH;
  public static final BlockDefinition<MEExperienceHatchBlock> ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH;

  static {
    if (Mods.isMekPossible()) {
      ME_INPUT_CHEMICAL_HATCH =
          block(MEHatchSize.ME_INPUT_CHEMICAL_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_INPUT_CHEMICAL_HATCH.getSerializedName()),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_INPUT_CHEMICAL_HATCH),
              (block, props) -> new MEItem<>((MEChemicalHatchBlock) block, MEHatchSize.ME_INPUT_CHEMICAL_HATCH));
      ME_ADVANCED_INPUT_CHEMICAL_HATCH =
          block(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH.getSerializedName()),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH),
              (block, props) -> new MEItem<>((MEChemicalHatchBlock) block, MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH));
      ME_OUTPUT_CHEMICAL_HATCH =
          block(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH.getSerializedName()),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH),
              (block, props) -> new MEItem<>((MEChemicalHatchBlock) block, MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH));
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH =
          block(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.getSerializedName()),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH),
              (block, props) -> new MEItem<>((MEChemicalHatchBlock) block, MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH));
    } else {
      ME_INPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = null;
      ME_OUTPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = null;
    }

    if (Mods.isArsPossible()) {
      ME_INPUT_SOURCE_HATCH =
          block(MEHatchSize.ME_INPUT_SOURCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_INPUT_SOURCE_HATCH.getSerializedName()),
              () -> new MESourceHatchBlock(MEHatchSize.ME_INPUT_SOURCE_HATCH),
              (block, props) -> new MEItem<>((MESourceHatchBlock) block, MEHatchSize.ME_INPUT_SOURCE_HATCH));
      ME_ADVANCED_INPUT_SOURCE_HATCH =
          block(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH.getSerializedName()),
              () -> new MESourceHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH),
              (block, props) -> new MEItem<>((MESourceHatchBlock) block, MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH));
      ME_OUTPUT_SOURCE_HATCH =
          block(MEHatchSize.ME_OUTPUT_SOURCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_OUTPUT_SOURCE_HATCH.getSerializedName()),
              () -> new MESourceHatchBlock(MEHatchSize.ME_OUTPUT_SOURCE_HATCH),
              (block, props) -> new MEItem<>((MESourceHatchBlock) block, MEHatchSize.ME_OUTPUT_SOURCE_HATCH));
      ME_ADVANCED_OUTPUT_SOURCE_HATCH =
          block(MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH.getSerializedName()),
              () -> new MESourceHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH),
              (block, props) -> new MEItem<>((MESourceHatchBlock) block, MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH));
    } else {
      ME_INPUT_SOURCE_HATCH = null;
      ME_ADVANCED_INPUT_SOURCE_HATCH = null;
      ME_OUTPUT_SOURCE_HATCH = null;
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = null;
    }

    if (Mods.isExperiencePossible()) {
      ME_INPUT_EXPERIENCE_HATCH =
          block(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH.getSerializedName()),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH),
              (block, props) -> new MEItem<>((MEExperienceHatchBlock) block, MEHatchSize.ME_INPUT_EXPERIENCE_HATCH));
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH =
          block(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.getSerializedName()),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH),
              (block, props) -> new MEItem<>((MEExperienceHatchBlock) block, MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH));
      ME_OUTPUT_EXPERIENCE_HATCH =
          block(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH.getSerializedName()),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH),
              (block, props) -> new MEItem<>((MEExperienceHatchBlock) block, MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH));
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH =
          block(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
              ModularMachineryRebornEnergistics.rl(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.getSerializedName()),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH),
              (block, props) -> new MEItem<>((MEExperienceHatchBlock) block, MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH));
    } else {
      ME_INPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH = null;
      ME_OUTPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH = null;
    }
  }

  private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id,
                                                            Supplier<T> blockSupplier) {
    return block(englishName, id, blockSupplier, null);
  }

  private static <T extends Block> BlockDefinition<T> block(
      String englishName,
      ResourceLocation id,
      Supplier<T> blockSupplier,
      @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
    Preconditions.checkArgument(id.getNamespace().equals(ModularMachineryRebornEnergistics.MODID));

    // Create block and matching item
    var deferredBlock = DR.register(id.getPath(), blockSupplier);
    var deferredItem = ItemRegistration.DR.register(id.getPath(), () -> {
      var block = deferredBlock.get();
      var itemProperties = new Item.Properties();
      if (itemFactory != null) {
        var item = itemFactory.apply(block, itemProperties);
        if (item == null) {
          throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
        }
        return item;
      } else if (block instanceof AEBaseBlock) {
        return new AEBaseBlockItem(block, itemProperties);
      } else {
        return new BlockItem(block, itemProperties);
      }
    });

    var itemDef = new ItemDefinition<>(englishName, deferredItem);
//    MainCreativeTab.add(itemDef);
    BlockDefinition<T> definition = new BlockDefinition<>(englishName, deferredBlock, itemDef);

    BLOCKS.add(definition);

    return definition;

  }

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }
}
