package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

@Getter
public class ExperiencePatternTarget implements PatternBusTarget {
  private final BasicExperienceTank tank;
  private final BlockPos pos;

  public ExperiencePatternTarget(BasicExperienceTank tank, BlockPos pos) {
    this.tank = tank;
    this.pos = pos;
  }

  public ExperiencePatternTarget(Level level, BlockPos pos) {
    IExperienceHandler handler = level.getCapability(ExperienceLibCapabilities.EXPERIENCE.block(), pos, null);
    // Wrap the handler's first tank into a BasicExperienceTank-compatible object
    if (handler != null && handler.getTanks() > 0) {
      BasicExperienceTank wrapper = new BasicExperienceTank(handler.getExperienceCapacity(), null);
      wrapper.setExperience(handler.getExperience());
      this.tank = wrapper;
    } else {
      this.tank = null;
    }
    this.pos = pos;
  }

  @Override
  public String getType() {
    return "experience";
  }

  @Override
  public long insert(AEKey what, long amount, Actionable type) {
    if (!(what instanceof AEExperienceKey) || tank == null) return 0;
    return tank.receiveExperience(amount, type.isSimulate());
  }

  @Override
  public boolean containsPatternInput(Set<AEKey> patternInputs) {
    if (tank == null) return false;
    return patternInputs.stream()
        .filter(key -> key instanceof AEExperienceKey)
        .map(AEExperienceKey.class::cast)
        .anyMatch(experienceKey -> tank.getExperience() > 0);
  }
}
