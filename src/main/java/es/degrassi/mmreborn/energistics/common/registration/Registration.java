package es.degrassi.mmreborn.energistics.common.registration;

import net.neoforged.bus.api.IEventBus;

public abstract class Registration {
  public static void register(final IEventBus bus) {
    BlockRegistration.register(bus);
    ItemRegistration.register(bus);
    EntityRegistration.register(bus);
    ContainerRegistration.register(bus);
    CreativeTabsRegistration.register(bus);
  }
}
