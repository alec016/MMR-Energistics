package es.degrassi.mmreborn.energistics.common.network.server;

import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.entity.base.ControllerAccessible;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SSyncControllerPosInComponent(BlockPos entityPos, BlockPos controllerPos) implements CustomPacketPayload {
  public static final Type<SSyncControllerPosInComponent> TYPE = new Type<>(ModularMachineryRebornEnergistics.rl("sync_controller_pos"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncControllerPosInComponent> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      SSyncControllerPosInComponent::entityPos,
      BlockPos.STREAM_CODEC,
      SSyncControllerPosInComponent::controllerPos,
      SSyncControllerPosInComponent::new
  );

  @Override
  public Type<SSyncControllerPosInComponent> type() {
    return TYPE;
  }

  public static void handle(SSyncControllerPosInComponent packet, IPayloadContext context) {
    if (context.flow().isClientbound()) {
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.entityPos) instanceof ControllerAccessible accessible) {
          accessible.setControllerPos(packet.controllerPos);
        }
      });
    }
  }
}
