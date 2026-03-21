package es.degrassi.mmreborn.energistics.data;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.BlockController;
import es.degrassi.mmreborn.common.block.BlockMachineComponent;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.data.BaseMMRBlockStateProvider;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import es.degrassi.mmreborn.energistics.data.blockstate.StateMEHatchBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MMREngBlockStateProvider extends BaseMMRBlockStateProvider {
  protected MMREngBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, ModularMachineryRebornEnergistics.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    addMeHatch(BlockRegistration.ME_INPUT_BUS.block(), meHatch(MEHatchSize.ME_INPUT_BUS), false);
    addMeHatch(BlockRegistration.ME_ADVANCED_INPUT_BUS.block(), meHatch(MEHatchSize.ME_ADVANCED_INPUT_BUS), true);
    addMeHatch(BlockRegistration.ME_OUTPUT_BUS.block(), meHatch(MEHatchSize.ME_OUTPUT_BUS), false);
    addMeHatch(BlockRegistration.ME_ADVANCED_OUTPUT_BUS.block(), meHatch(MEHatchSize.ME_ADVANCED_OUTPUT_BUS), true);

    addMeHatch(BlockRegistration.ME_INPUT_HATCH.block(), meHatch(MEHatchSize.ME_INPUT_HATCH), false);
    addMeHatch(BlockRegistration.ME_ADVANCED_INPUT_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_INPUT_HATCH), true);
    addMeHatch(BlockRegistration.ME_OUTPUT_HATCH.block(), meHatch(MEHatchSize.ME_OUTPUT_HATCH), false);
    addMeHatch(BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH), true);

    addMeHatch(BlockRegistration.ME_PATTERN_BUS.block(), meHatch(MEHatchSize.ME_PATTERN_BUS), false);
    addMeHatch(BlockRegistration.ME_ADVANCED_PATTERN_BUS.block(), meHatch(MEHatchSize.ME_ADVANCED_PATTERN_BUS), true);

    if (Mods.isArsPossible()) {
      addMeHatch(BlockRegistration.ME_INPUT_SOURCE_HATCH.block(), meHatch(MEHatchSize.ME_INPUT_SOURCE_HATCH), false);
      addMeHatch(BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH), true);
      addMeHatch(BlockRegistration.ME_OUTPUT_SOURCE_HATCH.block(), meHatch(MEHatchSize.ME_OUTPUT_SOURCE_HATCH), false);
      addMeHatch(BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_OUTPUT_BUS), true);
    }

    if (Mods.isMekPossible()) {
      addMeHatch(BlockRegistration.ME_INPUT_CHEMICAL_HATCH.block(), meHatch(MEHatchSize.ME_INPUT_CHEMICAL_HATCH), false);
      addMeHatch(BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH), true);
      addMeHatch(BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH.block(), meHatch(MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH), false);
      addMeHatch(BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH), true);
    }

    if (Mods.isExperiencePossible()) {
      addMeHatch(BlockRegistration.ME_INPUT_EXPERIENCE_HATCH.block(), meHatch(MEHatchSize.ME_INPUT_EXPERIENCE_HATCH), false);
      addMeHatch(BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH), true);
      addMeHatch(BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH.block(), meHatch(MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH), false);
      addMeHatch(BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.block(), meHatch(MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH), true);
    }
  }

  protected <T extends MachineComponent<X>, X> void addMeHatch(MEBlock<T, X> block, ResourceLocation overlay, boolean advanced) {
    var builder = new StateMEHatchBuilder()
        .center(plainCenter)
        .horizontal(plainHorizontal)
        .empty(empty)
        .vertical(plainVertical)
        .particle(plainParticle)
        .overlay(ml("block/overlay_me_" + (advanced ? "advanced_" : "") + "inactive"))
        .centerActive(plainCenter)
        .horizontalActive(plainHorizontal)
        .emptyActive(empty)
        .verticalActive(plainVertical)
        .overlayActive(withActiveSuffix(overlay));
    var defaultHatchBuilder = models()
        .getBuilder(this.key(block).withPrefix("default/hatches/").toString())
        .parent(new ModelFile.UncheckedModelFile(modLoc("block/blockmodel_overlay_orientable_all").toString()))
        .texture("bg_all", plainParticle)
        .texture("ov_front", withActiveSuffix(overlay))
        .texture("ov_side", modLoc("block/overlay_transparent"))
        .texture("ov_top", modLoc("block/overlay_transparent"));
    if (advanced) {
      builder = builder.connectToReinforced()
          .ovCenter(reinforcedCenter)
          .ovHorizontal(reinforcedHorizontal)
          .ovVertical(reinforcedVertical)
          .ovParticle(reinforcedParticle);
      defaultHatchBuilder = defaultHatchBuilder
          .texture("ov_r_all", reinforcedParticle);
    } else {
      builder = builder.connectToPlain();
    }
    this.registeredBlocks.put(block, builder);
    item(block, true, true, withActiveSuffix(overlay));
    defaultHatch(block, defaultHatchBuilder);
  }

  @Override
  protected ResourceLocation loader(Block block) {
    return switch(block) {
      case BlockController blockController -> ModularMachineryReborn.rl("controller");
      case BlockMachineComponent blockMachineComponent -> ModularMachineryReborn.rl("hatch");
      case MEBlock<?, ?> meBlock -> ModularMachineryReborn.rl("hatch");
      default -> null;
    };
  }

  private ResourceLocation meHatch(MEHatchSize size) {
    return ml("block/overlay_" + size.getSerializedName());
  }

  private ResourceLocation withActiveSuffix(ResourceLocation rl) {
    return rl.withSuffix("_active");
  }

  @Override
  public ResourceLocation modLoc(String name) {
    return ModularMachineryReborn.rl(name);
  }

  public ResourceLocation ml(String name) {
    return ModularMachineryRebornEnergistics.rl(name);
  }
}
