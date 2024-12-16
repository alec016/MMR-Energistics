package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
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
import es.degrassi.mmreborn.energistics.common.util.Mods;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ContainerRegistration {
  public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU,
      ModularMachineryRebornEnergistics.MODID);

  public static final DeferredHolder<MenuType<?>, MenuType<MEInputBusContainer>> INPUT_BUS = CONTAINERS.register(
      "input_bus",
      () -> MEInputBusContainer.TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputBusContainer>> OUTPUT_BUS = CONTAINERS.register(
      "output_bus",
      () -> MEOutputBusContainer.TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEInputBusContainer>> ADVANCED_INPUT_BUS = CONTAINERS.register(
      "advanced_input_bus",
      () -> MEInputBusContainer.ADVANCED_TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputBusContainer>> ADVANCED_OUTPUT_BUS = CONTAINERS.register(
      "advanced_output_bus",
      () -> MEOutputBusContainer.ADVANCED_TYPE
  );

  public static final DeferredHolder<MenuType<?>, MenuType<MEInputHatchContainer>> INPUT_HATCH = CONTAINERS.register(
      "input_hatch",
      () -> MEInputHatchContainer.TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputHatchContainer>> OUTPUT_HATCH = CONTAINERS.register(
      "output_hatch",
      () -> MEOutputHatchContainer.TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEInputHatchContainer>> ADVANCED_INPUT_HATCH = CONTAINERS.register(
      "advanced_input_hatch",
      () -> MEInputHatchContainer.ADVANCED_TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputHatchContainer>> ADVANCED_OUTPUT_HATCH = CONTAINERS.register(
      "advanced_output_hatch",
      () -> MEOutputHatchContainer.ADVANCED_TYPE
  );

  public static final DeferredHolder<MenuType<?>, MenuType<MEPatternBusContainer>> PATTERN_BUS = CONTAINERS.register(
      "pattern_bus",
      () -> MEPatternBusContainer.TYPE
  );
  public static final DeferredHolder<MenuType<?>, MenuType<MEPatternBusContainer>> ADVANCED_PATTERN_BUS = CONTAINERS.register(
      "advanced_pattern_bus",
      () -> MEPatternBusContainer.ADVANCED_TYPE
  );

  public static final DeferredHolder<MenuType<?>, MenuType<MEInputChemicalHatchContainer>> INPUT_CHEMICAL_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEInputChemicalHatchContainer>> ADVANCED_INPUT_CHEMICAL_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputChemicalHatchContainer>> OUTPUT_CHEMICAL_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputChemicalHatchContainer>> ADVANCED_OUTPUT_CHEMICAL_HATCH;

  public static final DeferredHolder<MenuType<?>, MenuType<MEInputSourceHatchContainer>> INPUT_SOURCE_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEInputSourceHatchContainer>> ADVANCED_INPUT_SOURCE_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputSourceHatchContainer>> OUTPUT_SOURCE_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputSourceHatchContainer>> ADVANCED_OUTPUT_SOURCE_HATCH;

  public static final DeferredHolder<MenuType<?>, MenuType<MEInputExperienceHatchContainer>> INPUT_EXPERIENCE_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEInputExperienceHatchContainer>> ADVANCED_INPUT_EXPERIENCE_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputExperienceHatchContainer>> OUTPUT_EXPERIENCE_HATCH;
  public static final DeferredHolder<MenuType<?>, MenuType<MEOutputExperienceHatchContainer>> ADVANCED_OUTPUT_EXPERIENCE_HATCH;

  static {
    if (Mods.isMekPossible()) {
      INPUT_CHEMICAL_HATCH = CONTAINERS.register(
          "input_chemical_hatch",
          () -> MEInputChemicalHatchContainer.TYPE
      );
      ADVANCED_INPUT_CHEMICAL_HATCH = CONTAINERS.register(
          "advanced_input_chemical_hatch",
          () -> MEInputChemicalHatchContainer.ADVANCED_TYPE
      );
      OUTPUT_CHEMICAL_HATCH = CONTAINERS.register(
          "output_chemical_hatch",
          () -> MEOutputChemicalHatchContainer.TYPE
      );
      ADVANCED_OUTPUT_CHEMICAL_HATCH = CONTAINERS.register(
          "advanced_output_chemical_hatch",
          () -> MEOutputChemicalHatchContainer.ADVANCED_TYPE
      );
    } else {
      INPUT_CHEMICAL_HATCH = null;
      ADVANCED_INPUT_CHEMICAL_HATCH = null;
      OUTPUT_CHEMICAL_HATCH = null;
      ADVANCED_OUTPUT_CHEMICAL_HATCH = null;
    }

    if (Mods.isArsPossible()) {
      INPUT_SOURCE_HATCH = CONTAINERS.register(
          "input_source_hatch",
          () -> MEInputSourceHatchContainer.TYPE
      );
      ADVANCED_INPUT_SOURCE_HATCH = CONTAINERS.register(
          "advanced_input_source_hatch",
          () -> MEInputSourceHatchContainer.ADVANCED_TYPE
      );
      OUTPUT_SOURCE_HATCH = CONTAINERS.register(
          "output_source_hatch",
          () -> MEOutputSourceHatchContainer.TYPE
      );
      ADVANCED_OUTPUT_SOURCE_HATCH = CONTAINERS.register(
          "advanced_output_source_hatch",
          () -> MEOutputSourceHatchContainer.ADVANCED_TYPE
      );
    } else {
      INPUT_SOURCE_HATCH = null;
      ADVANCED_INPUT_SOURCE_HATCH = null;
      OUTPUT_SOURCE_HATCH = null;
      ADVANCED_OUTPUT_SOURCE_HATCH = null;
    }

    if (Mods.isExperiencePossible()) {
      INPUT_EXPERIENCE_HATCH = CONTAINERS.register(
          "input_experience_hatch",
          () -> MEInputExperienceHatchContainer.TYPE
      );
      ADVANCED_INPUT_EXPERIENCE_HATCH = CONTAINERS.register(
          "advanced_input_experience_hatch",
          () -> MEInputExperienceHatchContainer.ADVANCED_TYPE
      );
      OUTPUT_EXPERIENCE_HATCH = CONTAINERS.register(
          "output_experience_hatch",
          () -> MEOutputExperienceHatchContainer.TYPE
      );
      ADVANCED_OUTPUT_EXPERIENCE_HATCH = CONTAINERS.register(
          "advanced_output_experience_hatch",
          () -> MEOutputExperienceHatchContainer.ADVANCED_TYPE
      );
    } else {
      INPUT_EXPERIENCE_HATCH = null;
      ADVANCED_INPUT_EXPERIENCE_HATCH = null;
      OUTPUT_EXPERIENCE_HATCH = null;
      ADVANCED_OUTPUT_EXPERIENCE_HATCH = null;
    }
  }


  public static void register(IEventBus bus) {
    CONTAINERS.register(bus);
  }
}
