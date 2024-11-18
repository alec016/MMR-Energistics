package es.degrassi.mmreborn.energistics;

import es.degrassi.mmreborn.energistics.client.ModularMachineryRebornEnergisticsClient;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.data.MMRConfig;
import es.degrassi.mmreborn.energistics.common.registration.EAERegistryHandler;
import es.degrassi.mmreborn.energistics.common.registration.Registration;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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

  public ModularMachineryRebornEnergistics(final IEventBus MOD_BUS) {
    ConfigHolder<MMRConfig> config = AutoConfig.register(MMRConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));

    config.registerSaveListener((holder, mmrConfig) -> {
      MEHatchSize.loadFromConfig();
      return InteractionResult.SUCCESS;
    });

    Registration.register(MOD_BUS);

    MOD_BUS.register(new ModularMachineryRebornEnergisticsClient());

    MOD_BUS.addListener(this::registerCapabilities);
    MOD_BUS.addListener(this::commonSetup);

    final IEventBus GAME_BUS = NeoForge.EVENT_BUS;
    GAME_BUS.addListener(this::onReloadStart);

    MEHatchSize.loadFromConfig();
  }

  public void commonSetup(final FMLCommonSetupEvent event) {
    EAERegistryHandler.INSTANCE.onInit();
  }

  private void registerCapabilities(final RegisterCapabilitiesEvent event) {
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
