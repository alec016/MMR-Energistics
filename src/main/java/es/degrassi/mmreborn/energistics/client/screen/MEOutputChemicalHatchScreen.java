package es.degrassi.mmreborn.energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import es.degrassi.mmreborn.energistics.client.container.MEOutputChemicalHatchContainer;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputChemicalHatchEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MEOutputChemicalHatchScreen extends GridConnectedScreen<MEOutputChemicalHatchEntity,
    MEOutputChemicalHatchContainer> {

  public MEOutputChemicalHatchScreen(MEOutputChemicalHatchContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.addOpenPriorityButton();
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();
    this.menu.showPage(0);
  }
}
