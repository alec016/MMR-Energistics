package es.degrassi.mmreborn.energistics.common.data;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.energistics.common.data.config.MMRMEHatchConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = ModularMachineryReborn.MODID)
public class MMRConfig extends PartitioningSerializer.GlobalData {
  @ConfigEntry.Category("me_hatch")
  @ConfigEntry.Gui.TransitiveObject
  public MMRMEHatchConfig meHatch = new MMRMEHatchConfig();

  public static MMRConfig get() {
    return AutoConfig.getConfigHolder(MMRConfig.class).getConfig();
  }
}
