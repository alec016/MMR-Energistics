package es.degrassi.mmreborn.energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import es.degrassi.mmreborn.energistics.client.container.MEOutputSourceHatchContainer;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputSourceHatchEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MEOutputSourceHatchScreen extends GridConnectedScreen<MEOutputSourceHatchEntity,
    MEOutputSourceHatchContainer> {

  public MEOutputSourceHatchScreen(MEOutputSourceHatchContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.addOpenPriorityButton();
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();
    this.menu.showPage(0);
  }
}
