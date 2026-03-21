package es.degrassi.mmreborn.energistics.common.block;

import appeng.api.orientation.IOrientableBlock;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.api.util.AEColor;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import es.degrassi.mmreborn.common.block.BlockDynamicColor;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.item.MEItem;
import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static es.degrassi.mmreborn.common.block.BlockMachineComponent.CONNECT_TEXTURES;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MEBlock<T extends MachineComponent<X>, X> extends AEBaseEntityBlock<MEEntity<T, X>> implements IOrientableBlock, EntityBlock, BlockDynamicColor {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  protected final MEHatchSize size;

  public MEBlock(MEHatchSize size) {
    super(
        Properties.of()
            .strength(2F, 10F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
            .dynamicShape()
            .noOcclusion()
    );
    this.size = size;
  }

  @Override
  public final void setBlockEntity(
    Class<MEEntity<T, X>> blockEntityClass,
    BlockEntityType<MEEntity<T, X>> blockEntityType,
    BlockEntityTicker<MEEntity<T, X>> clientTicker,
    BlockEntityTicker<MEEntity<T, X>> serverTicker
  ) {
    super.setBlockEntity(
        blockEntityClass,
        blockEntityType,
        (level, pos, state, be) -> be.tick(),
        (level, pos, state, be) -> be.tick()
    );
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(ACTIVE)
        .add(CONNECT_TEXTURES);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    var state = this.defaultBlockState();
    return getOrientationStrategy()
        .setFacing(state, context.getNearestLookingDirection().getOpposite())
        .setValue(ACTIVE, false)
        .setValue(CONNECT_TEXTURES, true);
  }

  @Override
  protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
    alertDevice(level, pos, neighborPos);
  }

  @Override
  public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighborPos) {
    alertDevice(level, pos, neighborPos);
  }

  public void alertDevice(LevelReader level, BlockPos pos, BlockPos neighborPos) {
    if (level.isClientSide()) return;
    if (level.getBlockEntity(pos) instanceof MEEntity<?, ?> machine) {
      machine.onOrientationChanged(machine.getOrientation());
      machine.getMainNode().ifPresent((grid, node) -> {
        grid.getTickManager().alertDevice(node);
      });
    }
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    var parent = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    if (parent.result() != InteractionResult.PASS)
      return parent;

    if (InteractionUtil.isInAlternateUseMode(player))
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    boolean consumeWater = false;
    boolean consumeDye = false;
    AEColor color = null;
    if (stack.is(Tags.Items.DYES) && stack.getItem() instanceof DyeItem dye) {
      color = AEColor.fromDye(dye.getDyeColor());
      consumeDye = !player.isCreative();
    }

    if (stack.is(Tags.Items.BUCKETS_WATER)) {
      color = AEColor.TRANSPARENT;
      consumeWater = !player.isCreative();
    }

    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof MEEntity<?, ?> machine) {
      if (color != null) {
        if (machine.getGridColor() == color) {
          return openMenu(machine, player, te);
        }
        machine.setGridColor(color);
        int index = player.getInventory().findSlotMatchingItem(stack);
        if (consumeWater) {
          player.getInventory().removeItem(index, 1);
          player.getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET));
        }
        if (consumeDye) {
          player.getInventory().removeItem(index, 1);
        }
        machine.markForUpdate();
        return ItemInteractionResult.CONSUME;
      }
      return openMenu(machine, player, te);
    }
    return ItemInteractionResult.FAIL;
  }

  private ItemInteractionResult openMenu(MEEntity<?, ?> machine, Player player, BlockEntity te) {
    machine.openMenu(player, MenuLocators.forBlockEntity(te));
    return ItemInteractionResult.SUCCESS;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof MEEntity<?, ?> machine) {
      machine.openMenu(player, MenuLocators.forBlockEntity(te));
      return InteractionResult.sidedSuccess(level.isClientSide());
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  @SuppressWarnings("unchecked")
  public MEItem<T, X> item() {
    return (MEItem<T, X>) switch (size) {
      case ME_INPUT_BUS -> BlockRegistration.ME_INPUT_BUS.item().get();
      case ME_ADVANCED_INPUT_BUS -> BlockRegistration.ME_ADVANCED_INPUT_BUS.item().get();
      case ME_OUTPUT_BUS -> BlockRegistration.ME_OUTPUT_BUS.item().get();
      case ME_ADVANCED_OUTPUT_BUS -> BlockRegistration.ME_ADVANCED_OUTPUT_BUS.item().get();
      case ME_INPUT_HATCH -> BlockRegistration.ME_INPUT_HATCH.item().get();
      case ME_ADVANCED_INPUT_HATCH -> BlockRegistration.ME_ADVANCED_INPUT_HATCH.item().get();
      case ME_OUTPUT_HATCH -> BlockRegistration.ME_OUTPUT_HATCH.item().get();
      case ME_ADVANCED_OUTPUT_HATCH -> BlockRegistration.ME_ADVANCED_OUTPUT_HATCH.item().get();
      case ME_INPUT_CHEMICAL_HATCH -> BlockRegistration.ME_INPUT_CHEMICAL_HATCH.item().get();
      case ME_ADVANCED_INPUT_CHEMICAL_HATCH -> BlockRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.item().get();
      case ME_OUTPUT_CHEMICAL_HATCH -> BlockRegistration.ME_OUTPUT_CHEMICAL_HATCH.item().get();
      case ME_ADVANCED_OUTPUT_CHEMICAL_HATCH -> BlockRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.item().get();
      case ME_INPUT_SOURCE_HATCH -> BlockRegistration.ME_INPUT_SOURCE_HATCH.item().get();
      case ME_ADVANCED_INPUT_SOURCE_HATCH -> BlockRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.item().get();
      case ME_OUTPUT_SOURCE_HATCH -> BlockRegistration.ME_OUTPUT_SOURCE_HATCH.item().get();
      case ME_ADVANCED_OUTPUT_SOURCE_HATCH -> BlockRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.item().get();
      case ME_INPUT_EXPERIENCE_HATCH -> BlockRegistration.ME_INPUT_EXPERIENCE_HATCH.item().get();
      case ME_ADVANCED_INPUT_EXPERIENCE_HATCH -> BlockRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.item().get();
      case ME_OUTPUT_EXPERIENCE_HATCH -> BlockRegistration.ME_OUTPUT_EXPERIENCE_HATCH.item().get();
      case ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH -> BlockRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.item().get();
      case ME_PATTERN_BUS -> BlockRegistration.ME_PATTERN_BUS.item().get();
      case ME_ADVANCED_PATTERN_BUS -> BlockRegistration.ME_ADVANCED_PATTERN_BUS.item().get();
    };
  }

  @Override
  public IOrientationStrategy getOrientationStrategy() {
    return OrientationStrategies.facing();
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    var te = this.getBlockEntity(level, pos);
    if (te instanceof MEEntity<?, ?> invBlockEntity) {
      return invBlockEntity.getRedstoneSignal();
    }
    return 0;
  }
}
