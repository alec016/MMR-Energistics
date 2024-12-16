package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import lombok.Getter;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

import static me.ramidzkh.mekae2.MekCapabilities.CHEMICAL;

@Getter
public class ChemicalPatternTarget implements PatternBusTarget {
  private final IChemicalHandler tank;
  private final BlockPos pos;

  public ChemicalPatternTarget(BasicChemicalTank tank, BlockPos pos) {
    this.tank = tank;
    this.pos = pos;
  }

  public ChemicalPatternTarget(Level level, BlockPos pos) {
    this.tank = level.getCapability(CHEMICAL.block(), pos, null);
    this.pos = pos;
  }

  @Override
  public String getType() {
    return "chemical";
  }

  @Override
  public long insert(AEKey what, long amount, Actionable type) {
    if (!(what instanceof MekanismKey k)) return 0;
    return amount - tank.insertChemical(k.withAmount(amount), Action.fromFluidAction(type.getFluidAction())).getAmount();
  }

  @Override
  public boolean containsPatternInput(Set<AEKey> patternInputs) {
    return patternInputs.stream()
        .filter(key -> key instanceof MekanismKey)
        .map(MekanismKey.class::cast)
        .map(MekanismKey::getStack)
        .map(ChemicalStack::getChemical)
        .anyMatch(chemical -> {
          for (int i = 0; i < tank.getChemicalTanks(); i++) {
            ChemicalStack stack = tank.getChemicalInTank(i);
            if (stack.is(chemical)) return true;
          }
          return false;
        });
  }
}
