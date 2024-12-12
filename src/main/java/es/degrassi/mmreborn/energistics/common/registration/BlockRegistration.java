package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.MEChemicalHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEExperienceHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEFluidHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEItemBusBlock;
import es.degrassi.mmreborn.energistics.common.block.MESourceHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistration {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModularMachineryRebornEnergistics.MODID);

  public static final DeferredBlock<MEBlock> ME_INPUT_BUS =
      BLOCKS.register(MEHatchSize.ME_INPUT_BUS.getSerializedName(),
          () -> new MEItemBusBlock(MEHatchSize.ME_INPUT_BUS));
  public static final DeferredBlock<MEBlock> ME_OUTPUT_BUS =
      BLOCKS.register(MEHatchSize.ME_OUTPUT_BUS.getSerializedName(),
          () -> new MEItemBusBlock(MEHatchSize.ME_OUTPUT_BUS));
  public static final DeferredBlock<MEBlock> ME_ADVANCED_INPUT_BUS =
      BLOCKS.register(MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName(),
          () -> new MEItemBusBlock(MEHatchSize.ME_ADVANCED_INPUT_BUS));
  public static final DeferredBlock<MEBlock> ME_ADVANCED_OUTPUT_BUS =
      BLOCKS.register(MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName(),
          () -> new MEItemBusBlock(MEHatchSize.ME_ADVANCED_OUTPUT_BUS));

  public static final DeferredBlock<MEBlock> ME_INPUT_HATCH =
      BLOCKS.register(MEHatchSize.ME_INPUT_HATCH.getSerializedName(),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_INPUT_HATCH));
  public static final DeferredBlock<MEBlock> ME_OUTPUT_HATCH =
      BLOCKS.register(MEHatchSize.ME_OUTPUT_HATCH.getSerializedName(),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_OUTPUT_HATCH));
  public static final DeferredBlock<MEBlock> ME_ADVANCED_INPUT_HATCH =
      BLOCKS.register(MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName(),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_HATCH));
  public static final DeferredBlock<MEBlock> ME_ADVANCED_OUTPUT_HATCH =
      BLOCKS.register(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName(),
          () -> new MEFluidHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH));

  public static final DeferredBlock<MEBlock> ME_INPUT_CHEMICAL_HATCH;
  public static final DeferredBlock<MEBlock> ME_ADVANCED_INPUT_CHEMICAL_HATCH;
  public static final DeferredBlock<MEBlock> ME_OUTPUT_CHEMICAL_HATCH;
  public static final DeferredBlock<MEBlock> ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;

  public static final DeferredBlock<MEBlock> ME_INPUT_SOURCE_HATCH;
  public static final DeferredBlock<MEBlock> ME_ADVANCED_INPUT_SOURCE_HATCH;
  public static final DeferredBlock<MEBlock> ME_OUTPUT_SOURCE_HATCH;
  public static final DeferredBlock<MEBlock> ME_ADVANCED_OUTPUT_SOURCE_HATCH;

  public static final DeferredBlock<MEBlock> ME_INPUT_EXPERIENCE_HATCH;
  public static final DeferredBlock<MEBlock> ME_ADVANCED_INPUT_EXPERIENCE_HATCH;
  public static final DeferredBlock<MEBlock> ME_OUTPUT_EXPERIENCE_HATCH;
  public static final DeferredBlock<MEBlock> ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH;

  static {
    if (Mods.isMekPossible()) {
      ME_INPUT_CHEMICAL_HATCH =
          BLOCKS.register(MEHatchSize.ME_INPUT_CHEMICAL_HATCH.getSerializedName(),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_INPUT_CHEMICAL_HATCH));
      ME_ADVANCED_INPUT_CHEMICAL_HATCH =
          BLOCKS.register(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH.getSerializedName(),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH));
      ME_OUTPUT_CHEMICAL_HATCH =
          BLOCKS.register(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH));
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH =
          BLOCKS.register(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
              () -> new MEChemicalHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH));
    } else {
      ME_INPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = null;
      ME_OUTPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = null;
    }

    if (Mods.isArsPossible()) {
      ME_INPUT_SOURCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_INPUT_SOURCE_HATCH.getSerializedName(),
              () -> new MESourceHatchBlock(MEHatchSize.ME_INPUT_SOURCE_HATCH));
      ME_ADVANCED_INPUT_SOURCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH.getSerializedName(),
              () -> new MESourceHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH));
      ME_OUTPUT_SOURCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_OUTPUT_SOURCE_HATCH.getSerializedName(),
              () -> new MESourceHatchBlock(MEHatchSize.ME_OUTPUT_SOURCE_HATCH));
      ME_ADVANCED_OUTPUT_SOURCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH.getSerializedName(),
              () -> new MESourceHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH));
    } else {
      ME_INPUT_SOURCE_HATCH = null;
      ME_ADVANCED_INPUT_SOURCE_HATCH = null;
      ME_OUTPUT_SOURCE_HATCH = null;
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = null;
    }

    if (Mods.isExperiencePossible()) {
      ME_INPUT_EXPERIENCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH.getSerializedName(),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH));
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.getSerializedName(),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH));
      ME_OUTPUT_EXPERIENCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH));
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH =
          BLOCKS.register(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
              () -> new MEExperienceHatchBlock(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH));
    } else {
      ME_INPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH = null;
      ME_OUTPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH = null;
    }
  }


  public static void register(final IEventBus bus) {
    BLOCKS.register(bus);
  }
}
