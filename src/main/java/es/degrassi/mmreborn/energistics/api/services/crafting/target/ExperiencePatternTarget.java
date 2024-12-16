package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Set;

@Getter
public class ExperiencePatternTarget implements PatternBusTarget {
  private final IExperienceHandler tank;
  private final BlockPos pos;

  public ExperiencePatternTarget(BasicExperienceTank tank, BlockPos pos) {
    this.tank = tank;
    this.pos = pos;
  }

  public ExperiencePatternTarget(Level level, BlockPos pos) {
    this.tank = level.getCapability(ExperienceLibCapabilities.EXPERIENCE.block(), pos, null);
    this.pos = pos;
  }

  @Override
  public String getType() {
    return "experience";
  }

  @Override
  public long insert(AEKey what, long amount, Actionable type) {
    if (!(what instanceof ExperienceKey)) return 0;
    return tank.receiveExperience(amount, type.isSimulate());
  }

  @Override
  public boolean containsPatternInput(Set<AEKey> patternInputs) {
    return patternInputs.stream()
        .filter(key -> key instanceof ExperienceKey)
        .map(ExperienceKey.class::cast)
        .anyMatch(experienceKey -> tank.getExperience() > 0);
  }
}
