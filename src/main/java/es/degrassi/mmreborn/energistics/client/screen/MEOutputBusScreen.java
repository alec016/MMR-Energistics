package es.degrassi.mmreborn.energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import es.degrassi.mmreborn.energistics.client.container.MEOutputBusContainer;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputBusEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MEOutputBusScreen extends GridConnectedScreen<MEOutputBusEntity, MEOutputBusContainer> {

  public MEOutputBusScreen(MEOutputBusContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.addOpenPriorityButton();
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();
    this.menu.showPage(0);
  }
}
