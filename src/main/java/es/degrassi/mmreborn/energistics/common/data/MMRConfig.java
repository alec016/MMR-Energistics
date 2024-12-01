package es.degrassi.mmreborn.energistics.common.data;

import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class MMRConfig {
  private static final MMRConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<MMRConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(MMRConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public final ConfigValue<Double> ME_INPUT_BUS_size;

  public final ConfigValue<Double> ME_ADVANCED_INPUT_BUS_size;

  public final ConfigValue<Double> ME_OUTPUT_BUS_size;

  public final ConfigValue<Double> ME_ADVANCED_OUTPUT_BUS_size;

  public final ConfigValue<Double> ME_INPUT_HATCH_size;

  public final ConfigValue<Double> ME_ADVANCED_INPUT_HATCH_size;

  public final ConfigValue<Double> ME_OUTPUT_HATCH_size;

  public final ConfigValue<Double> ME_ADVANCED_OUTPUT_HATCH_size;

  public final ConfigValue<Long> ME_UPDATE_INTERVAL;

  public final ConfigValue<Long> ME_UPDATE_INTERVAL_ADVANCED;

  public MMRConfig(ModConfigSpec.Builder builder) {
    builder.push(MEHatchSize.ME_INPUT_BUS.getSerializedName());
    this.ME_INPUT_BUS_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_INPUT_BUS.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName());
    this.ME_ADVANCED_INPUT_BUS_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_ADVANCED_INPUT_BUS.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_OUTPUT_BUS.getSerializedName());
    this.ME_OUTPUT_BUS_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_OUTPUT_BUS.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName());
    this.ME_ADVANCED_OUTPUT_BUS_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_ADVANCED_OUTPUT_BUS.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_INPUT_HATCH.getSerializedName());
    this.ME_INPUT_HATCH_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_INPUT_HATCH.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName());
    this.ME_ADVANCED_INPUT_HATCH_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_ADVANCED_INPUT_HATCH.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_OUTPUT_HATCH.getSerializedName());
    this.ME_OUTPUT_HATCH_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_OUTPUT_HATCH.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push(MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName());
    this.ME_ADVANCED_OUTPUT_HATCH_size = builder
        .comment("Defines the idle power consumption when online in an ae2 network")
        .defineInRange("idlePowerDrain", MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.defaultIdlePowerDrainOnConnected, 1, Double.MAX_VALUE);
    builder.pop();
    builder.push("ME update interval");
    this.ME_UPDATE_INTERVAL = builder
        .comment("Defines the update interval of normal buses/hatches inventory retrieve/insert on ae2 network, default: 20")
        .defineInRange("me_update_interval", 20, 1, Long.MAX_VALUE);
    this.ME_UPDATE_INTERVAL_ADVANCED = builder
        .comment("Defines the update interval of advanced buses/hatches inventory retrieve/insert on ae2 network, default: 10")
        .defineInRange("me_update_interval", 10, 1, Long.MAX_VALUE);
    builder.pop();
  }

  public static MMRConfig get() {
    return INSTANCE;
  }

  public double idlePowerDrainOnConnected(MEHatchSize size) {
    return switch (size) {
      case ME_INPUT_BUS -> ME_INPUT_BUS_size.get();
      case ME_ADVANCED_INPUT_BUS -> ME_ADVANCED_INPUT_BUS_size.get();
      case ME_OUTPUT_BUS -> ME_OUTPUT_BUS_size.get();
      case ME_ADVANCED_OUTPUT_BUS -> ME_ADVANCED_OUTPUT_BUS_size.get();
      case ME_INPUT_HATCH, ME_INPUT_CHEMICAL_HATCH, ME_INPUT_SOURCE_HATCH -> ME_INPUT_HATCH_size.get();
      case ME_ADVANCED_INPUT_HATCH, ME_ADVANCED_INPUT_CHEMICAL_HATCH, ME_ADVANCED_INPUT_SOURCE_HATCH -> ME_ADVANCED_INPUT_HATCH_size.get();
      case ME_OUTPUT_HATCH, ME_OUTPUT_CHEMICAL_HATCH, ME_OUTPUT_SOURCE_HATCH -> ME_OUTPUT_HATCH_size.get();
      case ME_ADVANCED_OUTPUT_HATCH, ME_ADVANCED_OUTPUT_CHEMICAL_HATCH, ME_ADVANCED_OUTPUT_SOURCE_HATCH -> ME_ADVANCED_OUTPUT_HATCH_size.get();
    };
  }
}
