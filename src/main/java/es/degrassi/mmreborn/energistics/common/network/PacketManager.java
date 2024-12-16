package es.degrassi.mmreborn.energistics.common.network;

import es.degrassi.mmreborn.energistics.common.network.server.SSyncControllerPosInComponent;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModularMachineryRebornEnergistics.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PacketManager {
  public PacketManager() {
  }

  @SubscribeEvent
  public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(ModularMachineryRebornEnergistics.MODID);
    registrar.playToClient(SSyncControllerPosInComponent.TYPE, SSyncControllerPosInComponent.CODEC, SSyncControllerPosInComponent::handle);
  }
}
