package es.degrassi.mmreborn.energistics.common.registration;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.DeferredBlockEntityType;
import com.google.common.base.Preconditions;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputBusEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputChemicalHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputExperienceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputSourceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputBusEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputChemicalHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputExperienceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.entity.base.MEPatternBus;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EntityRegistration {
  public static final List<DeferredBlockEntityType<?>> BLOCK_ENTITY_TYPES = new ArrayList<>();
  public static final DeferredRegister<BlockEntityType<?>> DR =
      DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModularMachineryRebornEnergistics.MODID);

  public static final DeferredBlockEntityType<MEInputBusEntity> ME_INPUT_BUS = create(
      MEHatchSize.ME_INPUT_BUS.getSerializedName(),
      MEInputBusEntity.class,
      (type, pos, state) -> new MEInputBusEntity(pos, state, MEHatchSize.ME_INPUT_BUS),
      BlockRegistration.ME_INPUT_BUS
  );
  public static final DeferredBlockEntityType<MEOutputBusEntity> ME_OUTPUT_BUS = create(
      MEHatchSize.ME_OUTPUT_BUS.getSerializedName(),
      MEOutputBusEntity.class,
      (type, pos, state) -> new MEOutputBusEntity(pos, state, MEHatchSize.ME_OUTPUT_BUS),
      BlockRegistration.ME_OUTPUT_BUS
  );
  public static final DeferredBlockEntityType<MEInputBusEntity> ME_ADVANCED_INPUT_BUS = create(
      MEHatchSize.ME_ADVANCED_INPUT_BUS.getSerializedName(),
      MEInputBusEntity.class,
      (type, pos, state) -> new MEInputBusEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_BUS),
      BlockRegistration.ME_ADVANCED_INPUT_BUS
  );
  public static final DeferredBlockEntityType<MEOutputBusEntity> ME_ADVANCED_OUTPUT_BUS = create(
      MEHatchSize.ME_ADVANCED_OUTPUT_BUS.getSerializedName(),
      MEOutputBusEntity.class,
      (type, pos, state) -> new MEOutputBusEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_BUS),
      BlockRegistration.ME_ADVANCED_OUTPUT_BUS
  );

  public static final DeferredBlockEntityType<MEInputHatchEntity> ME_INPUT_HATCH = create(
      MEHatchSize.ME_INPUT_HATCH.getSerializedName(),
      MEInputHatchEntity.class,
      (type, pos, state) -> new MEInputHatchEntity(pos, state, MEHatchSize.ME_INPUT_HATCH),
      BlockRegistration.ME_INPUT_HATCH
  );
  public static final DeferredBlockEntityType<MEOutputHatchEntity> ME_OUTPUT_HATCH = create(
      MEHatchSize.ME_OUTPUT_HATCH.getSerializedName(),
      MEOutputHatchEntity.class,
      (type, pos, state) -> new MEOutputHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_HATCH),
      BlockRegistration.ME_OUTPUT_HATCH
  );
  public static final DeferredBlockEntityType<MEInputHatchEntity> ME_ADVANCED_INPUT_HATCH = create(
      MEHatchSize.ME_ADVANCED_INPUT_HATCH.getSerializedName(),
      MEInputHatchEntity.class,
      (type, pos, state) -> new MEInputHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_HATCH),
      BlockRegistration.ME_ADVANCED_INPUT_HATCH
  );
  public static final DeferredBlockEntityType<MEOutputHatchEntity> ME_ADVANCED_OUTPUT_HATCH = create(
      MEHatchSize.ME_ADVANCED_OUTPUT_HATCH.getSerializedName(),
      MEOutputHatchEntity.class,
      (type, pos, state) -> new MEOutputHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_HATCH),
      BlockRegistration.ME_ADVANCED_OUTPUT_HATCH
  );
  public static final DeferredBlockEntityType<MEPatternBus> ME_PATTERN_BUS = create(
      MEHatchSize.ME_PATTERN_BUS.getSerializedName(),
      MEPatternBus.class,
      (type, pos, state) -> new MEPatternBus(pos, state, MEHatchSize.ME_PATTERN_BUS),
      BlockRegistration.ME_PATTERN_BUS
  );
  public static final DeferredBlockEntityType<MEPatternBus> ME_ADVANCED_PATTERN_BUS = create(
      MEHatchSize.ME_ADVANCED_PATTERN_BUS.getSerializedName(),
      MEPatternBus.class,
      (type, pos, state) -> new MEPatternBus(pos, state, MEHatchSize.ME_ADVANCED_PATTERN_BUS),
      BlockRegistration.ME_ADVANCED_PATTERN_BUS
  );

  public static final DeferredBlockEntityType<MEInputChemicalHatchEntity> ME_ADVANCED_INPUT_CHEMICAL_HATCH;
  public static final DeferredBlockEntityType<MEInputChemicalHatchEntity> ME_INPUT_CHEMICAL_HATCH;
  public static final DeferredBlockEntityType<MEOutputChemicalHatchEntity> ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;
  public static final DeferredBlockEntityType<MEOutputChemicalHatchEntity> ME_OUTPUT_CHEMICAL_HATCH;

  public static final DeferredBlockEntityType<MEInputSourceHatchEntity> ME_ADVANCED_INPUT_SOURCE_HATCH;
  public static final DeferredBlockEntityType<MEInputSourceHatchEntity> ME_INPUT_SOURCE_HATCH;
  public static final DeferredBlockEntityType<MEOutputSourceHatchEntity> ME_ADVANCED_OUTPUT_SOURCE_HATCH;
  public static final DeferredBlockEntityType<MEOutputSourceHatchEntity> ME_OUTPUT_SOURCE_HATCH;

  public static final DeferredBlockEntityType<MEInputExperienceHatchEntity> ME_ADVANCED_INPUT_EXPERIENCE_HATCH;
  public static final DeferredBlockEntityType<MEInputExperienceHatchEntity> ME_INPUT_EXPERIENCE_HATCH;
  public static final DeferredBlockEntityType<MEOutputExperienceHatchEntity> ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH;
  public static final DeferredBlockEntityType<MEOutputExperienceHatchEntity> ME_OUTPUT_EXPERIENCE_HATCH;

  static {
    if (Mods.isMekPossible()) {
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = create(
          MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH.getSerializedName(),
          MEInputChemicalHatchEntity.class,
          (type, pos, state) -> new MEInputChemicalHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_CHEMICAL_HATCH),
          BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH
      );
      ME_INPUT_CHEMICAL_HATCH = create(
          MEHatchSize.ME_INPUT_CHEMICAL_HATCH.getSerializedName(),
          MEInputChemicalHatchEntity.class,
          (type, pos, state) -> new MEInputChemicalHatchEntity(pos, state, MEHatchSize.ME_INPUT_CHEMICAL_HATCH),
          BlockRegistration.ME_INPUT_CHEMICAL_HATCH
      );
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = create(
          MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
          MEOutputChemicalHatchEntity.class,
          (type, pos, state) -> new MEOutputChemicalHatchEntity(pos, state,
              MEHatchSize.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH),
          BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH
      );
      ME_OUTPUT_CHEMICAL_HATCH = create(
          MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH.getSerializedName(),
          MEOutputChemicalHatchEntity.class,
          (type, pos, state) -> new MEOutputChemicalHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_CHEMICAL_HATCH),
          BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH
      );
    } else {
      ME_INPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_INPUT_CHEMICAL_HATCH = null;
      ME_OUTPUT_CHEMICAL_HATCH = null;
      ME_ADVANCED_OUTPUT_CHEMICAL_HATCH = null;
    }

    if (Mods.isArsPossible()) {
      ME_INPUT_SOURCE_HATCH = create(
          MEHatchSize.ME_INPUT_SOURCE_HATCH.getSerializedName(),
          MEInputSourceHatchEntity.class,
          (type, pos, state) -> new MEInputSourceHatchEntity(pos, state, MEHatchSize.ME_INPUT_SOURCE_HATCH),
          BlockRegistration.ME_INPUT_SOURCE_HATCH
      );
      ME_ADVANCED_INPUT_SOURCE_HATCH = create(
          MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH.getSerializedName(),
          MEInputSourceHatchEntity.class,
          (type, pos, state) -> new MEInputSourceHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_SOURCE_HATCH),
          BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH
      );
      ME_OUTPUT_SOURCE_HATCH = create(
          MEHatchSize.ME_OUTPUT_SOURCE_HATCH.getSerializedName(),
          MEOutputSourceHatchEntity.class,
          (type, pos, state) -> new MEOutputSourceHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_SOURCE_HATCH),
          BlockRegistration.ME_OUTPUT_SOURCE_HATCH
      );
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = create(
          MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH.getSerializedName(),
          MEOutputSourceHatchEntity.class,
          (type, pos, state) -> new MEOutputSourceHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_SOURCE_HATCH),
          BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH
      );
    } else {
      ME_INPUT_SOURCE_HATCH = null;
      ME_ADVANCED_INPUT_SOURCE_HATCH = null;
      ME_OUTPUT_SOURCE_HATCH = null;
      ME_ADVANCED_OUTPUT_SOURCE_HATCH = null;
    }

    if (Mods.isExperiencePossible()) {
      ME_INPUT_EXPERIENCE_HATCH = create(
          MEHatchSize.ME_INPUT_EXPERIENCE_HATCH.getSerializedName(),
          MEInputExperienceHatchEntity.class,
          (type, pos, state) -> new MEInputExperienceHatchEntity(pos, state, MEHatchSize.ME_INPUT_EXPERIENCE_HATCH),
          BlockRegistration.ME_INPUT_EXPERIENCE_HATCH
      );
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH = create(
          MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.getSerializedName(),
          MEInputExperienceHatchEntity.class,
          (type, pos, state) -> new MEInputExperienceHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_INPUT_EXPERIENCE_HATCH),
          BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH
      );
      ME_OUTPUT_EXPERIENCE_HATCH = create(
          MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
          MEOutputExperienceHatchEntity.class,
          (type, pos, state) -> new MEOutputExperienceHatchEntity(pos, state, MEHatchSize.ME_OUTPUT_EXPERIENCE_HATCH),
          BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH
      );
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH = create(
          MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.getSerializedName(),
          MEOutputExperienceHatchEntity.class,
          (type, pos, state) -> new MEOutputExperienceHatchEntity(pos, state, MEHatchSize.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH),
          BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH
      );
    } else {
      ME_INPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_INPUT_EXPERIENCE_HATCH = null;
      ME_OUTPUT_EXPERIENCE_HATCH = null;
      ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH = null;
    }
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  private static <T extends MEEntity<C, X>, C extends MachineComponent<X>, X> DeferredBlockEntityType<T> create(String shortId,
                                                                                                                Class<T> entityClass,
                                                                                                                BlockEntityFactory<T, C, X> factory,
                                                                                                                BlockDefinition<? extends MEBlock<C, X>>... blockDefinitions) {
    Preconditions.checkArgument(blockDefinitions.length > 0);

    var deferred = DR.register(shortId, () -> {
      AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
      BlockEntityType.BlockEntitySupplier<T> supplier =
          (blockPos, blockState) -> factory.create(typeHolder.get(), blockPos, blockState);

      var blocks = Arrays.stream(blockDefinitions)
          .map(BlockDefinition::block)
          .toArray(AEBaseEntityBlock[]::new);

      var type = BlockEntityType.Builder.of(supplier, blocks).build(null);
      typeHolder.setPlain(type); // Makes it available to the supplier used above

      AEBaseBlockEntity.registerBlockEntityItem(type, blockDefinitions[0].asItem());

      // If the block entity classes implement specific interfaces, automatically register them
      // as tickers with the blocks that create that entity.
      BlockEntityTicker<T> serverTicker = null;
      if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
        serverTicker = (level, pos, state, entity) -> {
          ((ServerTickingBlockEntity) entity).serverTick();
        };
      }
      BlockEntityTicker<T> clientTicker = null;
      if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
        clientTicker = (level, pos, state, entity) -> {
          ((ClientTickingBlockEntity) entity).clientTick();
        };
      }

      for (var block : blocks) {
        AEBaseEntityBlock<T> baseBlock = (AEBaseEntityBlock<T>) block;
        baseBlock.setBlockEntity(entityClass, type, clientTicker, serverTicker);
      }

      return type;
    });

    var result = new DeferredBlockEntityType<>(entityClass, deferred);
    BLOCK_ENTITY_TYPES.add(result);
    return result;
  }

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }

  public static List<BlockEntityType<?>> getImplementorsOf(Class<?> iface) {
    var result = new ArrayList<BlockEntityType<?>>();
    for (var type : BLOCK_ENTITY_TYPES) {
      if (iface.isAssignableFrom(type.getBlockEntityClass())) {
        result.add(type.get());
      }
    }
    return result;
  }

  @FunctionalInterface
  interface BlockEntityFactory<T extends MEEntity<C, X>, C extends MachineComponent<X>, X> {
    T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
  }
}
