package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Getter
public class AESourceHolder extends SourceStorage {
  private final MEOutputSourceHatchEntity owner;
  public AESourceHolder(MEOutputSourceHatchEntity owner) {
    super((int) owner.getStorage().getCapacity(SourceKeyType.TYPE));
    this.owner = owner;
  }

  @Override
  public int receiveSource(int toReceive, boolean simulate) {
    if (!canReceive() || toReceive <= 0) return 0;
    return (int) owner.getStorage().insert(0, SourceKey.KEY, toReceive, Actionable.ofSimulate(simulate));
  }

  @Override
  public int extractSource(int toExtract, boolean simulate) {
    if (!canExtract() || toExtract <= 0) return 0;
    return (int) owner.getStorage().extract(0, SourceKey.KEY, toExtract, Actionable.ofSimulate(simulate));
  }

  @Override
  public void setSource(int source) {
    GenericStack stack = owner.getStorage().getStack(0);
    if (stack == null) stack = new GenericStack(SourceKey.KEY, source);
    else stack = new GenericStack(stack.what(), source);
    owner.getStorage().setStack(0, stack);
  }

  @Override
  public int getSource() {
    GenericStack stack = owner.getStorage().getStack(0);
    if (stack == null) return 0;
    return (int) stack.amount();
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
