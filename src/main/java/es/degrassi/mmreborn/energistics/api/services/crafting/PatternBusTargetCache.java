package es.degrassi.mmreborn.energistics.api.services.crafting;

import appeng.api.AECapabilities;
import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.MEStorage;
import appeng.me.storage.CompositeStorage;
import appeng.parts.automation.StackWorldBehaviors;
import es.degrassi.mmreborn.energistics.api.services.crafting.target.PatternBusTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class PatternBusTargetCache {
  private final BlockCapabilityCache<MEStorage, Direction> cache;
  private final IActionSource src;
  private final Map<AEKeyType, ExternalStorageStrategy> strategies;

  public PatternBusTargetCache(ServerLevel l, BlockPos pos, Direction direction, IActionSource src) {
    this.cache = BlockCapabilityCache.create(AECapabilities.ME_STORAGE, l, pos, direction);
    this.src = src;
    this.strategies = StackWorldBehaviors.createExternalStorageStrategies(l, pos, direction);
  }

  @Nullable
  public PatternBusTarget find() {
    // our capability first: allows any storage channel
    var meStorage = cache.getCapability();
    if (meStorage != null) {
      return wrapMeStorage(meStorage);
    }

    // otherwise fall back to the platform capability
    var externalStorages = new IdentityHashMap<AEKeyType, MEStorage>(2);
    for (var entry : strategies.entrySet()) {
      var wrapper = entry.getValue().createWrapper(false, () -> {
      });
      if (wrapper != null) {
        externalStorages.put(entry.getKey(), wrapper);
      }
    }

    if (!externalStorages.isEmpty()) {
      return wrapMeStorage(new CompositeStorage(externalStorages));
    }

    return null;
  }

  private PatternBusTarget wrapMeStorage(MEStorage storage) {
    return new PatternBusTarget() {
      @Override
      public String getType() {
        return "me_storage";
      }

      @Override
      public long insert(AEKey what, long amount, Actionable type) {
        return storage.insert(what, amount, type, src);
      }

      @Override
      public boolean containsPatternInput(Set<AEKey> patternInputs) {
        for (var stack : storage.getAvailableStacks()) {
          if (patternInputs.contains(stack.getKey().dropSecondary())) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
