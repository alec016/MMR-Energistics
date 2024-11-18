package es.degrassi.mmreborn.energistics.common.util;

import appeng.api.networking.GridFlags;
import appeng.me.helpers.BlockEntityNodeListener;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumSet;

@Getter
public class GridNodeHolder {

  protected final MEEntity machine;
  protected final SerializableManagedGridNode mainNode;

  public GridNodeHolder(MEEntity entity) {
    this.machine = entity;
    this.mainNode = (SerializableManagedGridNode) new SerializableManagedGridNode(entity, BlockEntityNodeListener.INSTANCE)
        .setFlags(GridFlags.REQUIRE_CHANNEL)
        .setIdlePowerUsage(entity.getSize().getIdlePowerDrainOnConnected())
        .setInWorldNode(true)
        .setExposedOnSides(EnumSet.allOf(Direction.class))
        .setTagName("proxy")
        .setVisualRepresentation(((MEBlock) entity.getBlockState().getBlock()).item());
  }

  protected void createMainNode() {
    getMainNode().create(machine.getLevel(), machine.getBlockPos());
  }

  public void onUnloaded() {
    getMainNode().destroy();
  }

  public void onLoad() {
    createMainNode();
  }

  @SuppressWarnings("unused")
  public boolean onGridNodeDirty(SerializableManagedGridNode node) {
    return node != null && node.isActive() && node.isOnline();
  }

  @SuppressWarnings("unused")
  public CompoundTag serializeGridNode() {
    return getMainNode().serializeNBT(machine.getLevel().registryAccess());
  }

  @SuppressWarnings("unused")
  public SerializableManagedGridNode deserializeGridNode(CompoundTag tag) {
    this.getMainNode().deserializeNBT(machine.getLevel().registryAccess(), tag);
    return this.getMainNode();
  }
}
