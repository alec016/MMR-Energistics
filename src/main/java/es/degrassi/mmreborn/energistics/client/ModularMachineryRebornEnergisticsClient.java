package es.degrassi.mmreborn.energistics.client;

import appeng.init.client.InitScreens;
import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.energistics.client.container.MEInputBusContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputChemicalHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputSourceHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputBusContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputChemicalHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputSourceHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEPatternBusContainer;
import es.degrassi.mmreborn.energistics.client.screen.MEInputBusScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEInputChemicalHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEInputExperienceHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEInputHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEInputSourceHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputBusScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputChemicalHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputExperienceHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEOutputSourceHatchScreen;
import es.degrassi.mmreborn.energistics.client.screen.MEPatternBusScreen;
import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import es.degrassi.mmreborn.energistics.common.registration.ItemRegistration;
import es.degrassi.mmreborn.energistics.common.util.Mods;
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
        BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.get(),
        BlockRegistration.ME_PATTERN_BUS.get(),
        BlockRegistration.ME_ADVANCED_PATTERN_BUS.get()
    );

    if (Mods.isMekPossible()) {
      event.register(
          ModularMachineryRebornClient::blockColor,
          BlockRegistration.ME_INPUT_CHEMICAL_HATCH.get(),
          BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get(),
          BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH.get(),
          BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.get()
      );
    }

    if (Mods.isArsPossible()) {
      event.register(
          ModularMachineryRebornClient::blockColor,
          BlockRegistration.ME_INPUT_SOURCE_HATCH.get(),
          BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get(),
          BlockRegistration.ME_OUTPUT_SOURCE_HATCH.get(),
          BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.get()
      );
    }

    if (Mods.isExperiencePossible()) {
      event.register(
          ModularMachineryRebornClient::blockColor,
          BlockRegistration.ME_INPUT_EXPERIENCE_HATCH.get(),
          BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.get(),
          BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH.get(),
          BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.get()
      );
    }
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
        ItemRegistration.ME_ADVANCED_OUTPUT_HATCH.get(),
        ItemRegistration.ME_PATTERN_BUS.get(),
        ItemRegistration.ME_ADVANCED_PATTERN_BUS.get()
    );

    if (Mods.isMekPossible()) {
      event.register(
          ModularMachineryRebornClient::itemColor,
          ItemRegistration.ME_INPUT_CHEMICAL_HATCH.get(),
          ItemRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get(),
          ItemRegistration.ME_OUTPUT_CHEMICAL_HATCH.get(),
          ItemRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.get()
      );
    }

    if (Mods.isArsPossible()) {
      event.register(
          ModularMachineryRebornClient::itemColor,
          ItemRegistration.ME_INPUT_SOURCE_HATCH.get(),
          ItemRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get(),
          ItemRegistration.ME_OUTPUT_SOURCE_HATCH.get(),
          ItemRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.get()
      );
    }

    if (Mods.isExperiencePossible()) {
      event.register(
          ModularMachineryRebornClient::itemColor,
          ItemRegistration.ME_INPUT_EXPERIENCE_HATCH.get(),
          ItemRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.get(),
          ItemRegistration.ME_OUTPUT_EXPERIENCE_HATCH.get(),
          ItemRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.get()
      );
    }
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

    InitScreens.register(event, MEPatternBusContainer.ADVANCED_TYPE, MEPatternBusScreen::new, "/screens/me_advanced_pattern_bus.json");
    InitScreens.register(event, MEPatternBusContainer.TYPE, MEPatternBusScreen::new, "/screens/me_pattern_bus.json");

    if (Mods.isMekPossible()) {
      InitScreens.register(event, MEInputChemicalHatchContainer.TYPE, MEInputChemicalHatchScreen::new, "/screens" +
          "/me_input_chemical_hatch.json");
      InitScreens.register(event, MEInputChemicalHatchContainer.ADVANCED_TYPE, MEInputChemicalHatchScreen::new, "/screens" +
          "/me_advanced_input_chemical_hatch.json");
      InitScreens.register(event, MEOutputChemicalHatchContainer.TYPE, MEOutputChemicalHatchScreen::new, "/screens" +
          "/me_output_chemical_hatch.json");
      InitScreens.register(event, MEOutputChemicalHatchContainer.ADVANCED_TYPE, MEOutputChemicalHatchScreen::new, "/screens" +
          "/me_advanced_output_chemical_hatch.json");
    }

    if (Mods.isArsPossible()) {
      InitScreens.register(event, MEInputSourceHatchContainer.TYPE, MEInputSourceHatchScreen::new, "/screens" +
          "/me_input_source_hatch.json");
      InitScreens.register(event, MEInputSourceHatchContainer.ADVANCED_TYPE, MEInputSourceHatchScreen::new, "/screens" +
          "/me_advanced_input_source_hatch.json");
      InitScreens.register(event, MEOutputSourceHatchContainer.TYPE, MEOutputSourceHatchScreen::new, "/screens" +
          "/me_output_source_hatch.json");
      InitScreens.register(event, MEOutputSourceHatchContainer.ADVANCED_TYPE, MEOutputSourceHatchScreen::new,
          "/screens/me_advanced_output_source_hatch.json");
    }

    if (Mods.isExperiencePossible()) {
      InitScreens.register(event, MEInputExperienceHatchContainer.TYPE, MEInputExperienceHatchScreen::new, "/screens" +
          "/me_input_experience_hatch.json");
      InitScreens.register(event, MEInputExperienceHatchContainer.ADVANCED_TYPE, MEInputExperienceHatchScreen::new, "/screens" +
          "/me_advanced_input_experience_hatch.json");
      InitScreens.register(event, MEOutputExperienceHatchContainer.TYPE, MEOutputExperienceHatchScreen::new,
          "/screens/me_output_experience_hatch.json");
      InitScreens.register(event, MEOutputExperienceHatchContainer.ADVANCED_TYPE, MEOutputExperienceHatchScreen::new,
          "/screens/me_advanced_output_experience_hatch.json");
    }
  }
}
