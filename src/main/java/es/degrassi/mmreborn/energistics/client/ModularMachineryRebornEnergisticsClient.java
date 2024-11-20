package es.degrassi.mmreborn.energistics.client;

import appeng.init.client.InitScreens;
import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.energistics.client.container.MEInputBusContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputBusContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputHatchContainer;
import es.degrassi.mmreborn.energistics.client.screen.MEInputBusScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEInputHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputBusScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputHatchScreen;
import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import es.degrassi.mmreborn.energistics.common.registration.ItemRegistration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ModularMachineryRebornEnergisticsClient {

  @SubscribeEvent
  public void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
    event.register(
        ModularMachineryRebornClient::blockColor,
        BlockRegistration.ME_INPUT_BUS.get(),
        BlockRegistration.ME_OUTPUT_BUS.get(),
        BlockRegistration.ME_ADVANCED_INPUT_BUS.get(),
        BlockRegistration.ME_ADVANCED_OUTPUT_BUS.get(),
        BlockRegistration.ME_INPUT_HATCH.get(),
        BlockRegistration.ME_OUTPUT_HATCH.get(),
        BlockRegistration.ME_ADVANCED_INPUT_HATCH.get(),
        BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.get()
    );
  }

  @SubscribeEvent
  public void registerItemColors(final RegisterColorHandlersEvent.Item event) {
    event.register(
        ModularMachineryRebornClient::itemColor,
        ItemRegistration.ME_INPUT_BUS.get(),
        ItemRegistration.ME_OUTPUT_BUS.get(),
        ItemRegistration.ME_ADVANCED_INPUT_BUS.get(),
        ItemRegistration.ME_ADVANCED_OUTPUT_BUS.get(),
        ItemRegistration.ME_INPUT_HATCH.get(),
        ItemRegistration.ME_OUTPUT_HATCH.get(),
        ItemRegistration.ME_ADVANCED_INPUT_HATCH.get(),
        ItemRegistration.ME_ADVANCED_OUTPUT_HATCH.get()
    );
  }

  @SubscribeEvent
  public void registerMenuScreens(final RegisterMenuScreensEvent event) {
    InitScreens.register(event, MEInputBusContainer.TYPE, MEInputBusScreen::new, "/screens/me_input_bus.json");
    InitScreens.register(event, MEInputBusContainer.ADVANCED_TYPE, MEInputBusScreen::new, "/screens/me_advanced_input_bus.json");
    InitScreens.register(event, MEOutputBusContainer.TYPE, MEOutputBusScreen::new, "/screens/me_output_bus.json");
    InitScreens.register(event, MEOutputBusContainer.ADVANCED_TYPE, MEOutputBusScreen::new, "/screens/me_advanced_output_bus.json");

    InitScreens.register(event, MEInputHatchContainer.TYPE, MEInputHatchScreen::new, "/screens/me_input_hatch.json");
    InitScreens.register(event, MEInputHatchContainer.ADVANCED_TYPE, MEInputHatchScreen::new, "/screens/me_advanced_input_hatch.json");
    InitScreens.register(event, MEOutputHatchContainer.TYPE, MEOutputHatchScreen::new, "/screens/me_output_hatch.json");
    InitScreens.register(event, MEOutputHatchContainer.ADVANCED_TYPE, MEOutputHatchScreen::new, "/screens/me_advanced_output_hatch.json");
  }
}
