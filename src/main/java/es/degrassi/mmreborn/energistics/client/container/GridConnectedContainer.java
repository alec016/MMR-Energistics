package es.degrassi.mmreborn.energistics.client.container;

import appeng.menu.implementations.UpgradeableMenu;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class GridConnectedContainer<
  E extends MEEntity
> extends UpgradeableMenu<E> {
  protected GridConnectedContainer(E entity, Player player, @Nullable MenuType<?> menuType, int id) {
    super(menuType, id, player.getInventory(), entity);
  }
}
