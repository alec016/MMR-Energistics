package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputBusEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputChemicalHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputSourceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputBusEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputChemicalHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class EntityRegistration {
  public static final DeferredRegister<BlockEntityType<?>> ENTITY_TYPE =
      DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModularMachineryRebornEnergistics.MODID);

  public static final Supplier<BlockEntityType<MEEntity>> ME_INPUT_BUS = ENTITY_TYPE.register(
      MEHatchSize.ME_INPUT_BUS.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEInputBusEntity(pos, state, MEHatchSize.ME_INPUT_BUS),
          Set.of(BlockRegistration.ME_INPUT_BUS.get()),
          null)
  );
  public static final Supplier<BlockEntityType<MEEntity>> ME_OUTPUT_BUS = ENTITY_TYPE.register(
      MEHatchSize.ME_OUTPUT_BUS.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEOutputBusEntity(pos, state, MEHatchSize.ME_OUTPUT_BUS),
          Set.of(BlockRegistration.ME_OUTPUT_BUS.get()),
          null)
  );
  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_INPUT_BUS = ENTITY_TYPE.register(
      MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEInputBusEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_BUS),
          Set.of(BlockRegistration.ME_ADVANCED_INPUT_BUS.get()),
          null)
  );
  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_OUTPUT_BUS = ENTITY_TYPE.register(
      MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEOutputBusEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_BUS),
          Set.of(BlockRegistration.ME_ADVANCED_OUTPUT_BUS.get()),
          null)
  );

  public static final Supplier<BlockEntityType<MEEntity>> ME_INPUT_HATCH = ENTITY_TYPE.register(
      MEHatchSize.ME_INPUT_HATCH.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEInputHatchEntity(pos, state, MEHatchSize.ME_INPUT_HATCH),
          Set.of(BlockRegistration.ME_INPUT_HATCH.get()),
          null)
  );
  public static final Supplier<BlockEntityType<MEEntity>> ME_OUTPUT_HATCH = ENTITY_TYPE.register(
      MEHatchSize.ME_OUTPUT_HATCH.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEOutputHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_HATCH),
          Set.of(BlockRegistration.ME_OUTPUT_HATCH.get()),
          null)
  );
  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_INPUT_HATCH = ENTITY_TYPE.register(
      MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEInputHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_HATCH),
          Set.of(BlockRegistration.ME_ADVANCED_INPUT_HATCH.get()),
          null)
  );
  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_OUTPUT_HATCH = ENTITY_TYPE.register(
      MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName(),
      () -> new BlockEntityType<>(
          (pos, state) -> new MEOutputHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_HATCH),
          Set.of(BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.get()),
          null)
  );

  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_INPUT_CHEMICAL_HATCH;
  public static final Supplier<BlockEntityType<MEEntity>> ME_INPUT_CHEMICAL_HATCH;
  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;
  public static final Supplier<BlockEntityType<MEEntity>> ME_OUTPUT_CHEMICAL_HATCH;

  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_INPUT_SOURCE_HATCH;
  public static final Supplier<BlockEntityType<MEEntity>> ME_INPUT_SOURCE_HATCH;
  public static final Supplier<BlockEntityType<MEEntity>> ME_ADVANCED_OUTPUT_SOURCE_HATCH;
  public static final Supplier<BlockEntityType<MEEntity>> ME_OUTPUT_SOURCE_HATCH;

  static {
    if (Mods.isMekPossible()) {
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEInputChemicalHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH),
              Set.of(BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get()),
              null)
      );
      ME_INPUT_CHEMICAL_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_INPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEInputChemicalHatchEntity(pos, state, MEHatchSize.ME_INPUT_CHEMICAL_HATCH),
              Set.of(BlockRegistration.ME_INPUT_CHEMICAL_HATCH.get()),
              null)
      );
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEOutputChemicalHatchEntity(pos, state,
                  MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH),
              Set.of(BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.get()),
              null)
      );
      ME_OUTPUT_CHEMICAL_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEOutputChemicalHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH),
              Set.of(BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH.get()),
              null)
      );
    } else {
      ME_INPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = null;
      ME_OUTPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = null;
    }

    if (Mods.isArsPossible()) {
      ME_INPUT_SOURCE_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_INPUT_SOURCE_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEInputSourceHatchEntity(pos, state, MEHatchSize.ME_INPUT_SOURCE_HATCH),
              Set.of(BlockRegistration.ME_INPUT_SOURCE_HATCH.get()),
              null)
      );
      ME_ADVANCED_INPUT_SOURCE_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEInputSourceHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH),
              Set.of(BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get()),
              null)
      );
      ME_OUTPUT_SOURCE_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_OUTPUT_SOURCE_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEOutputSourceHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_SOURCE_HATCH),
              Set.of(BlockRegistration.ME_OUTPUT_SOURCE_HATCH.get()),
              null)
      );
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = ENTITY_TYPE.register(
          MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH.getSerializedName(),
          () -> new BlockEntityType<>(
              (pos, state) -> new MEOutputSourceHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH),
              Set.of(BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.get()),
              null)
      );
    } else {
      ME_INPUT_SOURCE_HATCH = null;
      ME_ADVANCED_INPUT_SOURCE_HATCH = null;
      ME_OUTPUT_SOURCE_HATCH = null;
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = null;
    }
  }


  public static void register(final IEventBus bus) {
    ENTITY_TYPE.register(bus);
  }
}
