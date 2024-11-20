package es.degrassi.mmreborn.energistics.data;

import es.degrassi.mmreborn.data.MMRTags;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MMRBlockTagProvider extends BlockTagsProvider {
  public MMRBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, ModularMachineryRebornEnergistics.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.@NotNull Provider provider) {
    tag(MMREnergisticsTags.Blocks.ME_FLUID_OUTPUT)
        .add(
            BlockRegistration.ME_OUTPUT_HATCH.get(),
            BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.get()
        );
    tag(MMREnergisticsTags.Blocks.ME_FLUID_INPUT)
        .add(
            BlockRegistration.ME_INPUT_HATCH.get(),
            BlockRegistration.ME_ADVANCED_INPUT_HATCH.get()
        );
    tag(MMREnergisticsTags.Blocks.ME_OUTPUT_BUS)
        .add(
            BlockRegistration.ME_OUTPUT_BUS.get(),
            BlockRegistration.ME_ADVANCED_OUTPUT_BUS.get()
        );
    tag(MMREnergisticsTags.Blocks.ME_INPUT_BUS)
        .add(
            BlockRegistration.ME_INPUT_BUS.get(),
            BlockRegistration.ME_ADVANCED_INPUT_BUS.get()
        );

    tag(MMREnergisticsTags.Blocks.ME_BUS)
        .addTag(MMREnergisticsTags.Blocks.ME_OUTPUT_BUS)
        .addTag(MMREnergisticsTags.Blocks.ME_INPUT_BUS);

    tag(MMREnergisticsTags.Blocks.ME_FLUID)
        .addTag(MMREnergisticsTags.Blocks.ME_FLUID_OUTPUT)
        .addTag(MMREnergisticsTags.Blocks.ME_FLUID_INPUT);

    tag(MMRTags.Blocks.INPUT_BUS)
        .addTag(MMREnergisticsTags.Blocks.ME_INPUT_BUS);

    tag(MMRTags.Blocks.OUTPUT_BUS)
        .addTag(MMREnergisticsTags.Blocks.ME_OUTPUT_BUS);

    tag(MMRTags.Blocks.FLUID_INPUT)
        .addTag(MMREnergisticsTags.Blocks.ME_FLUID_INPUT);

    tag(MMRTags.Blocks.FLUID_OUTPUT)
        .addTag(MMREnergisticsTags.Blocks.ME_FLUID_OUTPUT);
  }
}
