package es.degrassi.mmreborn.energistics.api.services.crafting;

import appeng.api.config.Actionable;
import appeng.api.config.LockCraftingMode;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.util.IConfigManager;
import appeng.core.AELog;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.core.settings.TickRates;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.UnlockCraftingEvent;
import appeng.me.helpers.MachineSource;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.PlayerInternalInventory;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.ChemicalPatternTarget;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.ExperiencePatternTarget;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.FluidPatternTarget;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.ItemPatternTarget;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.PatternBusTarget;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.SourcePatternTarget;
import es.degrassi.mmreborn.energistics.api.services.settings.MMRESettings;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import gripe._90.arseng.me.key.SourceKey;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
public class PatternBusLogic implements InternalInventoryHost, ICraftingProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(PatternBusLogic.class);

  public static final String NBT_MEMORY_CARD_PATTERNS = "patterns";
  public static final String NBT_UNLOCK_EVENT = "unlockEvent";
  public static final String NBT_UNLOCK_STACK = "unlockStack";
  public static final String NBT_PRIORITY = "priority";
  public static final String NBT_SEND_LIST = "sendList";
  public static final String NBT_ADAPTERS = "adapters";

  private final PatternBusLogicHost host;
  private final IManagedGridNode mainNode;
  private final IActionSource actionSource;
  private final IConfigManager configManager;

  private int priority;

  // Pattern storing logic
  private final AppEngInternalInventory patternInventory;
  private final List<IPatternDetails> patterns = new ArrayList<>();
  private final List<PatternBusTarget> adapters = new ArrayList<>();
  /**
   * Keeps track of the inputs of all the patterns. When blocking mode is enabled, if any of these is contained in the
   * target, the pattern won't be pushed. Always contains keys with the secondary component dropped.
   */
  private final Set<AEKey> patternInputs = new HashSet<>();
  // Pattern sending logic
  private final List<GenericStack> sendList = new ArrayList<>();

  private final PatternBusTargetCache[] targetCaches = new PatternBusTargetCache[6];

  private YesNo redstoneState = YesNo.UNDECIDED;

  @Nullable
  private UnlockCraftingEvent unlockEvent;
  @Nullable
  private GenericStack unlockStack;
  private int roundRobinIndex = 0;
  public PatternBusLogic(IManagedGridNode mainNode, PatternBusLogicHost host) {
    this(mainNode, host, 9);
  }

  public PatternBusLogic(IManagedGridNode mainNode, PatternBusLogicHost host, int patternInventorySize) {
    this.patternInventory = new AppEngInternalInventory(this, patternInventorySize);
    this.host = host;
    this.mainNode = mainNode
        .setFlags(GridFlags.REQUIRE_CHANNEL)
        .addService(IGridTickable.class, new PatternBusLogic.Ticker())
        .addService(ICraftingProvider.class, this);
    this.actionSource = new MachineSource(mainNode::getNode);

    configManager = IConfigManager.builder(this::configChanged)
        .registerSetting(MMRESettings.BLOCKING_MODE, YesNo.NO)
        .registerSetting(Settings.PATTERN_ACCESS_TERMINAL, YesNo.YES)
        .registerSetting(MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.NONE)
        .build();
  }

  public void setPriority(int priority) {
    this.priority = priority;
    this.host.saveChanges();

    ICraftingProvider.requestUpdate(mainNode);
  }

  public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
    this.configManager.writeToNBT(tag, registries);
    this.patternInventory.writeToNBT(tag, NBT_MEMORY_CARD_PATTERNS, registries);
    tag.putInt(NBT_PRIORITY, this.priority);
    if (unlockEvent == UnlockCraftingEvent.REDSTONE_POWER) {
      tag.putByte(NBT_UNLOCK_EVENT, (byte) 1);
    } else if (unlockEvent == UnlockCraftingEvent.RESULT) {
      if (unlockStack != null) {
        tag.putByte(NBT_UNLOCK_EVENT, (byte) 2);
        tag.put(NBT_UNLOCK_STACK, GenericStack.writeTag(registries, unlockStack));
      } else {
        LOGGER.error("Saving pattern provider {}, locked waiting for stack, but stack is null!", host);
      }
    } else if (unlockEvent == UnlockCraftingEvent.REDSTONE_PULSE) {
      tag.putByte(NBT_UNLOCK_EVENT, (byte) 3);
    }

    ListTag sendListTag = new ListTag();
    for (var toSend : sendList) {
      sendListTag.add(GenericStack.writeTag(registries, toSend));
    }
    tag.put(NBT_SEND_LIST, sendListTag);
    ListTag adaptersTag = new ListTag();
    for (var adapter : adapters) {
      adaptersTag.add(adapter.writeNBT(registries));
    }
    tag.put(NBT_ADAPTERS, adaptersTag);
  }

  public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
    this.configManager.readFromNBT(tag, registries);
    this.patternInventory.readFromNBT(tag, NBT_MEMORY_CARD_PATTERNS, registries);
    this.priority = tag.getInt(NBT_PRIORITY);

    var unlockEventType = tag.getByte(NBT_UNLOCK_EVENT);
    this.unlockEvent = switch (unlockEventType) {
      case 0 -> null;
      case 1 -> UnlockCraftingEvent.REDSTONE_POWER;
      case 2 -> UnlockCraftingEvent.RESULT;
      case 3 -> UnlockCraftingEvent.REDSTONE_PULSE;
      default -> {
        LOGGER.error("Unknown unlock event type {} in NBT for pattern provider: {}", unlockEventType, tag);
        yield null;
      }
    };
    if (this.unlockEvent == UnlockCraftingEvent.RESULT) {
      this.unlockStack = GenericStack.readTag(registries, tag.getCompound(NBT_UNLOCK_STACK));
      if (this.unlockStack == null) {
        LOGGER.error("Could not load unlock stack for pattern provider from NBT: {}", tag);
      }
    } else {
      this.unlockStack = null;
    }

    var sendListTag = tag.getList("sendList", Tag.TAG_COMPOUND);
    for (int i = 0; i < sendListTag.size(); ++i) {
      var stack = GenericStack.readTag(registries, sendListTag.getCompound(i));
      if (stack != null) {
        this.addToSendList(stack.what(), stack.amount());
      }
    }
    var adaptersTag = tag.getList(NBT_ADAPTERS, Tag.TAG_COMPOUND);
    adapters.clear();
    for (int i = 0; i < adaptersTag.size(); ++i) {
      PatternBusTarget target = PatternBusTarget.readNBT(host.getLevel(), adaptersTag.getCompound(i));
      if (target != null)
        adapters.add(target);
    }
  }

  public void saveChanges() {
    this.host.saveChanges();
  }

  @Override
  public void saveChangedInventory(AppEngInternalInventory inv) {
    this.host.saveChanges();
  }

  @Override
  public void onChangeInventory(AppEngInternalInventory inv, int slot) {
    this.saveChanges();
    this.updatePatterns();
  }

  @Override
  public boolean isClientSide() {
    Level level = this.host.getBlockEntity().getLevel();
    return level == null || level.isClientSide();
  }

  public void updatePatterns() {
    patterns.clear();
    patternInputs.clear();

    for (var stack : this.patternInventory) {
      var details = PatternDetailsHelper.decodePattern(stack, this.host.getBlockEntity().getLevel());

      if (details != null) {
        patterns.add(details);

        for (var iinput : details.getInputs()) {
          for (var inputCandidate : iinput.getPossibleInputs()) {
            patternInputs.add(inputCandidate.what().dropSecondary());
          }
        }
      }
    }

    ICraftingProvider.requestUpdate(mainNode);
  }

  @Override
  public List<IPatternDetails> getAvailablePatterns() {
    return this.patterns;
  }

  @Override
  public int getPatternPriority() {
    return this.priority;
  }

  /**
   * Apply round-robin to list.
   */
  private <T> void rearrangeRoundRobin(List<T> list) {
    if (list.isEmpty()) {
      return;
    }

    roundRobinIndex %= list.size();
    for (int i = 0; i < roundRobinIndex; ++i) {
      list.add(list.get(i));
    }
    list.subList(0, roundRobinIndex).clear();
  }

  @Override
  public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
    if (!sendList.isEmpty() || !this.mainNode.isActive() || !this.patterns.contains(patternDetails)) {
      return false;
    }

    var be = host.getBlockEntity();

    if (getCraftingLockedReason() != LockCraftingMode.NONE || host.getController() == null) {
      return false;
    }

    var possibleTargets = new ArrayList<PatternBusTarget>();

    List<Object2LongMap.Entry<AEKey>> inputs = new LinkedList<>();
    Arrays.stream(inputHolder).map(KeyCounter::iterator).forEachOrdered(iterator -> iterator.forEachRemaining(inputs::add));

    components:
    for (var component : host.getFoundComponents().entrySet()) {
      var provider = component.getValue().getContainerProvider();
      if (provider == null) continue;
      if (!component.getValue().getIOType().isInput()) continue;
      if (
           provider instanceof Long ||
              provider instanceof Integer ||
               provider instanceof Boolean ||
               provider instanceof String ||
               provider instanceof Byte ||
               provider instanceof Double ||
               provider instanceof Float ||
               provider instanceof Enum<?> ||
               provider.getClass().isPrimitive()
      ) continue;

      for (var entry : inputs) {
        var key = entry.getKey();
        if (key instanceof AEItemKey itemKey && provider instanceof IOInventory inv) {
          int inserted = ItemUtils.tryPlaceItemInInventory(itemKey.toStack((int) entry.getLongValue()), inv, true);
          if (inserted >= entry.getLongValue()) {
            possibleTargets.add(new ItemPatternTarget(inv, component.getKey()));
            inputs.remove(entry);
            continue components;
          }
        } else if (key instanceof AEFluidKey fluidKey && provider instanceof HybridTank tank) {
          int inserted = tank.fill(fluidKey.toStack((int) entry.getLongValue()), IFluidHandler.FluidAction.SIMULATE);
          if (inserted >= entry.getLongValue()) {
            if (inserted >= entry.getLongValue()) {
              possibleTargets.add(new FluidPatternTarget(tank, component.getKey()));
              inputs.remove(entry);
              continue components;
            }
          }
        } else if (Mods.isMekPossible()) {
          if (key instanceof MekanismKey chemKey && provider instanceof BasicChemicalTank tank) {
            ChemicalStack inserted = tank.insertChemical(chemKey.withAmount(entry.getLongValue()), Action.SIMULATE);
            if (inserted.getAmount() <= 0) {
              possibleTargets.add(new ChemicalPatternTarget(tank, component.getKey()));
              inputs.remove(entry);
              continue components;
            }
          }
        } else if (Mods.isArsPossible()) {
          if (key instanceof SourceKey && provider instanceof SourceStorage tank) {
            int inserted = tank.receiveSource((int) entry.getLongValue(), true);
            if (inserted >= entry.getLongValue()) {
              possibleTargets.add(new SourcePatternTarget(tank, component.getKey()));
              inputs.remove(entry);
              continue components;
            }
          }
        } else if (Mods.isExperiencePossible()) {
          if (key instanceof ExperienceKey && provider instanceof BasicExperienceTank tank) {
            long inserted = tank.receiveExperience(entry.getLongValue(), true);
            if (inserted >= entry.getLongValue()) {
              possibleTargets.add(new ExperiencePatternTarget(tank, component.getKey()));
              inputs.remove(entry);
              continue components;
            }
          }
        }
      }
    }

    // If no dedicated crafting machine could be found, and the pattern does not support
    // generic external inventories, stop here.
    if (!patternDetails.supportsPushInputsToExternalInventory()) {
      return false;
    }

    // Rearrange for round-robin
    rearrangeRoundRobin(possibleTargets);

    // Push to other kinds of blocks
    for (var target : possibleTargets) {

      if (this.isBlocking() && target.containsPatternInput(this.patternInputs)) {
        continue;
      }

      if (this.adapterAcceptsAll(target, inputHolder)) {
        patternDetails.pushInputsToExternalInventory(inputHolder, (what, amount) -> {
          var inserted = target.insert(what, amount, Actionable.SIMULATE);
          if (inserted < amount) {
            this.addToSendList(what, amount - inserted);
          }
        });
        adapters.add(target);
      } else if (adapterAcceptsAnything(target, inputHolder)) {
        patternDetails.pushInputsToExternalInventory(inputHolder, (what, amount) -> {
          var inserted = target.insert(what, amount, Actionable.SIMULATE);
          if (inserted < amount) {
            this.addToSendList(what, amount - inserted);
          }
        });
        adapters.add(target);
      }
      if (sendList.size() == inputHolder.length) {
        onPushPatternSuccess(patternDetails);
        this.sendStacksOut();
        ++roundRobinIndex;
        return true;
      }
    }

    return false;
  }

  public void resetCraftingLock() {
    if (unlockEvent != null) {
      unlockEvent = null;
      unlockStack = null;
      saveChanges();
    }
  }

  private void onPushPatternSuccess(IPatternDetails pattern) {
    resetCraftingLock();

    var lockMode = configManager.getSetting(MMRESettings.LOCK_CRAFTING_MODE);
    switch (lockMode) {
      case LOCK_UNTIL_PULSE -> {
        if (getRedstoneState()) {
          // Already have signal, wait for no signal before switching to REDSTONE_POWER
          unlockEvent = UnlockCraftingEvent.REDSTONE_PULSE;
        } else {
          // No signal, wait for signal
          unlockEvent = UnlockCraftingEvent.REDSTONE_POWER;
        }
        redstoneState = YesNo.UNDECIDED; // Check redstone state again next update
        saveChanges();
      }
      case LOCK_UNTIL_RESULT -> {
        unlockEvent = UnlockCraftingEvent.RESULT;
        unlockStack = pattern.getPrimaryOutput();
        saveChanges();
      }
    }
  }

  /**
   * Gets if the crafting lock is in effect and why.
   *
   * @return null if the lock isn't in effect
   */
  public LockCraftingMode getCraftingLockedReason() {
    var lockMode = configManager.getSetting(MMRESettings.LOCK_CRAFTING_MODE);
    if (lockMode == LockCraftingMode.LOCK_WHILE_LOW && !getRedstoneState()) {
      // Crafting locked by redstone signal
      return LockCraftingMode.LOCK_WHILE_LOW;
    } else if (lockMode == LockCraftingMode.LOCK_WHILE_HIGH && getRedstoneState()) {
      return LockCraftingMode.LOCK_WHILE_HIGH;
    } else if (unlockEvent != null) {
      // Crafting locked by waiting for unlock event
      switch (unlockEvent) {
        case REDSTONE_POWER, REDSTONE_PULSE -> {
          return LockCraftingMode.LOCK_UNTIL_PULSE;
        }
        case RESULT -> {
          return LockCraftingMode.LOCK_UNTIL_RESULT;
        }
      }
    }
    return LockCraftingMode.NONE;
  }

  /**
   * @return Null if {@linkplain #getCraftingLockedReason()} is not {@link LockCraftingMode#LOCK_UNTIL_RESULT}.
   */
  @Nullable
  public GenericStack getUnlockStack() {
    return unlockStack;
  }

  private Set<Direction> getActiveSides() {
    var sides = host.getTargets();

    // Skip sides with grid connections to other pattern providers and to interfaces connected to the same network
    var node = mainNode.getNode();
    if (node != null) {
      for (var entry : node.getInWorldConnections().entrySet()) {
        var otherNode = entry.getValue().getOtherSide(node);
        if (otherNode.getOwner() instanceof PatternBusLogicHost
            || otherNode.getOwner() instanceof PatternProviderLogicHost
            || (otherNode.getOwner() instanceof InterfaceLogicHost
            && otherNode.getGrid().equals(mainNode.getGrid()))) {
          sides.remove(entry.getKey());
        }
      }
    }

    return sides;
  }

  public boolean isBlocking() {
    return this.configManager.getSetting(MMRESettings.BLOCKING_MODE) == YesNo.YES;
  }

  @Nullable
  private PatternBusTarget findAdapter(Direction side) {
    if (targetCaches[side.get3DDataValue()] == null) {
      var thisBe = host.getBlockEntity();
      targetCaches[side.get3DDataValue()] = new PatternBusTargetCache(
          (ServerLevel) thisBe.getLevel(),
          host.getControllerPos(),
          side.getOpposite(),
          actionSource);
    }

    return targetCaches[side.get3DDataValue()].find();
  }

  private boolean adapterAcceptsAll(PatternBusTarget target, KeyCounter[] inputHolder) {
    for (var inputList : inputHolder) {
      for (var input : inputList) {
        var inserted = target.insert(input.getKey(), input.getLongValue(), Actionable.SIMULATE);
        if (inserted == 0) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean adapterAcceptsAnything(PatternBusTarget target, KeyCounter[] inputHolder) {
    for (var inputList : inputHolder) {
      for (var input : inputList) {
        var inserted = target.insert(input.getKey(), input.getLongValue(), Actionable.SIMULATE);
        if (inserted > 0) {
          return true;
        }
      }
    }
    return false;
  }

  private void addToSendList(AEKey what, long amount) {
    if (amount > 0) {
      this.sendList.add(new GenericStack(what, amount));

      this.mainNode.ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
    }
  }

  private boolean sendStacksOut() {
    if (adapters.isEmpty()) {
      if (!sendList.isEmpty()) {
        throw new IllegalStateException("Invalid pattern provider state, this is a bug.");
      }
      return false;
    }

    for (var ad = adapters.listIterator(); ad.hasNext();) {
      var adapter = ad.next();
      if (adapter == null) {
        continue;
      }

      for (var it = sendList.listIterator(); it.hasNext();) {
        var stack = it.next();
        var what = stack.what();
        long amount = stack.amount();

        var inserted = adapter.insert(what, amount, Actionable.MODULATE);
        if (inserted >= amount) {
          it.remove();
          ad.remove();
        } else if (inserted > 0) {
          it.set(new GenericStack(what, amount - inserted));
        }
      }

      if (sendList.isEmpty() && adapters.isEmpty()) {
        return true;
      }
    }
    adapters.clear();
    return false;
  }

  @Override
  public boolean isBusy() {
    return !sendList.isEmpty() && !adapters.isEmpty();
  }

  private boolean hasWorkToDo() {
    return !sendList.isEmpty() && !adapters.isEmpty();
  }

  private boolean doWork() {
    // Note: bitwise OR to avoid short-circuiting.
    return sendStacksOut();
  }

  public InternalInventory getPatternInv() {
    return this.patternInventory;
  }

  public void onMainNodeStateChanged() {
    if (this.mainNode.isActive()) {
      this.mainNode.ifPresent((grid, node) -> {
        grid.getTickManager().alertDevice(node);
      });
    }
  }

  public void addDrops(List<ItemStack> drops) {
    for (var stack : this.patternInventory) {
      drops.add(stack);
    }

    for (var stack : this.sendList) {
      stack.what().addDrops(stack.amount(), drops, this.host.getBlockEntity().getLevel(),
          this.host.getBlockEntity().getBlockPos());
    }
  }

  public void clearContent() {
    this.patternInventory.clear();
    this.sendList.clear();
  }

  public void exportSettings(DataComponentMap.Builder builder) {
    builder.set(AEComponents.EXPORTED_PATTERNS, patternInventory.toItemContainerContents());
  }

  public void importSettings(DataComponentMap input, @Nullable Player player) {
    var patterns = input.getOrDefault(AEComponents.EXPORTED_PATTERNS, ItemContainerContents.EMPTY);

    if (player != null && !player.level().isClientSide) {
      clearPatternInventory(player);

      var desiredPatterns = new AppEngInternalInventory(patternInventory.size());
      desiredPatterns.fromItemContainerContents(patterns);

      // Restore from blank patterns in the player inv
      var playerInv = player.getInventory();
      var blankPatternsAvailable = player.getAbilities().instabuild ? Integer.MAX_VALUE
          : playerInv.countItem(AEItems.BLANK_PATTERN.asItem());
      var blankPatternsUsed = 0;
      for (int i = 0; i < desiredPatterns.size(); i++) {
        if (desiredPatterns.getStackInSlot(i).isEmpty()) {
          continue;
        }

        // Don't restore junk
        var pattern = PatternDetailsHelper.decodePattern(desiredPatterns.getStackInSlot(i),
            host.getBlockEntity().getLevel());
        if (pattern == null) {
          continue; // Skip junk / broken recipes
        }

        // Keep track of how many blank patterns we need
        ++blankPatternsUsed;
        if (blankPatternsAvailable >= blankPatternsUsed) {
          if (!patternInventory.addItems(pattern.getDefinition().toStack()).isEmpty()) {
            AELog.warn("Failed to add pattern to pattern provider");
            blankPatternsUsed--;
          }
        }
      }

      // Deduct the used blank patterns
      if (blankPatternsUsed > 0 && !player.getAbilities().instabuild) {
        new PlayerInternalInventory(playerInv)
            .removeItems(blankPatternsUsed, AEItems.BLANK_PATTERN.stack(), null);
      }

      // Warn about not being able to restore all patterns due to lack of blank patterns
      if (blankPatternsUsed > blankPatternsAvailable) {
        player.sendSystemMessage(
            PlayerMessages.MissingBlankPatterns.text(blankPatternsUsed - blankPatternsAvailable));
      }
    }
  }

  // Converts all patterns in this provider to blank patterns and give them to the player
  private void clearPatternInventory(Player player) {
    // Just clear it for creative mode players
    if (player.getAbilities().instabuild) {
      for (int i = 0; i < patternInventory.size(); i++) {
        patternInventory.setItemDirect(i, ItemStack.EMPTY);
      }
      return;
    }

    var playerInv = player.getInventory();

    // Clear out any existing patterns and give them to the player
    var blankPatternCount = 0;
    for (int i = 0; i < patternInventory.size(); i++) {
      var pattern = patternInventory.getStackInSlot(i);
      // Auto-Clear encoded patterns to allow them to stack
      if (pattern.is(AEItems.CRAFTING_PATTERN.asItem())
          || pattern.is(AEItems.PROCESSING_PATTERN.asItem())
          || pattern.is(AEItems.SMITHING_TABLE_PATTERN.asItem())
          || pattern.is(AEItems.STONECUTTING_PATTERN.asItem())
          || pattern.is(AEItems.BLANK_PATTERN.asItem())) {
        blankPatternCount += pattern.getCount();
      } else {
        // Give back any non-blank-patterns individually
        playerInv.placeItemBackInInventory(pattern);
      }
      patternInventory.setItemDirect(i, ItemStack.EMPTY);
    }

    // Place back the removed blank patterns all at once
    if (blankPatternCount > 0) {
      playerInv.placeItemBackInInventory(AEItems.BLANK_PATTERN.stack(blankPatternCount), false);
    }
  }

  private void onStackReturnedToNetwork(GenericStack genericStack) {
    if (unlockEvent != UnlockCraftingEvent.RESULT) {
      return; // If we're not waiting for the result, we don't care
    }

    if (unlockStack == null) {
      // Actually an error state...
      LOGGER.error("pattern provider was waiting for RESULT, but no result was set");
      unlockEvent = null;
    } else if (unlockStack.what().equals(genericStack.what())) {
      var remainingAmount = unlockStack.amount() - genericStack.amount();
      if (remainingAmount <= 0) {
        unlockEvent = null;
        unlockStack = null;
      } else {
        unlockStack = new GenericStack(unlockStack.what(), remainingAmount);
      }
    }
  }

  private class Ticker implements IGridTickable {

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
      return new TickingRequest(TickRates.Interface, !hasWorkToDo());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
      if (!mainNode.isActive()) {
        return TickRateModulation.SLEEP;
      }
      boolean couldDoWork = doWork();
      return hasWorkToDo() ? couldDoWork ? TickRateModulation.URGENT : TickRateModulation.SLOWER
          : TickRateModulation.SLEEP;
    }
  }

  /**
   * @return Gets the name used to show this pattern provider in the
   *         {@link appeng.menu.implementations.PatternAccessTermMenu}.
   */
  public PatternContainerGroup getTerminalGroup() {
    var host = this.host.getBlockEntity();
    var hostLevel = host.getLevel();

    // Prefer own custom name / icon if player has named it
    if (this.host instanceof Nameable nameable && nameable.hasCustomName()) {
      var name = nameable.getCustomName();
      return new PatternContainerGroup(
          this.host.getTerminalIcon(),
          name,
          List.of());
    }

    var sides = getActiveSides();
    var groups = new LinkedHashSet<PatternContainerGroup>(sides.size());
    for (var side : sides) {
      var sidePos = host.getBlockPos().relative(side);
      var group = PatternContainerGroup.fromMachine(Objects.requireNonNull(hostLevel), sidePos, side.getOpposite());
      if (group != null) {
        groups.add(group);
      }
    }

    // If there's just one group, group by that
    if (groups.size() == 1) {
      return groups.iterator().next();
    }

    List<Component> tooltip = List.of();
    // If there are multiple groups, show that in the tooltip
    if (groups.size() > 1) {
      tooltip = new ArrayList<>();
      tooltip.add(GuiText.AdjacentToDifferentMachines.text().withStyle(ChatFormatting.BOLD));
      for (var group : groups) {
        tooltip.add(group.name());
        for (var line : group.tooltip()) {
          tooltip.add(Component.literal("  ").append(line));
        }
      }
    }

    // If nothing is adjacent, just use itself
    var hostIcon = this.host.getTerminalIcon();
    return new PatternContainerGroup(
        hostIcon,
        hostIcon.getDisplayName(),
        tooltip);
  }

  public long getSortValue() {
    final BlockEntity te = this.host.getBlockEntity();
    return (long) te.getBlockPos().getZ() << 24 ^ (long) te.getBlockPos().getX() << 8 ^ te.getBlockPos().getY();
  }

  @Nullable
  public IGrid getGrid() {
    return mainNode.getGrid();
  }

  public void updateRedstoneState() {
    // If we're waiting for a pulse, update immediately
    if (unlockEvent == UnlockCraftingEvent.REDSTONE_POWER && getRedstoneState()) {
      unlockEvent = null; // Unlocked!
      saveChanges();
    } else if (unlockEvent == UnlockCraftingEvent.REDSTONE_PULSE && !getRedstoneState()) {
      unlockEvent = UnlockCraftingEvent.REDSTONE_POWER; // Wait for re-power
      redstoneState = YesNo.UNDECIDED; // Need to re-check signal on next update
      saveChanges();
    } else {
      // Otherwise, just reset back to undecided
      redstoneState = YesNo.UNDECIDED;
    }
  }

  private void configChanged(IConfigManager manager, Setting<?> setting) {
    if (setting == MMRESettings.LOCK_CRAFTING_MODE) {
      resetCraftingLock();
    } else {
      saveChanges();
    }
  }

  private boolean getRedstoneState() {
    if (redstoneState == YesNo.UNDECIDED) {
      var be = this.host.getBlockEntity();
      redstoneState = Objects.requireNonNull(be.getLevel()).hasNeighborSignal(be.getBlockPos())
          ? YesNo.YES
          : YesNo.NO;
    }
    return redstoneState == YesNo.YES;
  }
}
