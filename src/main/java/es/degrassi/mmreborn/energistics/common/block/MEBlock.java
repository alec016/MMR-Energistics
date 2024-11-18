package es.degrassi.mmreborn.energistics.common.block;

import appeng.api.orientation.IOrientableBlock;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.IOwnerAwareBlockEntity;
import appeng.hooks.WrenchHook;
import appeng.me.helpers.IGridConnectedBlockEntity;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import es.degrassi.mmreborn.common.block.BlockMachineComponent;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.item.MEItem;
import es.degrassi.mmreborn.energistics.common.registration.ItemRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MEBlock extends BlockMachineComponent implements IOrientableBlock {
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
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(ACTIVE);

    for (Property<?> property : this.getOrientationStrategy().getProperties()) {
      builder.add(property);
    }
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.getOrientationStrategy().getStateForPlacement(defaultBlockState(), context).setValue(ACTIVE, false);
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    drops.add(item().getDefaultInstance());
    return drops;
  }

  public @Nullable BlockEntity getBlockEntity(BlockGetter level, BlockPos pos) {
    return level.getBlockEntity(pos);
  }

  protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
    if (!WrenchHook.isDisassembling()) {
      super.spawnDestroyParticles(level, player, pos, state);
    }
  }

  @Override
  protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
    alertDevice(level, state, pos, neighborPos);
  }

  @Override
  public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighborPos) {
    alertDevice(level, state, pos, neighborPos);
  }

  private void alertDevice(LevelReader level, BlockState state, BlockPos pos, BlockPos neighborPos) {
    if (level.isClientSide()) return;
    if (level.getBlockEntity(pos) instanceof MEEntity machine) {
      machine.onOrientationChanged();
      if (level.getBlockEntity(neighborPos) instanceof IGridConnectedBlockEntity gridEntity) {
        machine.getMainNode().ifPresent((grid, node) -> {
          grid.getTickManager().alertDevice(node);
        });
        gridEntity.getMainNode().ifPresent((grid, node) -> {
          grid.getTickManager().alertDevice(node);
        });
      }
    }
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    BlockEntity blockEntity = this.getBlockEntity(level, pos);
    if (blockEntity != null) {
      Player player;
      if (blockEntity instanceof IOwnerAwareBlockEntity ownerAware) {
        if (placer instanceof Player) {
          player = (Player) placer;
          ownerAware.setOwner(player);
        }
      }

//      player = null;
//      if (placer instanceof Player) {
//        player = (Player)placer;
//      }
//
//      blockEntity.importSettings(SettingsFrom.DISMANTLE_ITEM, stack.getComponents(), player);
    }
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    var parent = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    if (parent.result() != InteractionResult.PASS)
      return parent;

    if (InteractionUtil.isInAlternateUseMode(player))
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof MEEntity machine) {
      machine.openMenu(player, MenuLocators.forBlockEntity(te));
      return ItemInteractionResult.SUCCESS;
    }
    return ItemInteractionResult.FAIL;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof MEEntity machine) {
      machine.openMenu(player, MenuLocators.forBlockEntity(te));
      return InteractionResult.sidedSuccess(level.isClientSide());
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  @Nullable
  public MEItem item() {
    // TODO: add proper items
    return switch (size) {
      case ME_INPUT_BUS -> ItemRegistration.ME_INPUT_BUS.get();
      case ME_ADVANCED_INPUT_BUS -> ItemRegistration.ME_ADVANCED_INPUT_BUS.get();
      case ME_OUTPUT_BUS -> ItemRegistration.ME_OUTPUT_BUS.get();
      case ME_ADVANCED_OUTPUT_BUS -> ItemRegistration.ME_ADVANCED_OUTPUT_BUS.get();
      default -> null;
    };
  }

  @Override
  public IOrientationStrategy getOrientationStrategy() {
    return OrientationStrategies.full();
  }
}
