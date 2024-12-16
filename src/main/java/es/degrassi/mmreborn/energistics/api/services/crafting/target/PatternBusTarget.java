package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.AECapabilities;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.MEStorage;
import appeng.me.storage.CompositeStorage;
import appeng.parts.automation.StackWorldBehaviors;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Set;

public interface PatternBusTarget {
  @Nullable
  static PatternBusTarget get(Level l, BlockPos pos, @Nullable BlockEntity be, Direction side,
                                   IActionSource src) {

    // our capability first: allows any storage channel
    MEStorage storage;
    if (be != null) {
      storage = l.getCapability(AECapabilities.ME_STORAGE, be.getBlockPos(), be.getBlockState(), be, side);
    } else {
      storage = l.getCapability(AECapabilities.ME_STORAGE, pos, side);
    }
    if (storage != null) {
      return wrapMeStorage(storage, src);
    }

    // otherwise fall back to the platform capability
    var strategies = StackWorldBehaviors.createExternalStorageStrategies((ServerLevel) l, pos, side);
    var externalStorages = new IdentityHashMap<AEKeyType, MEStorage>(2);
    for (var entry : strategies.entrySet()) {
      var wrapper = entry.getValue().createWrapper(false, Runnables.doNothing());
      if (wrapper != null) {
        externalStorages.put(entry.getKey(), wrapper);
      }
    }

    if (!externalStorages.isEmpty()) {
      return wrapMeStorage(new CompositeStorage(externalStorages), src);
    }

    return null;
  }

  private static PatternBusTarget wrapMeStorage(MEStorage storage, IActionSource src) {
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

      @Override
      public CompoundTag writeNBT(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", getType());
        return tag;
      }
    };
  }

  default BlockPos getPos() {
    return null;
  }

  String getType();

  default void setBlockPos(BlockPos pos) {}

  long insert(AEKey what, long amount, Actionable type);

  boolean containsPatternInput(Set<AEKey> patternInputs);

  default CompoundTag writeNBT(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    tag.putLong("pos", getPos().asLong());
    tag.putString("type", getType());
    return tag;
  }

  static PatternBusTarget readNBT(Level level, CompoundTag nbt) {
    String type = nbt.getString("type");
    BlockPos pos = BlockPos.of(nbt.getLong("pos"));
    return switch (type) {
      case "item" -> new ItemPatternTarget(level, pos);
      case "fluid" -> new FluidPatternTarget(level, pos);
      case "experience" -> new ExperiencePatternTarget(level, pos);
      case "chemical" -> new ChemicalPatternTarget(level, pos);
      case "source" -> new SourcePatternTarget(level, pos);
      default -> null;
    };
  }
}
