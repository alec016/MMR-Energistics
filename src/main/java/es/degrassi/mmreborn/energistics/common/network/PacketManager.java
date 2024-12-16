package es.degrassi.mmreborn.energistics.common.network;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.ServerboundPacket;
import es.degrassi.mmreborn.energistics.common.network.client.ConfigButtonPacket;
import es.degrassi.mmreborn.energistics.common.network.server.SSyncControllerPosInComponent;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.ParametersAreNonnullByDefault;

@EventBusSubscriber(modid = ModularMachineryRebornEnergistics.MODID, bus = EventBusSubscriber.Bus.MOD)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PacketManager {
  public PacketManager() {
  }

  @SubscribeEvent
  public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(ModularMachineryRebornEnergistics.MODID);
    registrar.playToClient(SSyncControllerPosInComponent.TYPE, SSyncControllerPosInComponent.CODEC, SSyncControllerPosInComponent::handle);
    serverbound(registrar, ConfigButtonPacket.TYPE, ConfigButtonPacket.STREAM_CODEC);
  }

  private static <T extends ServerboundPacket> void serverbound(PayloadRegistrar registrar,
                                                                CustomPacketPayload.Type<T> type,
                                                                StreamCodec<RegistryFriendlyByteBuf, T> codec) {
    registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
  }

  private static <T extends ClientboundPacket> void clientbound(PayloadRegistrar registrar,
                                                                CustomPacketPayload.Type<T> type,
                                                                StreamCodec<RegistryFriendlyByteBuf, T> codec) {
    registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
  }
}
