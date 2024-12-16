package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import gripe._90.arseng.me.key.SourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Set;

public class SourcePatternTarget implements PatternBusTarget {
  private final ISourceCap tank;
  private final BlockPos pos;

  public SourcePatternTarget(SourceStorage tank, BlockPos pos) {
    this.tank = tank;
    this.pos = pos;
  }

  public SourcePatternTarget(Level level, BlockPos pos) {
    this.tank = level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, pos, null);
    this.pos = pos;
  }

  @Override
  public String getType() {
    return "source";
  }

  @Override
  public long insert(AEKey what, long amount, Actionable type) {
    if (!(what instanceof SourceKey)) return 0;
    return tank.receiveSource((int) amount, type.isSimulate());
  }

  @Override
  public boolean containsPatternInput(Set<AEKey> patternInputs) {
    return patternInputs.stream()
        .filter(key -> key instanceof SourceKey)
        .map(SourceKey.class::cast)
        .anyMatch(sourceKey -> tank.getSource() > 0);
  }
}
