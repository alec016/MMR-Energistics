package es.degrassi.mmreborn.energistics.common.entity;

import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.util.AECableType;
import appeng.me.helpers.IGridConnectedBlockEntity;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.Set;

public interface IMEConnectedEntity extends IGridConnectedBlockEntity {

  default MEEntity self() {
    return (MEEntity) this;
  }

  long getMEUpdateInterval();

  /**
   * @return return {@code true} if current machine connected to a valid ME network, {@code false} otherwise.
   */
  boolean isOnline();

  void setOnline(boolean online);

  /**
   * @return {@code true} if current machine should interact with ME network, {@code false} otherwise.
   */
  default boolean shouldSyncME() {
    return self().getOffsetTimer() % getMEUpdateInterval() == 0;
  }

  default AECableType getCableConnectionType(Direction dir) {
    return AECableType.SMART;
  }
  /**
   * Update me network connection status.
   *
   * @return the updated status.
   */
  default boolean updateMEStatus() {
    var proxy = getMainNode();
    setOnline(proxy.isActive());
    return isOnline();
  }

  @Override
  default void onMainNodeStateChanged(IGridNodeListener.State reason) {
    this.updateMEStatus();
  }

  default Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
    return EnumSet.of(orientation.getSide(RelativeSide.FRONT));
  }
}
