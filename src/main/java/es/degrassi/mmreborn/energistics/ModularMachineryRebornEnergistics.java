package es.degrassi.mmreborn.energistics;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.block.prop.ConfigLoaded;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.data.MMRConfig;
import es.degrassi.mmreborn.energistics.common.integration.theoneprobe.TOPInfoProvider;
import es.degrassi.mmreborn.energistics.common.registration.EAERegistryHandler;
import es.degrassi.mmreborn.energistics.common.registration.EntityRegistration;
import es.degrassi.mmreborn.energistics.common.registration.Registration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Mod(ModularMachineryRebornEnergistics.MODID)
public class ModularMachineryRebornEnergistics {
  public static final String MODID = "modular_machinery_reborn_energistics";
  public static final Logger LOGGER = LogManager.getLogger("Modular Machinery Reborn Energistics");

  public ModularMachineryRebornEnergistics(final ModContainer CONTAINER, final IEventBus MOD_BUS) {
    CONTAINER.registerConfig(ModConfig.Type.COMMON, MMRConfig.getSpec(), config("common"));

    addConfigLoaders();
    Registration.register(MOD_BUS);

    MOD_BUS.addListener(this::registerCapabilities);
    MOD_BUS.addListener(this::commonSetup);
    MOD_BUS.addListener(this::sendIMCMessages);
  }

  private void registerCapabilities(RegisterCapabilitiesEvent event) {
    for (var type : EntityRegistration.getImplementorsOf(IInWorldGridNodeHost.class)) {
      event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, type,
          (object, context) -> (IInWorldGridNodeHost) object);
    }
  }

  private static void addConfigLoaders() {
    ConfigLoaded.add(MEHatchSize.class, size -> size.setIdlePowerDrainOnConnected(MMRConfig.get().idlePowerDrainOnConnected(size)));
  }

  private static String config(String name) {
    return String.format("%s/Energistics/%s.toml", ModularMachineryReborn.MODID, name);
  }

  private void sendIMCMessages(final InterModEnqueueEvent event) {
    if (ModList.get().isLoaded("theoneprobe"))
      InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPInfoProvider::new);
  }

  public void commonSetup(final FMLCommonSetupEvent event) {
    EAERegistryHandler.INSTANCE.onInit();
  }

  @Contract("_ -> new")
  public static @NotNull ResourceLocation rl(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }
}
