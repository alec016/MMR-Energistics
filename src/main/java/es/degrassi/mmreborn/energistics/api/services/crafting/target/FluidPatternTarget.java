package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import es.degrassi.mmreborn.common.util.HybridTank;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Set;

@Getter
public class FluidPatternTarget implements PatternBusTarget {
  private final IFluidHandler tank;
  private final BlockPos pos;

  public FluidPatternTarget(HybridTank tank, BlockPos pos) {
    this.tank = tank;
    this.pos = pos;
  }

  public FluidPatternTarget(Level level, BlockPos pos) {
    this.pos = pos;
    this.tank = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
  }

  @Override
  public String getType() {
    return "fluid";
  }

  @Override
  public long insert(AEKey what, long amount, Actionable type) {
    if (!(what instanceof AEFluidKey k)) return 0;
    return tank.fill(k.toStack((int) amount), type.getFluidAction());
  }

  @Override
  public boolean containsPatternInput(Set<AEKey> patternInputs) {
    return patternInputs.stream()
        .filter(AEFluidKey::is)
        .map(AEFluidKey.class::cast)
        .map(AEFluidKey::getFluid)
        .anyMatch(fluid -> {
          for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack stack = tank.getFluidInTank(i);
            if (stack.is(fluid)) return true;
          }
          return false;
        });
  }
}
