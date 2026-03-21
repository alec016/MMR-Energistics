package es.degrassi.mmreborn.energistics.data.lang;

import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import es.degrassi.mmreborn.energistics.data.MMREnergisticsTags;

public final class ZhCnLang extends Lang {

  @Override
  protected void addTags() {
    MMREnergisticsTags.getAllTags().forEach(tag -> add(tag.getFirst(), tag.getSecond()));
  }

  @Override
  protected void addItemGroups() {
    add("itemGroup." + mme("group"), "模块化机械:重置 应用能源附属");
  }

  @Override
  protected void addGuiTitles() {
    add(gui(mmr(meBus("pattern"))), "ME Pattern Bus");
    add(gui(mmr(meBus("advanced_pattern"))), "ME Advanced Pattern Bus");

    add(gui(mmr(meBus("input"))), "ME物品输入仓");
    add(gui(mmr(meBus("advanced_input"))), "高级ME物品输入仓");
    add(gui(mmr(meBus("output"))), "ME物品输出仓");
    add(gui(mmr(meBus("advanced_output"))), "高级ME物品输出仓");

    add(gui(mmr(meHatch("input"))), "ME流体输出仓");
    add(gui(mmr(meHatch("output"))), "ME流体输出仓");
    add(gui(mmr(meHatch("advanced_input"))), "高级ME流体输入仓");
    add(gui(mmr(meHatch("advanced_output"))), "高级ME流体输出仓");

    add(gui(mmr(meHatch("input_chemical"))), "ME化学品输入仓");
    add(gui(mmr(meHatch("output_chemical"))), "ME化学品输出仓");
    add(gui(mmr(meHatch("advanced_input_chemical"))), "高级ME化学品输入仓");
    add(gui(mmr(meHatch("advanced_output_chemical"))), "高级ME化学品输出仓");

    add(gui(mmr(meHatch("input_source"))), "ME魔源输入仓");
    add(gui(mmr(meHatch("output_source"))), "ME魔源输出仓");
    add(gui(mmr(meHatch("advanced_input_source"))), "高级ME魔源输入仓");
    add(gui(mmr(meHatch("advanced_output_source"))), "高级ME魔源输出仓");

    add(gui(mmr(meHatch("input_experience"))), "ME Input Experience Hatch");
    add(gui(mmr(meHatch("output_experience"))), "ME Output Experience Hatch");
    add(gui(mmr(meHatch("advanced_input_experience"))), "ME Advanced Input Experience Hatch");
    add(gui(mmr(meHatch("advanced_output_experience"))), "ME Advanced Output Experience Hatch");
  }

  @Override
  protected void addTooltips() {
    add(mmre("info.node.color"), "Color: %s");
    add(mmr(gui("me_network.booting")), "寻找网络中..");
  }

  @Override
  protected void addBlocks() {
    addBlock(BlockRegistration.ME_INPUT_BUS::block, "ME物品输入仓");
    addBlock(BlockRegistration.ME_ADVANCED_INPUT_BUS::block, "高级ME输入仓");
    addBlock(BlockRegistration.ME_OUTPUT_BUS::block, "ME物品输出仓");
    addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_BUS::block, "高级ME输出仓");

    addBlock(BlockRegistration.ME_INPUT_HATCH::block, "ME流体输入仓");
    addBlock(BlockRegistration.ME_ADVANCED_INPUT_HATCH::block, "高级ME流体输入仓");
    addBlock(BlockRegistration.ME_OUTPUT_HATCH::block, "ME流体输出仓");
    addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_HATCH::block, "高级ME流体输出仓");

    addBlock(BlockRegistration.ME_PATTERN_BUS::block, "ME Pattern Bus");
    addBlock(BlockRegistration.ME_ADVANCED_PATTERN_BUS::block, "ME Advanced Pattern Bus");

    if (Mods.isExperiencePossible()) {
      addBlock(BlockRegistration.ME_INPUT_EXPERIENCE_HATCH::block, "ME Input Experience Hatch");
      addBlock(BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH::block, "ME Output Experience Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH::block, "ME Advanced Input Experience Hatch");
      addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH::block, "ME Advanced Output Experience Hatch");
    }

    if (Mods.isArsPossible()) {
      addBlock(BlockRegistration.ME_INPUT_SOURCE_HATCH::block, "ME魔源输入仓");
      addBlock(BlockRegistration.ME_OUTPUT_SOURCE_HATCH::block, "ME魔源输出仓");
      addBlock(BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH::block, "高级ME魔源输入仓");
      addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH::block, "高级ME魔源输出仓");
    }

    if (Mods.isMekPossible()) {
      addBlock(BlockRegistration.ME_INPUT_CHEMICAL_HATCH::block, "ME化学品输入仓");
      addBlock(BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH::block, "ME化学品输出仓");
      addBlock(BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH::block, "高级ME化学品输入仓");
      addBlock(BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH::block, "高级ME化学品输出仓");
    }
  }

  @Override
  protected void addJade() {
    add("config.jade.plugin_modular_machinery_reborn_energistics.me_entity_component_provider", "模块化机械:重置 应用能源附属");
  }
}
