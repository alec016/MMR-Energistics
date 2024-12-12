package es.degrassi.mmreborn.energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import es.degrassi.mmreborn.energistics.client.container.MEOutputHatchContainer;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputHatchEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MEOutputHatchScreen extends GridConnectedScreen<MEOutputHatchEntity, MEOutputHatchContainer> {

  public MEOutputHatchScreen(MEOutputHatchContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.addOpenPriorityButton();
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();
    this.menu.showPage(0);
  }
}
