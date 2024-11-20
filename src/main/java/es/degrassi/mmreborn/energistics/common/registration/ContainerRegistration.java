package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.client.container.MEInputBusContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputBusContainer;
import es.degrassi.mmreborn.energistics.client.container.MEOutputHatchContainer;
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


  public static void register(IEventBus bus) {
    CONTAINERS.register(bus);
  }
}
