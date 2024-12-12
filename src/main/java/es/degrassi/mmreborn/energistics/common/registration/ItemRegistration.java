package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.item.MEItem;
import es.degrassi.mmreborn.energistics.common.util.Mods;
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

  public static final DeferredItem<MEItem> ME_INPUT_HATCH =
      ITEMS.register(MEHatchSize.ME_INPUT_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_INPUT_HATCH.get(), MEHatchSize.ME_INPUT_HATCH));
  public static final DeferredItem<MEItem> ME_OUTPUT_HATCH =
      ITEMS.register(MEHatchSize.ME_OUTPUT_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_OUTPUT_HATCH.get(), MEHatchSize.ME_OUTPUT_HATCH));
  public static final DeferredItem<MEItem> ME_ADVANCED_INPUT_HATCH =
      ITEMS.register(MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_INPUT_HATCH.get(), MEHatchSize.ME_ADVANCED_INPUT_HATCH));
  public static final DeferredItem<MEItem> ME_ADVANCED_OUTPUT_HATCH =
      ITEMS.register(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.get(), MEHatchSize.ME_ADVANCED_OUTPUT_HATCH));

  public static final DeferredItem<MEItem> ME_INPUT_CHEMICAL_HATCH;
  public static final DeferredItem<MEItem> ME_ADVANCED_INPUT_CHEMICAL_HATCH;
  public static final DeferredItem<MEItem> ME_OUTPUT_CHEMICAL_HATCH;
  public static final DeferredItem<MEItem> ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;

  public static final DeferredItem<MEItem> ME_INPUT_SOURCE_HATCH;
  public static final DeferredItem<MEItem> ME_ADVANCED_INPUT_SOURCE_HATCH;
  public static final DeferredItem<MEItem> ME_OUTPUT_SOURCE_HATCH;
  public static final DeferredItem<MEItem> ME_ADVANCED_OUTPUT_SOURCE_HATCH;

  public static final DeferredItem<MEItem> ME_INPUT_EXPERIENCE_HATCH;
  public static final DeferredItem<MEItem> ME_ADVANCED_INPUT_EXPERIENCE_HATCH;
  public static final DeferredItem<MEItem> ME_OUTPUT_EXPERIENCE_HATCH;
  public static final DeferredItem<MEItem> ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH;

  static {
    if (Mods.isMekPossible()) {
      ME_INPUT_CHEMICAL_HATCH = ITEMS.register(MEHatchSize.ME_INPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_INPUT_CHEMICAL_HATCH.get(), MEHatchSize.ME_INPUT_CHEMICAL_HATCH));
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = ITEMS.register(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get(), MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH));
      ME_OUTPUT_CHEMICAL_HATCH = ITEMS.register(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH.get(), MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH));
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = ITEMS.register(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.get(), MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH));
    } else {
      ME_INPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = null;
      ME_OUTPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = null;
    }

    if (Mods.isArsPossible()) {
      ME_INPUT_SOURCE_HATCH = ITEMS.register(MEHatchSize.ME_INPUT_SOURCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_INPUT_SOURCE_HATCH.get(), MEHatchSize.ME_INPUT_SOURCE_HATCH));
      ME_ADVANCED_INPUT_SOURCE_HATCH = ITEMS.register(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get(), MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH));
      ME_OUTPUT_SOURCE_HATCH = ITEMS.register(MEHatchSize.ME_OUTPUT_SOURCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_OUTPUT_SOURCE_HATCH.get(), MEHatchSize.ME_OUTPUT_SOURCE_HATCH));
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = ITEMS.register(MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.get(), MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH));
    } else {
      ME_INPUT_SOURCE_HATCH = null;
      ME_ADVANCED_INPUT_SOURCE_HATCH = null;
      ME_OUTPUT_SOURCE_HATCH = null;
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = null;
    }

    if (Mods.isExperiencePossible()) {
      ME_INPUT_EXPERIENCE_HATCH = ITEMS.register(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_INPUT_EXPERIENCE_HATCH.get(), MEHatchSize.ME_INPUT_EXPERIENCE_HATCH));
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH = ITEMS.register(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.get(), MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH));
      ME_OUTPUT_EXPERIENCE_HATCH = ITEMS.register(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH.get(), MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH));
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH = ITEMS.register(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
          () -> new MEItem(BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.get(), MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH));
    } else {
      ME_INPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH = null;
      ME_OUTPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH = null;
    }
  }


  public static void register(final IEventBus bus) {
    ITEMS.register(bus);
  }
}
