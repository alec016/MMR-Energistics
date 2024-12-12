package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import es.degrassi.appexp.me.key.ExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputExperienceHatchEntity;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Getter
public class AEExperienceHolder extends BasicExperienceTank {
  private final MEOutputExperienceHatchEntity owner;
  public AEExperienceHolder(MEOutputExperienceHatchEntity owner) {
    super(owner.getStorage().getCapacity(ExperienceKeyType.TYPE), null);
    this.owner = owner;
  }

  @Override
  public long receiveExperience(long toReceive, boolean simulate) {
    if (!canReceive() || toReceive <= 0) return 0;
    return owner.getStorage().insert(0, ExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
  }

  @Override
  public long extractExperience(long toExtract, boolean simulate) {
    if (!canExtract() || toExtract <= 0) return 0;
    return owner.getStorage().extract(0, ExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
  }

  @Override
  public void setExperience(long experience) {
    GenericStack stack = owner.getStorage().getStack(0);
    if (stack == null) stack = new GenericStack(ExperienceKey.KEY, experience);
    else stack = new GenericStack(stack.what(), experience);
    owner.getStorage().setStack(0, stack);
  }

  @Override
  public long getExperience() {
    GenericStack stack = owner.getStorage().getStack(0);
    if (stack == null) return 0;
    return stack.amount();
  }

  @Override
  public long receiveExperienceRecipe(long toReceive, boolean simulate) {
    if (toReceive <= 0) return 0;
    return owner.getStorage().insert(0, ExperienceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
  }

  @Override
  public long extractExperienceRecipe(long toExtract, boolean simulate) {
    if (toExtract <= 0) return 0;
    return owner.getStorage().extract(0, ExperienceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
  }

  public void deserializeNBT(HolderLookup.Provider lookupProvider, Tag tag) {
    if (!(tag instanceof CompoundTag nbt)) return;
    owner.getStorage().readFromChildTag(nbt, "storage", lookupProvider);
  }

  @Override
  public Tag serializeNBT(HolderLookup.Provider lookupProvider) {
    CompoundTag nbt = new CompoundTag();
    owner.getStorage().writeToChildTag(nbt, "storage", lookupProvider);
    return nbt;
  }
}
