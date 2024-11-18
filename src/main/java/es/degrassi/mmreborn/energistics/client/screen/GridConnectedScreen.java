package es.degrassi.mmreborn.energistics.client.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.UpgradeableMenu;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GridConnectedScreen<E extends MEEntity, C extends UpgradeableMenu<E>> extends UpgradeableScreen<C> {
  public GridConnectedScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
  }
}
