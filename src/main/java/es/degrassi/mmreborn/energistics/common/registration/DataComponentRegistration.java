package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class DataComponentRegistration {
  public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister
      .create(Registries.DATA_COMPONENT_TYPE, ModularMachineryRebornEnergistics.MODID);

  private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
    var builder = DataComponentType.<T>builder();
    customizer.accept(builder);
    var componentType = builder.build();
    DR.register(name, () -> componentType);
    return componentType;
  }

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }
}
