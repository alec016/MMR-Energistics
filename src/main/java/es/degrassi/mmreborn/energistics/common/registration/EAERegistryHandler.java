package es.degrassi.mmreborn.energistics.common.registration;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import es.degrassi.mmreborn.energistics.common.util.Mods;

public class EAERegistryHandler {
  public static final EAERegistryHandler INSTANCE = new EAERegistryHandler();

  public void onInit() {
    this.registerAEUpgrade();
  }

  private void registerAEUpgrade() {
    Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_INPUT_BUS.get(), 1, "gui.mmr.input_bus");
    Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_INPUT_BUS.get(), 1, "gui.mmr.input_bus");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_INPUT_BUS.get(), 4, "group.mmr.input_bus");
    Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_ADVANCED_INPUT_BUS.get(), 1, "gui.mmr.advanced_input_bus");
    Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_ADVANCED_INPUT_BUS.get(), 1, "gui.mmr.advanced_input_bus");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_INPUT_BUS.get(), 4, "group.mmr.advanced_input_bus");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_OUTPUT_BUS.get(), 4, "group.mmr.output_bus");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_OUTPUT_BUS.get(), 4, "group.mmr.advanced_output_bus");

    Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_INPUT_HATCH.get(), 1, "gui.mmr.input_hatch");
    Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_INPUT_HATCH.get(), 1, "gui.mmr.input_hatch");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_INPUT_HATCH.get(), 4, "group.mmr.input_hatch");
    Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_ADVANCED_INPUT_HATCH.get(), 1, "gui.mmr.advanced_input_hatch");
    Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_ADVANCED_INPUT_HATCH.get(), 1, "gui.mmr.advanced_input_hatch");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_INPUT_HATCH.get(), 4, "group.mmr.advanced_input_hatch");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_OUTPUT_HATCH.get(), 4, "group.mmr.output_hatch");
    Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_OUTPUT_HATCH.get(), 4, "group.mmr.advanced_output_hatch");

    if (Mods.isMekPossible()) {
      Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_INPUT_CHEMICAL_HATCH.get(), 1, "gui.mmr.input_chemical_hatch");
      Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_INPUT_CHEMICAL_HATCH.get(), 1, "gui.mmr.input_chemical_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_INPUT_CHEMICAL_HATCH.get(), 4, "group.mmr.input_chemical_hatch");
      Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get(), 1, "gui.mmr.advanced_input_chemical_hatch");
      Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get(), 1, "gui.mmr.advanced_input_chemical_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get(), 4, "group.mmr.advanced_input_chemical_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_OUTPUT_CHEMICAL_HATCH.get(), 4, "group.mmr.output_chemical_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.get(), 4, "group.mmr.advanced_output_chemical_hatch");
    }

    if (Mods.isArsPossible()) {
      Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_INPUT_SOURCE_HATCH.get(), 1, "gui.mmr.input_source_hatch");
      Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_INPUT_SOURCE_HATCH.get(), 1, "gui.mmr.input_source_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_INPUT_SOURCE_HATCH.get(), 4, "group.mmr.input_source_hatch");
      Upgrades.add(AEItems.FUZZY_CARD, ItemRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get(), 1, "gui.mmr.advanced_input_source_hatch");
      Upgrades.add(AEItems.CRAFTING_CARD, ItemRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get(), 1, "gui.mmr.advanced_input_source_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get(), 4, "group.mmr.advanced_input_source_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_OUTPUT_SOURCE_HATCH.get(), 4, "group.mmr.output_source_hatch");
      Upgrades.add(AEItems.SPEED_CARD, ItemRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.get(), 4, "group.mmr.advanced_output_source_hatch");
    }
  }
}
