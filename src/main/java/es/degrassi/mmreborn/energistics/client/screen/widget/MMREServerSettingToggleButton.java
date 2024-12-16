package es.degrassi.mmreborn.energistics.client.screen.widget;

import appeng.api.config.Setting;
import appeng.core.network.ServerboundPacket;
import es.degrassi.mmreborn.energistics.common.network.client.ConfigButtonPacket;
import net.neoforged.neoforge.network.PacketDistributor;

public class MMREServerSettingToggleButton<T extends Enum<T>> extends MMRESettingToggleButton<T> {

  public MMREServerSettingToggleButton(Setting<T> setting, T val) {
    super(setting, val, MMREServerSettingToggleButton::sendToServer);
  }

  private static <T extends Enum<T>> void sendToServer(MMRESettingToggleButton<T> button, boolean backwards) {
    ServerboundPacket message = new ConfigButtonPacket(button.getSetting(), backwards);
    PacketDistributor.sendToServer(message);
  }

}
