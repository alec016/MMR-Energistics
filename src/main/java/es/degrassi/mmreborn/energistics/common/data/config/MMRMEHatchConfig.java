package es.degrassi.mmreborn.energistics.common.data.config;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "me_hatch")
public class MMRMEHatchConfig implements ConfigData {
  @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
  public Tier ME_INPUT_BUS = new Tier(MEHatchSize.ME_INPUT_BUS);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_ADVANCED_INPUT_BUS = new Tier(MEHatchSize.ME_ADVANCED_INPUT_BUS);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_OUTPUT_BUS = new Tier(MEHatchSize.ME_OUTPUT_BUS);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_ADVANCED_OUTPUT_BUS = new Tier(MEHatchSize.ME_ADVANCED_OUTPUT_BUS);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_INPUT_HATCH = new Tier(MEHatchSize.ME_INPUT_HATCH);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_ADVANCED_INPUT_HATCH = new Tier(MEHatchSize.ME_ADVANCED_INPUT_HATCH);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_OUTPUT_HATCH = new Tier(MEHatchSize.ME_OUTPUT_HATCH);

  @ConfigEntry.Gui.CollapsibleObject
  public Tier ME_ADVANCED_OUTPUT_HATCH = new Tier(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH);

  @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
  @Comment("Defines the update interval of normal buses/hatches inventory retrieve/insert on ae2 network, default: 20")
  public long ME_UPDATE_INTERVAL = 20;

  @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
  @Comment("Defines the update interval of advanced buses/hatches inventory retrieve/insert on ae2 network, default: 10")
  public long ME_UPDATE_INTERVAL_ADVANCED = 10;

  public static final class Tier {
    @ConfigEntry.BoundedDiscrete(max = Long.MAX_VALUE)
    @Comment("Defines the idle power consumption when online in an ae2 network")
    public double size;

    public Tier(MEHatchSize size) {
      this.size = size.defaultIdlePowerDrainOnConnected;
    }
  }

  public double idlePowerDrainOnConnected(MEHatchSize size) {
    return switch (size) {
      case ME_INPUT_BUS -> ME_INPUT_BUS.size;
      case ME_ADVANCED_INPUT_BUS -> ME_ADVANCED_INPUT_BUS.size;
      case ME_OUTPUT_BUS -> ME_OUTPUT_BUS.size;
      case ME_ADVANCED_OUTPUT_BUS -> ME_ADVANCED_OUTPUT_BUS.size;
      case ME_INPUT_HATCH -> ME_INPUT_HATCH.size;
      case ME_ADVANCED_INPUT_HATCH -> ME_ADVANCED_INPUT_HATCH.size;
      case ME_OUTPUT_HATCH -> ME_OUTPUT_HATCH.size;
      case ME_ADVANCED_OUTPUT_HATCH -> ME_ADVANCED_OUTPUT_HATCH.size;
    };
  }
}
