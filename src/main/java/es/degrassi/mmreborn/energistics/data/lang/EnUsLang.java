package es.degrassi.mmreborn.energistics.data.lang;

import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import es.degrassi.mmreborn.energistics.data.MMREnergisticsTags;

public final class EnUsLang extends Lang {

  @Override
  protected void addTags() {
    MMREnergisticsTags.getAllTags().forEach(tag -> add(tag.getFirst(), tag.getSecond()));
  }

  @Override
  protected void addItemGroups() {
    add("itemGroup." + mme("group"), "Modular Machinery Reborn Energistics");
  }

  @Override
  protected void addGuiTitles() {
    add(gui(mmr(meBus("pattern"))), "ME Pattern Bus");
    add(gui(mmr(meBus("advanced_pattern"))), "ME Advanced Pattern Bus");

    add(gui(mmr(meBus("input"))), "ME Input Bus");
    add(gui(mmr(meBus("advanced_input"))), "ME Advanced Input Bus");
    add(gui(mmr(meBus("output"))), "ME Output Bus");
    add(gui(mmr(meBus("advanced_output"))), "ME Advanced Output Bus");

    add(gui(mmr(meHatch("input"))), "ME Input Hatch");
    add(gui(mmr(meHatch("output"))), "ME Output hatch");
    add(gui(mmr(meHatch("advanced_input"))), "ME Advanced Input Hatch");
    add(gui(mmr(meHatch("advanced_output"))), "ME Advanced Output Hatch");

    add(gui(mmr(meHatch("input_chemical"))), "ME Input Chemical Hatch");
    add(gui(mmr(meHatch("output_chemical"))), "ME Output Chemical Hatch");
    add(gui(mmr(meHatch("advanced_input_chemical"))), "ME Advanced Input Chemical Hatch");
    add(gui(mmr(meHatch("advanced_output_chemical"))), "ME Advanced Output Chemical Hatch");

    add(gui(mmr(meHatch("input_source"))), "ME Input Source Hatch");
    add(gui(mmr(meHatch("output_source"))), "ME Output Source Hatch");
    add(gui(mmr(meHatch("advanced_input_source"))), "ME Advanced Input Source Hatch");
    add(gui(mmr(meHatch("advanced_output_source"))), "ME Advanced Output Source Hatch");

    add(gui(mmr(meHatch("input_experience"))), "ME Input Experience Hatch");
    add(gui(mmr(meHatch("output_experience"))), "ME Output Experience Hatch");
    add(gui(mmr(meHatch("advanced_input_experience"))), "ME Advanced Input Experience Hatch");
    add(gui(mmr(meHatch("advanced_output_experience"))), "ME Advanced Output Experience Hatch");
  }

  @Override
  protected void addTooltips() {
    add(mmre("info.node.color"), "Color: %s");
    add(mmr(gui("me_network.booting")), "Network Booting");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.ME_INPUT_BUS::block, "ME Input Bus");
    addBlock(BlockRegistration.ME_ADVANCED_INPUT_BUS::block, "ME Advanced Input Bus");
    addBlock(BlockRegistration.ME_OUTPUT_BUS::block, "ME Output Bus");
    addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_BUS::block, "ME Advanced Output Bus");

    addBlock(BlockRegistration.ME_INPUT_HATCH::block, "ME Input Hatch");
    addBlock(BlockRegistration.ME_ADVANCED_INPUT_HATCH::block, "ME Advanced Input Hatch");
    addBlock(BlockRegistration.ME_OUTPUT_HATCH::block, "ME Output Hatch");
    addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_HATCH::block, "ME Advanced Output Hatch");

    addBlock(BlockRegistration.ME_PATTERN_BUS::block, "ME Pattern Bus");
    addBlock(BlockRegistration.ME_ADVANCED_PATTERN_BUS::block, "ME Advanced Pattern Bus");

    if (Mods.isExperiencePossible()) {
      addBlock(BlockRegistration.ME_INPUT_EXPERIENCE_HATCH::block, "ME Input Experience Hatch");
      addBlock(BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH::block, "ME Output Experience Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH::block, "ME Advanced Input Experience Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH::block, "ME Advanced Output Experience Hatch");
    }

    if (Mods.isArsPossible()) {
      addBlock(BlockRegistration.ME_INPUT_SOURCE_HATCH::block, "ME Input Source Hatch");
      addBlock(BlockRegistration.ME_OUTPUT_SOURCE_HATCH::block, "ME Output Source Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH::block, "ME Advanced Input Source Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH::block, "ME Advanced Output Source Hatch");
    }

    if (Mods.isMekPossible()) {
      addBlock(BlockRegistration.ME_INPUT_CHEMICAL_HATCH::block, "ME Input Chemical Hatch");
      addBlock(BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH::block, "ME Output Chemical Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH::block, "ME Advanced Input Chemical Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH::block, "ME Advanced Output Chemical Hatch");
    }
  }

  @Override
  protected void addJade() {
    add("config.jade.plugin_modular_machinery_reborn_energistics.me_entity_component_provider", "Modular Machinery Reborn Energistics provider");
  }
}
