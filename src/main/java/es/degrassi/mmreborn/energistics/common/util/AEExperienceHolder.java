package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.impl.capability.BasicExperienceHandler;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputExperienceHatchEntity;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class AEExperienceHolder extends BasicExperienceHandler {
  private final MEOutputExperienceHatchEntity owner;
  public AEExperienceHolder(MEOutputExperienceHatchEntity owner) {
    super(owner.getStorage().size(), owner.getStorage().getCapacity(ExperienceKeyType.TYPE), null);
    this.owner = owner;
  }

  @Override
  public long receiveExperience(int tank, long toReceive, boolean simulate) {
    if (!canReceive(tank) || toReceive <= 0) return 0;
    return owner.getStorage().insert(tank, AEExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
  }

  @Override
  public long extractExperience(int tank, long toExtract, boolean simulate) {
    if (!canExtract(tank) || toExtract <= 0) return 0;
    return owner.getStorage().extract(tank, AEExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
  }

  @Override
  public void setExperience(int tank, long experience) {
    GenericStack stack = owner.getStorage().getStack(tank);
    if (stack == null) stack = new GenericStack(AEExperienceKey.KEY, experience);
    else stack = new GenericStack(stack.what(), experience);
    owner.getStorage().setStack(tank, stack);
  }

  @Override
  public long getExperience() {
    return owner.getStorage().getAvailableStacks().get(AEExperienceKey.KEY);
  }

  @Override
  public long receiveExperienceRecipe(int tank, long toReceive, boolean simulate) {
    if (toReceive <= 0) return 0;
    return owner.getStorage().insert(0, AEExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
  }

  @Override
  public long extractExperienceRecipe(int tank, long toExtract, boolean simulate) {
    if (toExtract <= 0) return 0;
    return owner.getStorage().extract(0, AEExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
  }

  public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
    super.deserializeNBT(lookupProvider, tag);
    owner.getStorage().readFromChildTag(tag, "storage", lookupProvider);
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
    CompoundTag nbt = super.serializeNBT(lookupProvider);
    owner.getStorage().writeToChildTag(nbt, "storage", lookupProvider);
    return nbt;
  }
}
