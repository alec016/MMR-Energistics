package es.degrassi.mmreborn.energistics.data;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ModularMachineryRebornEnergistics.MODID)
public class DataGeneration {

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    MMRBlockTagProvider blockTagProvider = generator.addProvider(
        event.includeServer(),
        new MMRBlockTagProvider(packOutput, lookupProvider, fileHelper)
    );
    generator.addProvider(
        event.includeServer(),
        new MMRItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), fileHelper)
    );

    generator.addProvider(
        event.includeServer(),
        new MMRLootTableProvider(packOutput, lookupProvider)
    );

    generator.addProvider(true, new MMRLangProvider(packOutput, "en_us"));
    generator.addProvider(true, new MMRLangProvider(packOutput, "es_es"));
    generator.addProvider(true, new MMRLangProvider(packOutput, "zh_cn"));

    generator.addProvider(true, new MMREngBlockStateProvider(packOutput, fileHelper));
  }
}
