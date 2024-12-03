package es.degrassi.mmreborn.energistics.mixin;

import appeng.api.networking.IGridNode;
import appeng.me.GridNode;
import es.degrassi.mmreborn.energistics.api.node.MMREGridNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GridNode.class)
public abstract class GridNodeMixin implements MMREGridNode {
  @Shadow abstract boolean hasConnection(IGridNode otherSide);

  @Override
  public boolean mmre$hasConnection(IGridNode node) {
    return hasConnection(node);
  }
}
