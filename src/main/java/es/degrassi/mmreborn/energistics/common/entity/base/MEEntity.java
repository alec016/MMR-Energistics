package es.degrassi.mmreborn.energistics.common.entity.base;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.orientation.BlockOrientation;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.IPriorityHost;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuHostLocator;
import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.data.MMRConfig;
import es.degrassi.mmreborn.energistics.common.registration.EntityRegistration;
import es.degrassi.mmreborn.energistics.common.util.ITickSubscription;
import es.degrassi.mmreborn.energistics.common.util.TickableSubscription;
import es.degrassi.mmreborn.energistics.common.entity.IMEConnectedEntity;
import es.degrassi.mmreborn.energistics.common.util.GridNodeHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MEEntity extends BlockEntityRestrictedTick implements IMEConnectedEntity, ITickSubscription, MachineComponentEntity, IUpgradeableObject, ICraftingRequester, IPriorityHost, InterfaceLogicHost {
  private final MEHatchSize size;
  private final GridNodeHolder nodeHolder;
  protected final IActionSource actionSource;

  protected final List<TickableSubscription> waitingToAdd = new LinkedList<>();
  protected final List<TickableSubscription> serverTicks = new LinkedList<>();

  @Nullable
  protected TickableSubscription autoIOSubs;

  @Setter
  protected boolean isOnline;

  private final long offset;

  public MEEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(getEntityType(size), pos, blockState);
    this.size = size;
    this.nodeHolder = new GridNodeHolder(this);
    this.actionSource = IActionSource.ofMachine(this);
    this.offset = RandomSource.createThreadSafe().nextInt(20);
    this.onGridConnectableSidesChanged();
  }

  protected static int[] generateSlots(int slots) {
    int[] slot = new int[slots];

    for (int i = 0; i < slots; slot[i] = i++) {}

    return slot;
  }

  private static BlockEntityType<MEEntity> getEntityType(MEHatchSize size) {
    return switch (size) {
      case ME_INPUT_BUS -> EntityRegistration.ME_INPUT_BUS.get();
      case ME_ADVANCED_INPUT_BUS -> EntityRegistration.ME_ADVANCED_INPUT_BUS.get();
      case ME_OUTPUT_BUS -> EntityRegistration.ME_OUTPUT_BUS.get();
      case ME_ADVANCED_OUTPUT_BUS -> EntityRegistration.ME_ADVANCED_OUTPUT_BUS.get();
      case ME_INPUT_HATCH -> EntityRegistration.ME_INPUT_HATCH.get();
      case ME_ADVANCED_INPUT_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_HATCH.get();
      case ME_OUTPUT_HATCH -> EntityRegistration.ME_OUTPUT_HATCH.get();
      case ME_ADVANCED_OUTPUT_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_HATCH.get();
      case ME_INPUT_CHEMICAL_HATCH -> EntityRegistration.ME_INPUT_CHEMICAL_HATCH.get();
      case ME_ADVANCED_INPUT_CHEMICAL_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH.get();
      case ME_OUTPUT_CHEMICAL_HATCH -> EntityRegistration.ME_OUTPUT_CHEMICAL_HATCH.get();
      case ME_ADVANCED_OUTPUT_CHEMICAL_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH.get();
      case ME_INPUT_SOURCE_HATCH -> EntityRegistration.ME_INPUT_SOURCE_HATCH.get();
      case ME_ADVANCED_INPUT_SOURCE_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH.get();
      case ME_OUTPUT_SOURCE_HATCH -> EntityRegistration.ME_OUTPUT_SOURCE_HATCH.get();
      case ME_ADVANCED_OUTPUT_SOURCE_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH.get();
      case ME_INPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_INPUT_EXPERIENCE_HATCH.get();
      case ME_ADVANCED_INPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH.get();
      case ME_OUTPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_OUTPUT_EXPERIENCE_HATCH.get();
      case ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH.get();
    };
  }

  @Override
  public IManagedGridNode getMainNode() {
    return nodeHolder.getMainNode();
  }

  @Override
  public void doRestrictedTick() {
    this.executeTick();
    if (this.getBlockState().getValue(MEBlock.ACTIVE) != isOnline) {
      this.setBlockState(this.getBlockState().setValue(MEBlock.ACTIVE, isOnline));
      this.updateMEStatus();
      this.updateSubscription();
      this.markForUpdate();
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
  }

  @Override
  public void clearRemoved() {
    super.clearRemoved();
    this.scheduleInit();
  }

  @Override
  public void setRemoved() {
    serverTicks.forEach(TickableSubscription::unsubscribe);
    serverTicks.clear();
    super.setRemoved();
    nodeHolder.onUnloaded();
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    nodeHolder.onUnloaded();
  }

  private byte queuedForReady = 0;

  protected void scheduleInit() {
    queuedForReady++;
    GridHelper.onFirstTick(this, MEEntity::onReady);
  }

  private byte readyInvoked = 0;

  private void onReady() {
    readyInvoked++;
    nodeHolder.onLoad();
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    this.getMainNode().saveToNBT(nbt);
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    this.getMainNode().loadFromNBT(nbt);
  }

  private void executeTick() {
    if (!waitingToAdd.isEmpty()) {
      serverTicks.addAll(waitingToAdd);
      waitingToAdd.clear();
    }
    var iter = serverTicks.iterator();
    while (iter.hasNext()) {
      var tickable = iter.next();
      if (tickable.isStillSubscribed()) {
        tickable.run();
      }
      if (isRemoved()) break;
      if (!tickable.isStillSubscribed()) {
        iter.remove();
      }
    }
  }

  @Override
  public final void onMainNodeStateChanged(IGridNodeListener.State reason) {
    IMEConnectedEntity.super.onMainNodeStateChanged(reason);
    this.updateSubscription();
  }

  public void updateSubscription() {
    if (shouldSubscribe()) {
      autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
    } else if (autoIOSubs != null) {
      autoIOSubs.unsubscribe();
      autoIOSubs = null;
    }
  }

  public void autoIO() {
    if (!this.shouldSyncME()) return;

    if (this.updateMEStatus()) {
      this.syncME();
      this.updateSubscription();
    }
  }

  protected abstract void syncME();

  protected final boolean shouldSubscribe() {
    return isOnline();
  }

  @Nullable
  public final TickableSubscription subscribeServerTick(Runnable runnable) {
    if (!getLevel().isClientSide()) {
      TickableSubscription sub = new TickableSubscription(runnable);
      waitingToAdd.add(sub);
      return sub;
    }
    return null;
  }

  public final void unsubscribe(@Nullable TickableSubscription current) {
    if(current != null) {
      current.unsubscribe();
    }
  }

  public final long getOffsetTimer() {
    return level == null ? getOffset() : (level.getGameTime() + getOffset());
  }

  @Override
  public final long getMEUpdateInterval() {
    return size.isAdvanced() ? MMRConfig.get().ME_UPDATE_INTERVAL_ADVANCED.get() : MMRConfig.get().ME_UPDATE_INTERVAL.get();
  }

  public final void setBlockState(BlockState state) {
    BlockOrientation previousOrientation = getOrientation();
    super.setBlockState(state);
    BlockOrientation newOrientation = getOrientation();
    if (previousOrientation != newOrientation) {
      this.onOrientationChanged();
    }
  }

  public final void onOrientationChanged() {
    this.invalidateCapabilities();
    this.onGridConnectableSidesChanged();
  }

  public final void onGridConnectableSidesChanged() {
    this.getMainNode().setExposedOnSides(this.getGridConnectableSides(getOrientation()));
  }

  public final BlockOrientation getOrientation() {
    return BlockOrientation.get(getBlockState());
  }

  @Override
  public final BlockEntity getBlockEntity() {
    return this;
  }

  @Override
  public abstract InterfaceLogic getInterfaceLogic();

  public abstract void openMenu(Player player, MenuHostLocator locator);
  public abstract void returnToMainMenu(Player player, ISubMenu subMenu);

  @Override
  public final ItemStack getMainMenuIcon() {
    return ((MEBlock) getBlockState().getBlock()).item().getDefaultInstance();
  }

  @Override
  public void saveChanges() {
    markForUpdate();
  }

  public void markForUpdate() {
    if (this.isRequestModelUpdate()) {
      this.requestModelDataUpdate();
      this.requestModelDataUpdate();
    }

    this.setChanged();

    if (getLevel() == null) return;
    this.setRequestModelUpdate(false);
    this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState());
  }
}
