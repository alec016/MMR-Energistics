package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.MEFluidHatchBlock;
import es.degrassi.mmreborn.energistics.common.block.MEItemBusBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
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


  public static void register(final IEventBus bus) {
    BLOCKS.register(bus);
  }
}
