package es.degrassi.mmreborn.energistics.common.entity.base;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.util.AEColor;
import appeng.api.util.IConfigManager;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.IPriorityHost;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.MultiCraftingTracker;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.MachineSource;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.IWrenchable;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.api.controller.ControllerAttacheable;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.client.integration.athena.model.hatch.HatchTextureData;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineEntity;
import es.degrassi.mmreborn.common.entity.base.IServerTickEntity;
import es.degrassi.mmreborn.common.entity.base.ITickEntity;
import es.degrassi.mmreborn.common.entity.base.ItemDroppeable;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.TextureableMachineEntity;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.MachineHatchType;
import es.degrassi.mmreborn.common.manager.handler.AbstractHandler;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineColorPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineTexturePacket;
import es.degrassi.mmreborn.common.util.IEntitySynchronizable;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.data.MMRConfig;
import es.degrassi.mmreborn.energistics.common.registration.EntityRegistration;
import es.degrassi.mmreborn.energistics.common.registration.MachineHatchTypeRegistration;
import es.degrassi.mmreborn.energistics.common.util.ITickSubscription;
import es.degrassi.mmreborn.energistics.common.util.TickableSubscription;
import es.degrassi.mmreborn.energistics.common.util.reflect.AEReflect;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@Setter
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MEEntity<T extends MachineComponent<X>, X> extends AENetworkedBlockEntity implements
    ITickSubscription, MachineComponentEntity<T>, IUpgradeableObject, ICraftingRequester, IPriorityHost,
    InterfaceLogicHost, ColorableMachineEntity, IEntitySynchronizable, ControllerAccessible, ITickEntity,
    TextureableMachineEntity, IServerTickEntity, ISyncableStuff, ItemDroppeable, IWrenchable, ControllerAttacheable {
  @Getter
  protected static final ResourceLocation defaultBaseTexture = ModularMachineryReborn.rl("block/casing_plain");
  private static final IGridNodeListener<MEEntity<?, ?>> NODE_LISTENER = new BlockEntityNodeListener<>() {
    @Override
    public void onGridChanged(MEEntity<?, ?> nodeOwner, IGridNode node) {
      nodeOwner.gridChanged();
    }
  };
  private static final String REQUEST_UPDATE_KEY = "requestModelUpdate";
  private static final String IN_STRUCTURE_KEY = "inStructure";
  private static final String CONTROLLER_POS_KEY = "controllerPos";
  private static final String CASING_COLOR_KEY = "casingColor";
  protected final GenericStack[] plannedWork;
  protected final MEHatchSize size;
  protected final IActionSource actionSource;
  protected final Set<BlockPos> controllerPosSet = new HashSet<>();
  protected final List<TickableSubscription> waitingToAdd = new LinkedList<>();
  protected final List<TickableSubscription> serverTicks = new LinkedList<>();
  protected final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> neighbourStorages = Maps.newEnumMap(Direction.class);
  protected final IActionSource requestActionSource;
  protected final MultiCraftingTracker craftingTracker;
  private final long offset;
  @Nullable
  private final InterfaceLogic interfaceLogic;
  @Nullable
  private final IUpgradeInventory upgrades;
  @Nullable
  private final ConfigInventory config;
  @Nullable
  private final ConfigInventory storage;
  @Nullable
  private final IConfigManager cm;
  @Nullable
  private final X inventory;
  @Nullable
  protected TickableSubscription autoIOSubs;
  @Setter
  protected boolean isOnline;
  protected ResourceLocation baseTexture;
  protected ResourceLocation overlayTexture;
  protected ResourceLocation defaultOverlayTexture;
  protected int priority;
  protected @Nullable MEStorage networkStorage;
  protected boolean hasConfig;
  @Nullable
  private BlockPos controllerPos;
  private boolean requestModelUpdate = false;
  private boolean inStructure = false;
  private int definedColor = Config.machineColor;

  public MEEntity(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(getEntityType(size), pos, blockState);
    this.size = size;
    this.craftingTracker = new MultiCraftingTracker(this, size.getSlots());
    this.requestActionSource = new RequestActionSource(this, getMainNode()::getNode);
    this.plannedWork = new GenericStack[size.getSlots()];
    this.actionSource = IActionSource.ofMachine(this);
    this.offset = RandomSource.createThreadSafe().nextInt(20);
    this.defaultOverlayTexture = createOverlayTexture(size);
    this.overlayTexture = defaultOverlayTexture;
    this.baseTexture = defaultBaseTexture;
    this.upgrades = createUpgrades();
    this.config = createConfig();
    this.storage = createStorage();
    this.cm = createConfigManager();
    this.interfaceLogic = createInterfaceLogic();
    getMainNode().setIdlePowerUsage(size.getIdlePowerDrainOnConnected());
    if (this.storage != null) {
      storage.useRegisteredCapacities();
    }
    if (this.config != null) {
      config.useRegisteredCapacities();
    }
    registerCapacities();
    this.inventory = createInventory();
    onGridConnectableSidesChanged();
  }

  @Contract(pure = true)
  protected static int[] generateSlots(int slots) {
    int[] slot = new int[slots];

    for (int i = 0; i < slots; i++) slot[i] = i;
    return slot;
  }

  @SuppressWarnings("unchecked")
  private static <T extends MEEntity<C, X>, C extends MachineComponent<X>, X> BlockEntityType<T> getEntityType(MEHatchSize size) {
    return (BlockEntityType<T>) (switch (size) {
      case ME_INPUT_BUS -> EntityRegistration.ME_INPUT_BUS;
      case ME_ADVANCED_INPUT_BUS -> EntityRegistration.ME_ADVANCED_INPUT_BUS;
      case ME_OUTPUT_BUS -> EntityRegistration.ME_OUTPUT_BUS;
      case ME_ADVANCED_OUTPUT_BUS -> EntityRegistration.ME_ADVANCED_OUTPUT_BUS;
      case ME_INPUT_HATCH -> EntityRegistration.ME_INPUT_HATCH;
      case ME_ADVANCED_INPUT_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_HATCH;
      case ME_OUTPUT_HATCH -> EntityRegistration.ME_OUTPUT_HATCH;
      case ME_ADVANCED_OUTPUT_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_HATCH;
      case ME_INPUT_CHEMICAL_HATCH -> EntityRegistration.ME_INPUT_CHEMICAL_HATCH;
      case ME_ADVANCED_INPUT_CHEMICAL_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH;
      case ME_OUTPUT_CHEMICAL_HATCH -> EntityRegistration.ME_OUTPUT_CHEMICAL_HATCH;
      case ME_ADVANCED_OUTPUT_CHEMICAL_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;
      case ME_INPUT_SOURCE_HATCH -> EntityRegistration.ME_INPUT_SOURCE_HATCH;
      case ME_ADVANCED_INPUT_SOURCE_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH;
      case ME_OUTPUT_SOURCE_HATCH -> EntityRegistration.ME_OUTPUT_SOURCE_HATCH;
      case ME_ADVANCED_OUTPUT_SOURCE_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH;
      case ME_INPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_INPUT_EXPERIENCE_HATCH;
      case ME_ADVANCED_INPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH;
      case ME_OUTPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_OUTPUT_EXPERIENCE_HATCH;
      case ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH -> EntityRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH;
      case ME_PATTERN_BUS -> EntityRegistration.ME_PATTERN_BUS;
      case ME_ADVANCED_PATTERN_BUS -> EntityRegistration.ME_ADVANCED_PATTERN_BUS;
    }).get();
  }

  protected void craftingRequester() {
    getMainNode().addService(ICraftingRequester.class, this);
  }

  protected void gridTickable(Ticker ticker) {
    getMainNode().addService(IGridTickable.class, ticker);
  }

  protected abstract void registerCapacities();

  @SuppressWarnings("unchecked")
  protected @Nullable InterfaceLogic createInterfaceLogic() {
    return new InterfaceLogic(getMainNode(), this, ((MEBlock<T, X>) getBlockState().getBlock()).item());
  }

  protected abstract @Nullable IUpgradeInventory createUpgrades();

  protected abstract @Nullable ConfigInventory createStorage();

  protected abstract @Nullable ConfigInventory createConfig();

  protected abstract @Nullable IConfigManager createConfigManager();

  @Override
  public @Nullable IConfigManager getConfigManager() {
    return cm;
  }

  protected ResourceLocation createOverlayTexture(MEHatchSize size) {
    return ModularMachineryRebornEnergistics.rl("block/overlay_" + size.getSerializedName() + "_" + (!getBlockState().getValue(MEBlock.ACTIVE) ? "in" : "") + "active");
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Item getItemFromBlockEntity() {
    return ((MEBlock<T, X>) getBlockState().getBlock()).item();
  }

  @Override
  protected final IManagedGridNode createMainNode() {
    return GridHelper.createManagedNode(this, getNodeListener())
        .setFlags(GridFlags.REQUIRE_CHANNEL)
        .setExposedOnSides(Set.of(getBlockState().getValue(BlockStateProperties.FACING)));
  }

  protected IGridNodeListener<MEEntity<?, ?>> getNodeListener() {
    return NODE_LISTENER;
  }

  @Override
  public ModelData getModelData() {
    return getModelDataBuilder("orientable").build();
  }

  @Override
  public HatchTextureData getTextureData(String mode) {
    return MachineComponentEntity.super.getTextureData(mode).derive(
        "bg_all",
        baseTexture,
        defaultBaseTexture,
        "ov_front",
        overlayTexture,
        defaultOverlayTexture,
        false
    );
  }

  @Override
  public Result onWrenched(es.degrassi.mmreborn.api.capability.config.RelativeSide side, Player player) {
    return Result.NONE;
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
  }

  @Override
  public ResourceLocation getMachineBaseTexture() {
    return baseTexture;
  }

  @Override
  public void setMachineBaseTexture(ResourceLocation newTexture) {
    setChanged();
    this.baseTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
          new SUpdateMachineTexturePacket(baseTexture, true, getBlockPos()));
    }
  }

  @Override
  public ResourceLocation getMachineOverlayTexture() {
    return overlayTexture;
  }

  @Override
  public void setMachineOverlayTexture(ResourceLocation newTexture) {
    setChanged();
    this.overlayTexture = newTexture;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
          new SUpdateMachineTexturePacket(overlayTexture, false, getBlockPos()));
    }
  }

  @Override
  public void resetTextures() {
    setMachineBaseTexture(defaultBaseTexture);
    setMachineOverlayTexture(defaultOverlayTexture);
  }

  @Override
  public int getMachineColor() {
    return definedColor;
  }

  @Override
  public void setMachineColor(int newColor) {
    setChanged();
    this.definedColor = newColor;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
          new SUpdateMachineColorPacket(newColor, getBlockPos()));
    }
  }

  @Override
  public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
    return Set.of(getBlockState().getValue(BlockStateProperties.FACING));
  }

  @Override
  public void onOrientationChanged(BlockOrientation orientation) {
    super.onOrientationChanged(orientation);
  }

  @Nullable
  public AEColor getGridColor() {
    if (getMainNode().getNode() == null) return null;
    return getMainNode().getNode().getGridColor();
  }

  public void setGridColor(AEColor gridColor) {
    getMainNode().setGridColor(gridColor);
  }

  public boolean hasColor() {
    if (getMainNode().getNode() == null) return false;
    return getGridColor() != null && getGridColor() != AEColor.TRANSPARENT;
  }

  @Override
  public MachineHatchType getHatchType() {
    return (switch (size) {
      case ME_INPUT_BUS -> MachineHatchTypeRegistration.ME_INPUT_BUS;
      case ME_ADVANCED_INPUT_BUS -> MachineHatchTypeRegistration.ME_ADVANCED_INPUT_BUS;
      case ME_OUTPUT_BUS -> MachineHatchTypeRegistration.ME_OUTPUT_BUS;
      case ME_ADVANCED_OUTPUT_BUS -> MachineHatchTypeRegistration.ME_ADVANCED_OUTPUT_BUS;
      case ME_INPUT_HATCH -> MachineHatchTypeRegistration.ME_INPUT_HATCH;
      case ME_ADVANCED_INPUT_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_INPUT_HATCH;
      case ME_OUTPUT_HATCH -> MachineHatchTypeRegistration.ME_OUTPUT_HATCH;
      case ME_ADVANCED_OUTPUT_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_OUTPUT_HATCH;
      case ME_PATTERN_BUS -> MachineHatchTypeRegistration.ME_PATTERN_BUS;
      case ME_ADVANCED_PATTERN_BUS -> MachineHatchTypeRegistration.ME_ADVANCED_PATTERN_BUS;

      // Only if AppEx is loaded
      case ME_INPUT_EXPERIENCE_HATCH -> MachineHatchTypeRegistration.ME_INPUT_EXPERIENCE_HATCH;
      case ME_ADVANCED_INPUT_EXPERIENCE_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_INPUT_EXPERIENCE_HATCH;
      case ME_OUTPUT_EXPERIENCE_HATCH -> MachineHatchTypeRegistration.ME_OUTPUT_EXPERIENCE_HATCH;
      case ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_OUTPUT_EXPERIENCE_HATCH;

      // Only if MMR Ars and ArsEng is loaded
      case ME_INPUT_SOURCE_HATCH -> MachineHatchTypeRegistration.ME_INPUT_SOURCE_HATCH;
      case ME_ADVANCED_INPUT_SOURCE_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_INPUT_SOURCE_HATCH;
      case ME_OUTPUT_SOURCE_HATCH -> MachineHatchTypeRegistration.ME_OUTPUT_SOURCE_HATCH;
      case ME_ADVANCED_OUTPUT_SOURCE_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_OUTPUT_SOURCE_HATCH;

      // Only if MMR Mek and ArsMek is loaded
      case ME_INPUT_CHEMICAL_HATCH -> MachineHatchTypeRegistration.ME_INPUT_CHEMICAL_HATCH;
      case ME_ADVANCED_INPUT_CHEMICAL_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_INPUT_CHEMICAL_HATCH;
      case ME_OUTPUT_CHEMICAL_HATCH -> MachineHatchTypeRegistration.ME_OUTPUT_CHEMICAL_HATCH;
      case ME_ADVANCED_OUTPUT_CHEMICAL_HATCH -> MachineHatchTypeRegistration.ME_ADVANCED_OUTPUT_CHEMICAL_HATCH;
    }).get();
  }

  public void doRestrictedTick() {
    IServerTickEntity.super.doRestrictedTick();
    this.executeTick();
    if (this.getBlockState().getValue(MEBlock.ACTIVE) != isOnline) {
      this.setBlockState(this.getBlockState().setValue(MEBlock.ACTIVE, isOnline));
      this.updateMEStatus();
      this.updateSubscription();
      this.markForUpdate();
    }
  }

  @Override
  public void setRemoved() {
    this.remove = true;
    this.invalidateCapabilities();
    this.requestModelDataUpdate();
    serverTicks.forEach(TickableSubscription::unsubscribe);
    serverTicks.clear();
    this.getMainNode().destroy();
  }

  @Override
  public void onChunkUnloaded() {
    serverTicks.forEach(TickableSubscription::unsubscribe);
    serverTicks.clear();
    this.getMainNode().destroy();
  }

  @Override
  public void setPriority(int priority) {
    this.priority = priority;
    saveChanges();
  }

  protected abstract X createInventory();

  @Override
  public void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    nbt.putBoolean(REQUEST_UPDATE_KEY, requestModelUpdate);
    nbt.putBoolean(IN_STRUCTURE_KEY, inStructure);
    nbt.putInt(CASING_COLOR_KEY, this.definedColor);
    if (controllerPos != null)
      nbt.putLong(CONTROLLER_POS_KEY, controllerPos.asLong());
    saveBaseTexture(nbt, pRegistries);
    saveOverlayTexture(nbt, pRegistries);
    if (storage != null)
      this.storage.writeToChildTag(nbt, "storage", pRegistries);
    if (upgrades != null)
      this.upgrades.writeToNBT(nbt, "upgrades", pRegistries);
    if (cm != null)
      this.cm.writeToNBT(nbt, pRegistries);
    if (config != null)
      this.config.writeToChildTag(nbt, "configStorage", pRegistries);
    if (interfaceLogic != null) {
      var tag = new CompoundTag();
      this.interfaceLogic.writeToNBT(tag, pRegistries);
      nbt.put("interfaceLogic", tag);
    }
    this.craftingTracker.writeToNBT(nbt);
    nbt.putInt("priority", this.priority);
  }

  @Override
  public void loadTag(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadTag(nbt, pRegistries);
    requestModelUpdate = nbt.contains(REQUEST_UPDATE_KEY) && nbt.getBoolean(REQUEST_UPDATE_KEY);
    inStructure = nbt.contains(IN_STRUCTURE_KEY) && nbt.getBoolean(IN_STRUCTURE_KEY);
    onGridConnectableSidesChanged();
    if (nbt.contains(CASING_COLOR_KEY)) {
      definedColor = nbt.getInt(CASING_COLOR_KEY);
    } else {
      definedColor = Config.machineColor;
    }
    if (nbt.contains(CONTROLLER_POS_KEY)) {
      controllerPos = BlockPos.of(nbt.getLong(CONTROLLER_POS_KEY));
    }
    loadBaseTexture(nbt, pRegistries);
    loadOverlayTexture(nbt, pRegistries);
    if (storage != null)
      this.storage.readFromChildTag(nbt, "storage", pRegistries);
    if (nbt.contains("upgrades") && upgrades != null)
      this.upgrades.readFromNBT(nbt, "upgrades", pRegistries);
    if (cm != null)
      this.cm.readFromNBT(nbt, pRegistries);
    if (config != null)
      this.config.readFromChildTag(nbt, "configStorage", pRegistries);
    if (this.interfaceLogic != null) {
      this.interfaceLogic.readFromNBT(nbt.getCompound("interfaceLogic"), pRegistries);
    }
    this.craftingTracker.readFromNBT(nbt);
    this.priority = nbt.getInt("priority");
    this.readConfig();
  }

  protected void readConfig() {
    this.hasConfig = this.config != null && !this.config.isEmpty();
    this.updatePlan();
    this.notifyNeighbors();
  }

  protected abstract void saveBaseTexture(CompoundTag compound, HolderLookup.Provider registries);

  protected abstract void saveOverlayTexture(CompoundTag compound, HolderLookup.Provider registries);

  protected abstract void loadBaseTexture(CompoundTag compound, HolderLookup.Provider registries);

  protected abstract void loadOverlayTexture(CompoundTag compound, HolderLookup.Provider registries);

  @Override
  public boolean triggerEvent(int id, int type) {
    if (id == 1) {
      if (getLevel() != null && getLevel().isClientSide())
        scheduleRenderUpdate();
      return true;
    }
    return false;
  }

  public void scheduleRenderUpdate() {
    if (getLevel() != null) {
      if (getLevel().isClientSide()) {
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1 << 3);
      } else {
        getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
      }
    }
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

  public boolean updateMEStatus() {
    var proxy = getMainNode();
    if (proxy.hasGridBooted() && getInterfaceLogic() != null) {
      this.getInterfaceLogic().notifyNeighbors();
    }
    setOnline(proxy.isActive());
    if (proxy.isActive()) {
      setDefaultOverlayTexture(createOverlayTexture(size));
      Optional.ofNullable(getController()).ifPresentOrElse(controller -> {
        Optional.ofNullable(controller.getFoundMachine().getFormedTextures().get(getHatchType())).ifPresentOrElse(pair -> {
          pair.getSecond().getFirst().ifPresent(this::setBaseTexture);
          pair.getSecond().getSecond().ifPresent(this::setOverlayTexture);
        }, this::resetTextures);
      }, this::resetTextures);
    }
    gridChanged();
    return isOnline();
  }

  public void gridChanged() {
    getMainNode().ifPresent(grid -> this.networkStorage = grid.getStorageService().getInventory());
    this.notifyNeighbors();
  }

  public void notifyNeighbors() {
    if (getMainNode().isActive()) {
      this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    this.invalidateCapabilities();
  }

  public boolean shouldSyncME() {
    return getOffsetTimer() % getMEUpdateInterval() == 0;
  }

  @Override
  public final void onMainNodeStateChanged(IGridNodeListener.State reason) {
    updateMEStatus();
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

  protected void syncME() {
    onStorageChanged();
  }

  protected final boolean shouldSubscribe() {
    return isOnline();
  }

  @Nullable
  public final TickableSubscription subscribeServerTick(Runnable runnable) {
    if (getLevel() != null && !getLevel().isClientSide()) {
      TickableSubscription sub = new TickableSubscription(runnable);
      waitingToAdd.add(sub);
      return sub;
    }
    return null;
  }

  public final void unsubscribe(@Nullable TickableSubscription current) {
    if (current != null) {
      current.unsubscribe();
    }
  }

  public final long getOffsetTimer() {
    return level == null ? getOffset() : (level.getGameTime() + getOffset());
  }

  public final long getMEUpdateInterval() {
    return size.isAdvanced() ? MMRConfig.get().ME_UPDATE_INTERVAL_ADVANCED.get() : MMRConfig.get().ME_UPDATE_INTERVAL.get();
  }

  @Override
  public MEEntity<T, X> getBlockEntity() {
    return this;
  }

  public abstract void openMenu(Player player, MenuHostLocator locator);

  public abstract void returnToMainMenu(Player player, ISubMenu subMenu);

  @Override
  @SuppressWarnings("unchecked")
  public final ItemStack getMainMenuIcon() {
    return ((MEBlock<T, X>) getBlockState().getBlock()).item().getDefaultInstance();
  }

  @Override
  public void saveChanges() {
    super.saveChanges();
    markForUpdate();
  }

  public void markForUpdate() {
    if (this.isRequestModelUpdate()) {
      this.requestModelDataUpdate();
    }

    if (getLevel() == null) return;
    this.setRequestModelUpdate(false);
    this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState());
    this.setChanged();
  }

  public void addDrops(List<ItemStack> drops) {
    if (storage != null) {
      storage.getAvailableStacks().iterator().forEachRemaining(entry -> {
        if (AEItemKey.is(entry.getKey())) {
          entry.getKey().addDrops(Integer.MAX_VALUE, drops, getLevel(), getBlockPos());
        }
      });
    }
    if (upgrades != null) {
      IItemHandler handler = upgrades.toItemHandler();
      for (int i = 0; i < handler.getSlots(); i++) {
        ItemStack stack = handler.getStackInSlot(i);
        if (stack.isEmpty()) continue;
        drops.add(stack);
      }
    }
  }

  public int getRedstoneSignal() {
    if (storage == null) return 0;
    return storage.createMenuWrapper().getRedstoneSignal();
  }

  protected boolean hasWorkToDo() {
    for (GenericStack requiredWork : this.plannedWork) {
      if (requiredWork != null) {
        return true;
      }
    }

    return false;
  }

  protected boolean updateStorage() {
    boolean didSomething = false;

    for (int x = 0; x < this.plannedWork.length; ++x) {
      GenericStack work = this.plannedWork[x];
      if (work != null) {
        int amount = (int) work.amount();
        didSomething = this.usePlan(x, work.what(), amount) || didSomething;
        if (didSomething) {
          this.onStorageChanged();
        }
      }
    }

    return didSomething;
  }

  protected boolean tryUsePlan(int slot, AEKey what, int amount) {
    IGrid grid = this.getMainNode().getGrid();
    if (grid == null) {
      return false;
    } else {
      MEStorage networkInv = grid.getStorageService().getInventory();
      IEnergyService energySrc = grid.getEnergyService();
      if (amount < 0) {
        amount = -amount;
        GenericStack inSlot = this.storage.getStack(slot);
        if (what.matches(inSlot) && inSlot.amount() >= (long) amount) {
          int inserted = (int) StorageHelper.poweredInsert(energySrc, networkInv, what, amount, this.requestActionSource);
          if (inserted > 0) {
            this.storage.extract(slot, what, inserted, Actionable.MODULATE);
          }

          return inserted > 0;
        } else {
          return true;
        }
      } else if (AEReflect.isBusy(craftingTracker, slot)) {
        return this.handleCrafting(slot, what, amount);
      } else if (amount == 0) {
        return false;
      } else if (this.storage.insert(slot, what, amount, Actionable.SIMULATE) != (long) amount) {
        return true;
      } else if (this.acquireFromNetwork(energySrc, networkInv, slot, what, amount)) {
        return true;
      } else {
        if (this.storage.getStack(slot) == null && this.upgrades.isInstalled(AEItems.FUZZY_CARD) && getConfigManager() != null) {
          FuzzyMode fuzzyMode = this.getConfigManager().getSetting(Settings.FUZZY_MODE);

          for (Object2LongMap.Entry<AEKey> aeKeyEntry : grid.getStorageService().getCachedInventory().findFuzzy(what, fuzzyMode)) {
            long maxAmount = this.storage.insert(slot, aeKeyEntry.getKey(), amount, Actionable.SIMULATE);
            if (this.acquireFromNetwork(energySrc, networkInv, slot, aeKeyEntry.getKey(), maxAmount)) {
              return true;
            }
          }
        }

        return this.handleCrafting(slot, what, amount);
      }
    }
  }

  protected boolean handleCrafting(int x, AEKey key, long amount) {
    IGrid grid = this.getMainNode().getGrid();
    return grid != null && this.upgrades.isInstalled(AEItems.CRAFTING_CARD) && this.craftingTracker.handleCrafting(x, key, amount, this.getLevel(), grid.getCraftingService(), this.actionSource);
  }

  protected boolean usePlan(int x, AEKey what, int amount) {
    boolean changed = this.tryUsePlan(x, what, amount);
    if (changed) {
      this.updatePlan(x);
    }

    return changed;
  }

  public void updatePlan() {
    boolean hadWork = this.hasWorkToDo();

    for (int x = 0; x < this.storage.size(); ++x) {
      this.updatePlan(x);
    }

    boolean hasWork = this.hasWorkToDo();
    if (hadWork != hasWork) {
      getMainNode().ifPresent((grid, node) -> {
        if (hasWork) {
          grid.getTickManager().alertDevice(node);
        } else {
          grid.getTickManager().sleepDevice(node);
        }

      });
    }
  }

  protected void cancelCrafting() {
    AEReflect.cancel(craftingTracker);
  }

  private void updatePlan(int slot) {
    if (config != null) {
      GenericStack req = this.config.getStack(slot);
      GenericStack stored = this.storage.getStack(slot);
      if (req == null && stored != null) {
        this.plannedWork[slot] = new GenericStack(stored.what(), -stored.amount());
      } else if (req != null) {
        if (stored == null) {
          this.plannedWork[slot] = req;
        } else if (this.storedRequestEquals(req.what(), stored.what())) {
          if (req.amount() != stored.amount()) {
            this.plannedWork[slot] = new GenericStack(req.what(), req.amount() - stored.amount());
          } else {
            this.plannedWork[slot] = null;
          }
        } else {
          this.plannedWork[slot] = new GenericStack(stored.what(), -stored.amount());
        }
      } else {
        this.plannedWork[slot] = null;
      }
    } else {
      GenericStack stored = this.storage.getStack(slot);
      if (stored != null) {
        this.plannedWork[slot] = new GenericStack(stored.what(), -stored.amount());
      } else {
        this.plannedWork[slot] = null;
      }
    }
  }

  private boolean storedRequestEquals(AEKey request, AEKey stored) {
    return this.upgrades.isInstalled(AEItems.FUZZY_CARD) && request.supportsFuzzyRangeSearch() ?
        request.fuzzyEquals(stored, this.cm.getSetting(Settings.FUZZY_MODE)) : request.equals(stored);
  }

  @Override
  public ImmutableSet<ICraftingLink> getRequestedJobs() {
    return this.craftingTracker.getRequestedJobs();
  }

  @Override
  public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
    int slot = AEReflect.getSlot(this.craftingTracker, link);
    return this.storage.insert(slot, what, amount, mode);
  }

  @Override
  public void jobStateChange(ICraftingLink link) {
    this.craftingTracker.jobStateChange(link);
  }

  protected void onStorageChanged() {
    this.saveChanges();
    this.updatePlan();
    if (inventory != null && inventory instanceof AbstractHandler<?, ?> handler) {
      handler.setChanged();
    }
  }

  protected boolean acquireFromNetwork(IEnergyService energySrc, MEStorage networkInv, int slot, AEKey what, long amount) {
    if (storage == null) return false;
    long acquired = StorageHelper.poweredExtraction(energySrc, networkInv, what, amount, this.requestActionSource);
    if (acquired > 0L) {
      long inserted = this.storage.insert(slot, what, acquired, Actionable.MODULATE);
      if (inserted < acquired) {
        throw new IllegalStateException("bad attempt at managing inventory. Voided items: " + inserted);
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  protected class RequestActionSource extends MachineSource {
    private final RequestContext context;

    public RequestActionSource(final MEEntity<T, X> machine, IActionHost host) {
      super(host);
      this.context = machine.new RequestContext();
    }

    public <C> Optional<C> context(Class<C> key) {
      return key == RequestContext.class ? Optional.of(key.cast(this.context)) : super.context(key);
    }
  }

  protected class RequestContext {
    protected RequestContext() {
    }

    public int getPriority() {
      return MEEntity.this.getPriority();
    }
  }

  protected class Ticker implements IGridTickable {
    public Ticker() {
    }

    public TickingRequest getTickingRequest(IGridNode node) {
      return new TickingRequest(TickRates.Interface, !hasWorkToDo());
    }

    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
      if (!getMainNode().isActive()) {
        return TickRateModulation.SLEEP;
      } else {
        boolean couldDoWork = updateStorage();
        return hasWorkToDo() ? (couldDoWork ? TickRateModulation.URGENT : TickRateModulation.SLOWER) : TickRateModulation.SLEEP;
      }
    }
  }
}
