package es.degrassi.mmreborn.energistics.common.integration.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum AEStatus {
  ONLINE,
  OFFLINE,
  MISSING_CHANNEL,
  BOOTING;

  public static AEStatus getAEStatus(boolean powered, boolean channel) {
    return !powered
        ? OFFLINE
        : channel
          ? ONLINE
          : MISSING_CHANNEL;
  }

  public Component message() {
    return switch (this) {
      case ONLINE -> Component.translatable("mmr.gui.me_network.online").withStyle(ChatFormatting.GREEN);
      case OFFLINE -> Component.translatable("mmr.gui.me_network.offline").withStyle(ChatFormatting.RED);
      case MISSING_CHANNEL -> Component.translatable("mmr.gui.me_network.channel").withStyle(ChatFormatting.GOLD);
      case BOOTING -> Component.translatable("mmr.gui.me_network.booting").withStyle(ChatFormatting.GRAY);
    };
  }
}
