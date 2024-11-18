package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.item.MEItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistration {
  public static final DeferredRegister.Items ITEMS =
      DeferredRegister.createItems(ModularMachineryRebornEnergistics.MODID);

  public static final DeferredItem<MEItem> ME_INPUT_BUS =
      ITEMS.register(MEHatchSize.ME_INPUT_BUS.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_INPUT_BUS.get(), MEHatchSize.ME_INPUT_BUS));
  public static final DeferredItem<MEItem> ME_OUTPUT_BUS =
      ITEMS.register(MEHatchSize.ME_OUTPUT_BUS.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_OUTPUT_BUS.get(), MEHatchSize.ME_OUTPUT_BUS));
  public static final DeferredItem<MEItem> ME_ADVANCED_INPUT_BUS =
      ITEMS.register(MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_INPUT_BUS.get(), MEHatchSize.ME_ADVANCED_INPUT_BUS));
  public static final DeferredItem<MEItem> ME_ADVANCED_OUTPUT_BUS =
      ITEMS.register(MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_OUTPUT_BUS.get(), MEHatchSize.ME_ADVANCED_OUTPUT_BUS));


  public static void register(final IEventBus bus) {
    ITEMS.register(bus);
  }
}
