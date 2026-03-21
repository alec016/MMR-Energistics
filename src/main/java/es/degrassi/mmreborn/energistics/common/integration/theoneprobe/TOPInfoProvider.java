package es.degrassi.mmreborn.energistics.common.integration.theoneprobe;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TOPInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {
  @Override
  public Void apply(ITheOneProbe probe) {
    probe.registerProvider(this);
    return null;
  }

  @Override
  public ResourceLocation getID() {
    return ModularMachineryRebornEnergistics.rl("mmrenergistics_info_provider");
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData data) {
    BlockEntity tile = level.getBlockEntity(data.getPos());
    if (tile instanceof MEEntity<?, ?> entity) {
      showAE2Info(entity, info);
    }
  }

  private void showAE2Info(MEEntity<?, ?> entity, IProbeInfo info) {
    if (!entity.getMainNode().hasGridBooted()) {
      info.mcText(Component.translatable("mmr.gui.me_network.booting").withStyle(ChatFormatting.GRAY));
    }
    if (entity.hasColor()) {
      info.mcText(
          Component.translatable(
              "mmre.info.node.color",
              Component.translatable(entity.getGridColor().translationKey).withColor(entity.getGridColor().whiteVariant)
          ).withStyle(ChatFormatting.GRAY)
      );
    }
  }
}
