package es.degrassi.mmreborn.energistics;

import es.degrassi.mmreborn.energistics.client.ModularMachineryRebornEnergisticsClient;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.data.MMRConfig;
import es.degrassi.mmreborn.energistics.common.integration.theoneprobe.TOPInfoProvider;
import es.degrassi.mmreborn.energistics.common.registration.EAERegistryHandler;
import es.degrassi.mmreborn.energistics.common.registration.Registration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Mod(ModularMachineryRebornEnergistics.MODID)
public class ModularMachineryRebornEnergistics {
  public static final String MODID = "modular_machinery_reborn_energistics";
  public static final Logger LOGGER = LogManager.getLogger("Modular Machinery Reborn Energistics");

  public ModularMachineryRebornEnergistics(final ModContainer CONTAINER, final IEventBus MOD_BUS) {
    CONTAINER.registerConfig(ModConfig.Type.COMMON, MMRConfig.getSpec());

    Registration.register(MOD_BUS);

    MOD_BUS.register(new ModularMachineryRebornEnergisticsClient());

    MOD_BUS.addListener(this::commonSetup);
    MOD_BUS.addListener(this::sendIMCMessages);
    MOD_BUS.addListener(this::reloadConfig);

    final IEventBus GAME_BUS = NeoForge.EVENT_BUS;
    GAME_BUS.addListener(this::onReloadStart);
  }

  private void sendIMCMessages(final InterModEnqueueEvent event) {
    if (ModList.get().isLoaded("theoneprobe"))
      InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPInfoProvider::new);
  }

  public void commonSetup(final FMLCommonSetupEvent event) {
    MEHatchSize.loadFromConfig();
    EAERegistryHandler.INSTANCE.onInit();
  }

  private void reloadConfig(final ModConfigEvent.Reloading event) {
    if(event.getConfig().getSpec() == MMRConfig.getSpec()) {
      MEHatchSize.loadFromConfig();
    }
  }

  @Contract("_ -> new")
  public static @NotNull ResourceLocation rl(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }

  private void onReloadStart(final CommandEvent event) {
    if (event.getParseResults().getReader().getString().equals("reload") && event.getParseResults().getContext().getSource().hasPermission(2)) {
      MEHatchSize.loadFromConfig();
    }
  }
}
