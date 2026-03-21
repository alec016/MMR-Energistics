package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.common.machine.MachineHatchType;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

import static es.degrassi.mmreborn.ModularMachineryReborn.rootLC;

public class MachineHatchTypeRegistration {
  private MachineHatchTypeRegistration() {}

  public static final DeferredRegister<MachineHatchType> MACHINE_COMPONENTS =
      DeferredRegister.create(MachineHatchType.REGISTRY_KEY, ModularMachineryRebornEnergistics.MODID);

  public static final Supplier<MachineHatchType> ME_INPUT_BUS =
      MACHINE_COMPONENTS.register(rootLC("ME_INPUT_BUS".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_INPUT_BUS =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_INPUT_BUS".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  public static final Supplier<MachineHatchType> ME_OUTPUT_BUS =
      MACHINE_COMPONENTS.register(rootLC("ME_OUTPUT_BUS".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_OUTPUT_BUS =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_OUTPUT_BUS".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  public static final Supplier<MachineHatchType> ME_INPUT_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_INPUT_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_INPUT_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_INPUT_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  public static final Supplier<MachineHatchType> ME_OUTPUT_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_OUTPUT_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_OUTPUT_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_OUTPUT_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  public static final Supplier<MachineHatchType> ME_PATTERN_BUS =
      MACHINE_COMPONENTS.register(rootLC("ME_PATTERN_BUS".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_PATTERN_BUS =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_PATTERN_BUS".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  // Experience Addon
  public static final Supplier<MachineHatchType> ME_INPUT_EXPERIENCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_INPUT_EXPERIENCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_INPUT_EXPERIENCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_INPUT_EXPERIENCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_OUTPUT_EXPERIENCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_OUTPUT_EXPERIENCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  // Ars Addon
  public static final Supplier<MachineHatchType> ME_INPUT_SOURCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_INPUT_SOURCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_INPUT_SOURCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_INPUT_SOURCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_OUTPUT_SOURCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_OUTPUT_SOURCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_OUTPUT_SOURCE_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_OUTPUT_SOURCE_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  // Mekanism Addon
  public static final Supplier<MachineHatchType> ME_INPUT_CHEMICAL_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_INPUT_CHEMICAL_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_INPUT_CHEMICAL_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_INPUT_CHEMICAL_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_OUTPUT_CHEMICAL_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_OUTPUT_CHEMICAL_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);
  public static final Supplier<MachineHatchType> ME_ADVANCED_OUTPUT_CHEMICAL_HATCH =
      MACHINE_COMPONENTS.register(rootLC("ME_ADVANCED_OUTPUT_CHEMICAL_HATCH".toLowerCase(Locale.ENGLISH)), MachineHatchType::create);

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
