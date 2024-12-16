package es.degrassi.mmreborn.energistics.client.screen;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.localization.GuiText;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.ConfigButtonPacket;
import es.degrassi.mmreborn.energistics.api.services.settings.MMRESettings;
import es.degrassi.mmreborn.energistics.client.container.MEPatternBusContainer;
import es.degrassi.mmreborn.energistics.client.screen.widget.MMREServerSettingToggleButton;
import es.degrassi.mmreborn.energistics.client.screen.widget.PatternBusLockReason;
import es.degrassi.mmreborn.energistics.common.entity.base.MEPatternBus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class MEPatternBusScreen extends GridConnectedScreen<MEPatternBus, MEPatternBusContainer> {

  private final MMREServerSettingToggleButton<YesNo> blockingModeButton;
  private final MMREServerSettingToggleButton<LockCraftingMode> lockCraftingModeButton;
  private final ToggleButton showInPatternAccessTerminalButton;
  private final PatternBusLockReason lockReason;

  public MEPatternBusScreen(MEPatternBusContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
    super(menu, playerInventory, title, style);

    this.blockingModeButton = new MMREServerSettingToggleButton<>(MMRESettings.BLOCKING_MODE, YesNo.NO);
    this.addToLeftToolbar(this.blockingModeButton);

    lockCraftingModeButton = new MMREServerSettingToggleButton<>(MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.NONE);
    this.addToLeftToolbar(lockCraftingModeButton);

    widgets.addOpenPriorityButton();

    this.showInPatternAccessTerminalButton = new ToggleButton(Icon.PATTERN_ACCESS_SHOW,
        Icon.PATTERN_ACCESS_HIDE,
        GuiText.PatternAccessTerminal.text(), GuiText.PatternAccessTerminalHint.text(),
        btn -> selectNextPatternProviderMode());
    this.addToLeftToolbar(this.showInPatternAccessTerminalButton);

    this.lockReason = new PatternBusLockReason(this);
    widgets.add("lockReason", this.lockReason);
  }

  @Override
  protected void updateBeforeRender() {
    super.updateBeforeRender();

    this.lockReason.setVisible(menu.getLockCraftingMode() != LockCraftingMode.NONE);
    this.blockingModeButton.set(this.menu.getBlockingMode());
    this.lockCraftingModeButton.set(this.menu.getLockCraftingMode());
    this.showInPatternAccessTerminalButton.setState(this.menu.getShowInAccessTerminal() == YesNo.YES);
  }

  private void selectNextPatternProviderMode() {
    final boolean backwards = isHandlingRightClick();
    ServerboundPacket message = new ConfigButtonPacket(Settings.PATTERN_ACCESS_TERMINAL, backwards);
    PacketDistributor.sendToServer(message);
  }
}
