package es.degrassi.mmreborn.energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.common.machine.component.ExperienceComponent;
import es.degrassi.mmreborn.energistics.client.container.MEOutputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputExperienceHatchEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MEOutputExperienceHatchScreen extends GridConnectedScreen<MEOutputExperienceHatchEntity,
    MEOutputExperienceHatchContainer, ExperienceComponent, IExperienceHandler> {

  public MEOutputExperienceHatchScreen(MEOutputExperienceHatchContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    widgets.addOpenPriorityButton();
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();
    this.menu.showPage(0);
  }
}
