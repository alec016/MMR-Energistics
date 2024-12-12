package es.degrassi.mmreborn.energistics.client.screen;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import es.degrassi.mmreborn.energistics.client.container.MEInputExperienceHatchContainer;
import es.degrassi.mmreborn.energistics.client.container.MEInputSourceHatchContainer;
import es.degrassi.mmreborn.energistics.common.entity.MEInputExperienceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputSourceHatchEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MEInputExperienceHatchScreen extends GridConnectedScreen<MEInputExperienceHatchEntity, MEInputExperienceHatchContainer> {
  private final SettingToggleButton<FuzzyMode> fuzzyMode;

  public MEInputExperienceHatchScreen(MEInputExperienceHatchContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);
    this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    addToLeftToolbar(this.fuzzyMode);
    widgets.addOpenPriorityButton();
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();

    this.fuzzyMode.set(menu.getFuzzyMode());
    this.fuzzyMode.setVisibility(menu.hasUpgrade(AEItems.FUZZY_CARD));

    this.menu.showPage(0);
  }
}
