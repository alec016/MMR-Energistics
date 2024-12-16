package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.networking.IGridNodeListener;
import appeng.api.util.AEColor;
import appeng.me.ManagedGridNode;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class SerializableManagedGridNode extends ManagedGridNode implements INBTSerializable<CompoundTag> {

  public <T> SerializableManagedGridNode(T nodeOwner, IGridNodeListener<? super T> listener) {
    super(nodeOwner, listener);
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider provider) {
    CompoundTag tag = new CompoundTag();
    super.saveToNBT(tag);
    tag.putString("gridColor", getGridColor().name());
    return tag;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
    super.loadFromNBT(tag);
    setGridColor(AEColor.valueOf(tag.getString("gridColor")));
  }
}
