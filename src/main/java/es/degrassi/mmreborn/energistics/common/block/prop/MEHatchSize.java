package es.degrassi.mmreborn.energistics.common.block.prop;

import es.degrassi.mmreborn.energistics.common.data.MMRConfig;
import lombok.Getter;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum MEHatchSize implements StringRepresentable {
  ME_INPUT_BUS(true, 9),
  ME_ADVANCED_INPUT_BUS(true, true, 9*4),
  ME_OUTPUT_BUS(false, 9),
  ME_ADVANCED_OUTPUT_BUS(false, true,9*2),
  ME_INPUT_HATCH(true, 9),
  ME_ADVANCED_INPUT_HATCH(true, true, 9*4),
  ME_OUTPUT_HATCH(false, 9),
  ME_ADVANCED_OUTPUT_HATCH(false, true,9*2),
  ME_INPUT_CHEMICAL_HATCH(true, 9),
  ME_ADVANCED_INPUT_CHEMICAL_HATCH(true, true, 9*4),
  ME_OUTPUT_CHEMICAL_HATCH(false, 9),
  ME_ADVANCED_OUTPUT_CHEMICAL_HATCH(false, true, 9*2),
  ME_INPUT_SOURCE_HATCH(true, 1),
  ME_ADVANCED_INPUT_SOURCE_HATCH(true, true, 1),
  ME_OUTPUT_SOURCE_HATCH(false, 1),
  ME_ADVANCED_OUTPUT_SOURCE_HATCH(false, true, 1),
  ME_INPUT_EXPERIENCE_HATCH(true, 1),
  ME_ADVANCED_INPUT_EXPERIENCE_HATCH(true, true, 1),
  ME_OUTPUT_EXPERIENCE_HATCH(false, 1),
  ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH(false, true, 1),
  ME_PATTERN_BUS(true, 9),
  ME_ADVANCED_PATTERN_BUS(true, true, 36);

  @Getter
  private double idlePowerDrainOnConnected;
  @Getter
  private final boolean advanced;
  @Getter
  private final boolean input;
  @Getter
  private final int slots;
  public final double defaultIdlePowerDrainOnConnected;

  MEHatchSize(boolean input, boolean advanced, int slots) {
    this.defaultIdlePowerDrainOnConnected = advanced ? 3.0 : 1.0;
    this.advanced = advanced;
    this.slots = slots;
    this.input = input;
  }
  MEHatchSize(boolean input, int slots) {
    this(input, false, slots);
  }

  public static void loadFromConfig() {
    for (MEHatchSize size : MEHatchSize.values()) {
      size.idlePowerDrainOnConnected = MMRConfig.get().idlePowerDrainOnConnected(size);
    }
  }

  @Override
  public @NotNull String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
