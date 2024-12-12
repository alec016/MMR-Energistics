package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputChemicalHatchEntity;
import lombok.Getter;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.functions.ConstantPredicates;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

@Getter
public class AEChemicalTankHolder extends BasicChemicalTank {
  private final MEOutputChemicalHatchEntity owner;

  public AEChemicalTankHolder(MEOutputChemicalHatchEntity owner) {
    super(
        owner.getStorage().getCapacity(MekanismKeyType.TYPE),
        ((chemical, automationType) -> owner.getSize().isInput() || automationType == AutomationType.INTERNAL),
        ((chemical, automationType) -> !owner.getSize().isInput() || automationType == AutomationType.INTERNAL),
        ConstantPredicates.alwaysTrue(),
        ChemicalAttributeValidator.ALWAYS_ALLOW,
        null
    );
    this.owner = owner;
  }

  @Override
  public int getChemicalTanks() {
    return owner.getStorage().size();
  }

  @Override
  public boolean isValid(ChemicalStack stack) {
    for (int i = 0; i < owner.getStorage().size(); i++) {
      if (owner.getStorage().isAllowedIn(i, MekanismKey.of(stack)))
        return true;
    }
    return false;
  }

  @Override
  public ChemicalStack getChemicalInTank(int tank) {
    if (tank < 0 || tank >= getChemicalTanks()) {
      return ChemicalStack.EMPTY;
    }
    GenericStack stack = owner.getStorage().getStack(tank);
    if (stack == null) return ChemicalStack.EMPTY;
    return new ChemicalStack(((MekanismKey) stack.what()).getStack().getChemical(), (int) stack.amount());
  }

  @Override
  public long getChemicalTankCapacity(int tank) {
    return owner.getStorage().getCapacity(MekanismKeyType.TYPE);
  }

  public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
    owner.getStorage().readFromChildTag(nbt, "storage", lookupProvider);
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
    CompoundTag nbt = new CompoundTag();
    owner.getStorage().writeToChildTag(nbt, "storage", lookupProvider);
    return nbt;
  }

  @Override
  public boolean isValid(int tank, ChemicalStack stack) {
    return owner.getStorage().isAllowedIn(tank, MekanismKey.of(stack));
  }

  private static Actionable actionableFromAction(Action action) {
    return switch (action) {
      case SIMULATE -> Actionable.SIMULATE;
      case EXECUTE -> Actionable.MODULATE;
    };
  }

  @Override
  public ChemicalStack insert(ChemicalStack resource, Action action, AutomationType automationType) {
    MekanismKey what = MekanismKey.of(resource);
    if (what == null) {
      return resource;
    } else {
      long inserted = 0;

      for (int i = 0; i < owner.getStorage().size() && inserted < resource.getAmount(); ++i) {
        inserted += owner.getStorage().insert(i, what, resource.getAmount() - inserted, actionableFromAction(action));
      }

      return resource.copyWithAmount(resource.getAmount() - inserted);
    }
  }

  @Override
  public ChemicalStack extract(long amount, Action action, AutomationType automationType) {
    if (amount <= 0) return ChemicalStack.EMPTY;
    for (AEKey key : owner.getStorage().getAvailableStacks().keySet()) {
      if (key instanceof MekanismKey mkey) {
        long extracted = owner.getStorage().extract(mkey, amount, Actionable.SIMULATE,
            owner.getActionSource());
        if (extracted == amount)
          return mkey.getStack().copyWithAmount(extracted);
      }

    }

    return ChemicalStack.EMPTY;
  }

  @Override
  public void onContentsChanged() {
    owner.saveChanges();
    owner.updatePlan();
  }

  @Override
  public void setStack(ChemicalStack stack) {
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}
