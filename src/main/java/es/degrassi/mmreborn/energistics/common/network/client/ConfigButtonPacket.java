package es.degrassi.mmreborn.energistics.common.network.client;

import appeng.api.config.Setting;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.AELog;
import appeng.core.network.ServerboundPacket;
import appeng.menu.AEBaseMenu;
import appeng.util.EnumCycler;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.api.services.settings.MMRESettings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record ConfigButtonPacket(Setting<?> option, boolean rotationDirection) implements ServerboundPacket {

  public static final StreamCodec<RegistryFriendlyByteBuf, ConfigButtonPacket> STREAM_CODEC = StreamCodec.ofMember(
      ConfigButtonPacket::write,
      ConfigButtonPacket::decode);

  public static final Type<ConfigButtonPacket> TYPE = new Type<>(ModularMachineryRebornEnergistics.rl("mmre_config_button"));

  @Override
  public Type<ConfigButtonPacket> type() {
    return TYPE;
  }

  public static ConfigButtonPacket decode(RegistryFriendlyByteBuf stream) {
    var option = MMRESettings.getOrThrow(stream.readUtf());
    var rotationDirection = stream.readBoolean();
    return new ConfigButtonPacket(option, rotationDirection);
  }

  public void write(RegistryFriendlyByteBuf data) {
    data.writeUtf(option.getName());
    data.writeBoolean(rotationDirection);
  }

  @Override
  public void handleOnServer(ServerPlayer player) {
    if (player.containerMenu instanceof AEBaseMenu baseMenu) {
      if (baseMenu.getTarget() instanceof IConfigurableObject configurableObject) {
        var cm = configurableObject.getConfigManager();
        if (cm.hasSetting(option)) {
          cycleSetting(cm, option);
        } else {
          AELog.info("Ignoring unsupported setting %s sent by client on %s", option,
              baseMenu.getTarget());
        }
      }
    }
  }

  private <T extends Enum<T>> void cycleSetting(IConfigManager cm, Setting<T> setting) {
    var currentValue = cm.getSetting(setting);
    var nextValue = EnumCycler.rotateEnum(currentValue, rotationDirection, setting.getValues());
    cm.putSetting(setting, nextValue);
  }
}
