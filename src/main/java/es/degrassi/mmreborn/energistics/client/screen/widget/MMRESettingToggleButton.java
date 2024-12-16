package es.degrassi.mmreborn.energistics.client.screen.widget;

import appeng.api.config.AccessRestriction;
import appeng.api.config.CondenserOutput;
import appeng.api.config.CpuSelectionMode;
import appeng.api.config.FullnessMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.InscriberInputCapacity;
import appeng.api.config.LockCraftingMode;
import appeng.api.config.OperationMode;
import appeng.api.config.PowerUnit;
import appeng.api.config.RedstoneMode;
import appeng.api.config.RelativeDirection;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Setting;
import appeng.api.config.ShowPatternProviders;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.StorageFilter;
import appeng.api.config.TerminalStyle;
import appeng.api.config.ViewItems;
import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ButtonToolTips;
import appeng.core.network.ServerboundPacket;
import appeng.util.EnumCycler;
import es.degrassi.mmreborn.energistics.api.services.settings.MMRESettings;
import es.degrassi.mmreborn.energistics.common.network.client.ConfigButtonPacket;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MMRESettingToggleButton<T extends Enum<T>> extends IconButton {
  private static Map<EnumPair<?>, ButtonAppearance> appearances;
  private final Setting<T> buttonSetting;
  private final IHandler<MMRESettingToggleButton<T>> onPress;
  private final EnumSet<T> validValues;
  @Getter
  private T currentValue;

  @FunctionalInterface
  public interface IHandler<T extends MMRESettingToggleButton<?>> {
    void handle(T button, boolean backwards);
  }

  public MMRESettingToggleButton(Setting<T> setting, T val, IHandler<MMRESettingToggleButton<T>> onPress) {
    this(setting, val, t -> true, onPress);
  }

  public MMRESettingToggleButton(Setting<T> setting, T val, Predicate<T> isValidValue, IHandler<MMRESettingToggleButton<T>> onPress) {
    super(MMRESettingToggleButton::onPress);
    this.onPress = onPress;

    // Build a list of values (in order) that are valid w.r.t. the given predicate
    EnumSet<T> validValues = EnumSet.allOf(val.getDeclaringClass());
    validValues.removeIf(isValidValue.negate());
    validValues.removeIf(s -> !setting.getValues().contains(s));
    this.validValues = validValues;

    this.buttonSetting = setting;
    this.currentValue = val;

    if (appearances == null) {
      appearances = new HashMap<>();
      registerApp(Icon.CONDENSER_OUTPUT_TRASH, MMRESettings.CONDENSER_OUTPUT, CondenserOutput.TRASH,
          ButtonToolTips.CondenserOutput,
          ButtonToolTips.Trash);
      registerApp(Icon.CONDENSER_OUTPUT_MATTER_BALL, MMRESettings.CONDENSER_OUTPUT, CondenserOutput.MATTER_BALLS,
          ButtonToolTips.CondenserOutput,
          ButtonToolTips.MatterBalls.text(CondenserOutput.MATTER_BALLS.requiredPower));
      registerApp(Icon.CONDENSER_OUTPUT_SINGULARITY, MMRESettings.CONDENSER_OUTPUT, CondenserOutput.SINGULARITY,
          ButtonToolTips.CondenserOutput,
          ButtonToolTips.Singularity.text(CondenserOutput.SINGULARITY.requiredPower));

      registerApp(Icon.ACCESS_READ, MMRESettings.ACCESS, AccessRestriction.READ, ButtonToolTips.IOMode,
          ButtonToolTips.Read);
      registerApp(Icon.ACCESS_WRITE, MMRESettings.ACCESS, AccessRestriction.WRITE, ButtonToolTips.IOMode,
          ButtonToolTips.Write);
      registerApp(Icon.ACCESS_READ_WRITE, MMRESettings.ACCESS, AccessRestriction.READ_WRITE, ButtonToolTips.IOMode,
          ButtonToolTips.ReadWrite);

      registerApp(Icon.POWER_UNIT_AE, MMRESettings.POWER_UNITS, PowerUnit.AE, ButtonToolTips.PowerUnits,
          PowerUnit.AE.textComponent());
      // registerApp(Icon.POWER_UNIT_EU, Settings.POWER_UNITS, PowerUnits.EU, ButtonToolTips.PowerUnits,
      // PowerUnits.EU.textComponent());
      registerApp(Icon.POWER_UNIT_RF, MMRESettings.POWER_UNITS, PowerUnit.FE, ButtonToolTips.PowerUnits,
          PowerUnit.FE.textComponent());

      registerApp(Icon.REDSTONE_IGNORE, MMRESettings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE,
          ButtonToolTips.RedstoneMode,
          ButtonToolTips.AlwaysActive);
      registerApp(Icon.REDSTONE_LOW, MMRESettings.REDSTONE_CONTROLLED, RedstoneMode.LOW_SIGNAL,
          ButtonToolTips.RedstoneMode,
          ButtonToolTips.ActiveWithoutSignal);
      registerApp(Icon.REDSTONE_HIGH, MMRESettings.REDSTONE_CONTROLLED, RedstoneMode.HIGH_SIGNAL,
          ButtonToolTips.RedstoneMode,
          ButtonToolTips.ActiveWithSignal);
      registerApp(Icon.REDSTONE_PULSE, MMRESettings.REDSTONE_CONTROLLED, RedstoneMode.SIGNAL_PULSE,
          ButtonToolTips.RedstoneMode,
          ButtonToolTips.ActiveOnPulse);

      registerApp(Icon.REDSTONE_BELOW, MMRESettings.REDSTONE_EMITTER, RedstoneMode.LOW_SIGNAL,
          ButtonToolTips.RedstoneMode,
          ButtonToolTips.EmitLevelsBelow);
      registerApp(Icon.REDSTONE_ABOVE_EQUAL, MMRESettings.REDSTONE_EMITTER, RedstoneMode.HIGH_SIGNAL,
          ButtonToolTips.RedstoneMode,
          ButtonToolTips.EmitLevelAbove);

      registerApp(Icon.ARROW_LEFT, MMRESettings.OPERATION_MODE, OperationMode.FILL,
          ButtonToolTips.TransferDirection,
          ButtonToolTips.TransferToStorageCell);
      registerApp(Icon.ARROW_RIGHT, MMRESettings.OPERATION_MODE, OperationMode.EMPTY,
          ButtonToolTips.TransferDirection,
          ButtonToolTips.TransferToNetwork);

      registerApp(Icon.ARROW_LEFT, MMRESettings.IO_DIRECTION, RelativeDirection.LEFT,
          ButtonToolTips.TransferDirection,
          ButtonToolTips.TransferToStorageCell);
      registerApp(Icon.ARROW_RIGHT, MMRESettings.IO_DIRECTION, RelativeDirection.RIGHT,
          ButtonToolTips.TransferDirection,
          ButtonToolTips.TransferToNetwork);

      registerApp(Icon.ARROW_UP, MMRESettings.SORT_DIRECTION, SortDir.ASCENDING, ButtonToolTips.SortOrder,
          ButtonToolTips.Ascending);
      registerApp(Icon.ARROW_DOWN, MMRESettings.SORT_DIRECTION, SortDir.DESCENDING, ButtonToolTips.SortOrder,
          ButtonToolTips.Descending);

      registerApp(Icon.TERMINAL_STYLE_SMALL, MMRESettings.TERMINAL_STYLE, TerminalStyle.SMALL,
          ButtonToolTips.TerminalStyle,
          ButtonToolTips.TerminalStyle_Small);
      registerApp(Icon.TERMINAL_STYLE_MEDIUM, MMRESettings.TERMINAL_STYLE, TerminalStyle.MEDIUM,
          ButtonToolTips.TerminalStyle,
          ButtonToolTips.TerminalStyle_Medium);
      registerApp(Icon.TERMINAL_STYLE_TALL, MMRESettings.TERMINAL_STYLE, TerminalStyle.TALL,
          ButtonToolTips.TerminalStyle,
          ButtonToolTips.TerminalStyle_Tall);
      registerApp(Icon.TERMINAL_STYLE_FULL, MMRESettings.TERMINAL_STYLE, TerminalStyle.FULL,
          ButtonToolTips.TerminalStyle,
          ButtonToolTips.TerminalStyle_Full);

      registerApp(Icon.SORT_BY_NAME, MMRESettings.SORT_BY, SortOrder.NAME, ButtonToolTips.SortBy,
          ButtonToolTips.ItemName);
      registerApp(Icon.SORT_BY_AMOUNT, MMRESettings.SORT_BY, SortOrder.AMOUNT, ButtonToolTips.SortBy,
          ButtonToolTips.NumberOfItems);
      registerApp(Icon.SORT_BY_MOD, MMRESettings.SORT_BY, SortOrder.MOD, ButtonToolTips.SortBy, ButtonToolTips.Mod);

      registerApp(Icon.VIEW_MODE_STORED, MMRESettings.VIEW_MODE, ViewItems.STORED, ButtonToolTips.View,
          ButtonToolTips.StoredItems);
      registerApp(Icon.VIEW_MODE_ALL, MMRESettings.VIEW_MODE, ViewItems.ALL, ButtonToolTips.View,
          ButtonToolTips.StoredCraftable);
      registerApp(Icon.VIEW_MODE_CRAFTING, MMRESettings.VIEW_MODE, ViewItems.CRAFTABLE, ButtonToolTips.View,
          ButtonToolTips.Craftable);

      registerApp(Icon.FUZZY_PERCENT_25, MMRESettings.FUZZY_MODE, FuzzyMode.PERCENT_25, ButtonToolTips.FuzzyMode,
          ButtonToolTips.FZPercent_25);
      registerApp(Icon.FUZZY_PERCENT_50, MMRESettings.FUZZY_MODE, FuzzyMode.PERCENT_50, ButtonToolTips.FuzzyMode,
          ButtonToolTips.FZPercent_50);
      registerApp(Icon.FUZZY_PERCENT_75, MMRESettings.FUZZY_MODE, FuzzyMode.PERCENT_75, ButtonToolTips.FuzzyMode,
          ButtonToolTips.FZPercent_75);
      registerApp(Icon.FUZZY_PERCENT_99, MMRESettings.FUZZY_MODE, FuzzyMode.PERCENT_99, ButtonToolTips.FuzzyMode,
          ButtonToolTips.FZPercent_99);
      registerApp(Icon.FUZZY_IGNORE, MMRESettings.FUZZY_MODE, FuzzyMode.IGNORE_ALL, ButtonToolTips.FuzzyMode,
          ButtonToolTips.FZIgnoreAll);

      registerApp(Icon.FULLNESS_EMPTY, MMRESettings.FULLNESS_MODE, FullnessMode.EMPTY, ButtonToolTips.OperationMode,
          ButtonToolTips.MoveWhenEmpty);
      registerApp(Icon.FULLNESS_HALF, MMRESettings.FULLNESS_MODE, FullnessMode.HALF, ButtonToolTips.OperationMode,
          ButtonToolTips.MoveWhenWorkIsDone);
      registerApp(Icon.FULLNESS_FULL, MMRESettings.FULLNESS_MODE, FullnessMode.FULL, ButtonToolTips.OperationMode,
          ButtonToolTips.MoveWhenFull);

      registerApp(Icon.BLOCKING_MODE_YES, MMRESettings.BLOCKING_MODE, YesNo.YES, ButtonToolTips.InterfaceBlockingMode,
          ButtonToolTips.Blocking);
      registerApp(Icon.BLOCKING_MODE_NO, MMRESettings.BLOCKING_MODE, YesNo.NO, ButtonToolTips.InterfaceBlockingMode,
          ButtonToolTips.NonBlocking);

      registerApp(Icon.VIEW_MODE_CRAFTING, MMRESettings.CRAFT_ONLY, YesNo.YES, ButtonToolTips.Craft,
          ButtonToolTips.CraftOnly);
      registerApp(Icon.VIEW_MODE_ALL, MMRESettings.CRAFT_ONLY, YesNo.NO, ButtonToolTips.Craft,
          ButtonToolTips.CraftEither);

      registerApp(Icon.CRAFT_HAMMER, MMRESettings.CRAFT_VIA_REDSTONE, YesNo.YES, ButtonToolTips.EmitterMode,
          ButtonToolTips.CraftViaRedstone);
      registerApp(Icon.ACCESS_READ, MMRESettings.CRAFT_VIA_REDSTONE, YesNo.NO, ButtonToolTips.EmitterMode,
          ButtonToolTips.EmitWhenCrafting);

      registerApp(Icon.STORAGE_FILTER_EXTRACTABLE_ONLY, MMRESettings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY,
          ButtonToolTips.ReportInaccessibleItems, ButtonToolTips.ReportInaccessibleItemsNo);
      registerApp(Icon.STORAGE_FILTER_EXTRACTABLE_NONE, MMRESettings.STORAGE_FILTER, StorageFilter.NONE,
          ButtonToolTips.ReportInaccessibleItems,
          ButtonToolTips.ReportInaccessibleItemsYes);

      registerApp(Icon.PLACEMENT_BLOCK, MMRESettings.PLACE_BLOCK, YesNo.YES, ButtonToolTips.BlockPlacement,
          ButtonToolTips.BlockPlacementYes);
      registerApp(Icon.PLACEMENT_ITEM, MMRESettings.PLACE_BLOCK, YesNo.NO, ButtonToolTips.BlockPlacement,
          ButtonToolTips.BlockPlacementNo);

      registerApp(Icon.SCHEDULING_DEFAULT, MMRESettings.SCHEDULING_MODE, SchedulingMode.DEFAULT,
          ButtonToolTips.SchedulingMode,
          ButtonToolTips.SchedulingModeDefault);
      registerApp(Icon.SCHEDULING_ROUND_ROBIN, MMRESettings.SCHEDULING_MODE, SchedulingMode.ROUNDROBIN,
          ButtonToolTips.SchedulingMode,
          ButtonToolTips.SchedulingModeRoundRobin);
      registerApp(Icon.SCHEDULING_RANDOM, MMRESettings.SCHEDULING_MODE, SchedulingMode.RANDOM,
          ButtonToolTips.SchedulingMode,
          ButtonToolTips.SchedulingModeRandom);

      registerApp(Icon.OVERLAY_OFF, MMRESettings.OVERLAY_MODE, YesNo.NO, ButtonToolTips.OverlayMode,
          ButtonToolTips.OverlayModeNo);
      registerApp(Icon.OVERLAY_ON, MMRESettings.OVERLAY_MODE, YesNo.YES, ButtonToolTips.OverlayMode,
          ButtonToolTips.OverlayModeYes);

      registerApp(Icon.FILTER_ON_EXTRACT_ENABLED, MMRESettings.FILTER_ON_EXTRACT, YesNo.YES,
          ButtonToolTips.FilterOnExtract, ButtonToolTips.FilterOnExtractEnabled);
      registerApp(Icon.FILTER_ON_EXTRACT_DISABLED, MMRESettings.FILTER_ON_EXTRACT, YesNo.NO,
          ButtonToolTips.FilterOnExtract, ButtonToolTips.FilterOnExtractDisabled);

      registerApp(Icon.CRAFT_HAMMER, MMRESettings.CPU_SELECTION_MODE, CpuSelectionMode.ANY,
          ButtonToolTips.CpuSelectionMode, ButtonToolTips.CpuSelectionModeAny);
      registerApp(AEParts.TERMINAL, MMRESettings.CPU_SELECTION_MODE, CpuSelectionMode.PLAYER_ONLY,
          ButtonToolTips.CpuSelectionMode, ButtonToolTips.CpuSelectionModePlayersOnly.text());
      registerApp(AEParts.EXPORT_BUS, MMRESettings.CPU_SELECTION_MODE, CpuSelectionMode.MACHINE_ONLY,
          ButtonToolTips.CpuSelectionMode, ButtonToolTips.CpuSelectionModeAutomationOnly.text());

      registerApp(Icon.PATTERN_TERMINAL_ALL, MMRESettings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.ALL,
          ButtonToolTips.InterfaceTerminalDisplayMode,
          ButtonToolTips.ShowAllProviders);
      registerApp(Icon.PATTERN_TERMINAL_VISIBLE, MMRESettings.TERMINAL_SHOW_PATTERN_PROVIDERS,
          ShowPatternProviders.VISIBLE,
          ButtonToolTips.InterfaceTerminalDisplayMode,
          ButtonToolTips.ShowVisibleProviders);
      registerApp(Icon.PATTERN_TERMINAL_NOT_FULL, MMRESettings.TERMINAL_SHOW_PATTERN_PROVIDERS,
          ShowPatternProviders.NOT_FULL,
          ButtonToolTips.InterfaceTerminalDisplayMode,
          ButtonToolTips.ShowNonFullProviders);

      registerApp(Icon.INSCRIBER_SEPARATE_SIDES, MMRESettings.INSCRIBER_SEPARATE_SIDES, YesNo.YES,
          ButtonToolTips.InscriberSideness,
          ButtonToolTips.InscriberSidenessSeparate);
      registerApp(Icon.INSCRIBER_COMBINED_SIDES, MMRESettings.INSCRIBER_SEPARATE_SIDES, YesNo.NO,
          ButtonToolTips.InscriberSideness,
          ButtonToolTips.InscriberSidenessCombined);

      registerApp(Icon.AUTO_EXPORT_ON, MMRESettings.AUTO_EXPORT, YesNo.YES,
          ButtonToolTips.AutoExport,
          ButtonToolTips.AutoExportOn);
      registerApp(Icon.AUTO_EXPORT_OFF, MMRESettings.AUTO_EXPORT, YesNo.NO,
          ButtonToolTips.AutoExport,
          ButtonToolTips.AutoExportOff);

      registerApp(Icon.INSCRIBER_BUFFER_64, MMRESettings.INSCRIBER_INPUT_CAPACITY, InscriberInputCapacity.SIXTY_FOUR,
          ButtonToolTips.InscriberBufferSize,
          ButtonToolTips.InscriberBufferHigh);
      registerApp(Icon.INSCRIBER_BUFFER_4, MMRESettings.INSCRIBER_INPUT_CAPACITY, InscriberInputCapacity.FOUR,
          ButtonToolTips.InscriberBufferSize,
          ButtonToolTips.InscriberBufferLow);
      registerApp(Icon.INSCRIBER_BUFFER_1, MMRESettings.INSCRIBER_INPUT_CAPACITY, InscriberInputCapacity.ONE,
          ButtonToolTips.InscriberBufferSize,
          ButtonToolTips.InscriberBufferVeryLow);

      registerApp(Icon.UNLOCKED, MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.NONE,
          ButtonToolTips.LockCraftingMode,
          ButtonToolTips.LockCraftingModeNone);
      registerApp(Icon.REDSTONE_ON, MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.LOCK_WHILE_HIGH,
          ButtonToolTips.LockCraftingMode,
          ButtonToolTips.LockCraftingWhileRedstoneHigh);
      registerApp(Icon.REDSTONE_OFF, MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.LOCK_WHILE_LOW,
          ButtonToolTips.LockCraftingMode,
          ButtonToolTips.LockCraftingWhileRedstoneLow);
      registerApp(Icon.REDSTONE_PULSE, MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.LOCK_UNTIL_PULSE,
          ButtonToolTips.LockCraftingMode,
          ButtonToolTips.LockCraftingUntilRedstonePulse);
      registerApp(Icon.ENTER, MMRESettings.LOCK_CRAFTING_MODE, LockCraftingMode.LOCK_UNTIL_RESULT,
          ButtonToolTips.LockCraftingMode,
          ButtonToolTips.LockCraftingUntilResultReturned);
    }
  }

  private static void onPress(Button btn) {
    if (btn instanceof MMRESettingToggleButton) {
      ((MMRESettingToggleButton<?>) btn).triggerPress();
    }
  }

  private void triggerPress() {
    boolean backwards = false;
    // This isn't great, but we don't get any information about right-clicks
    // otherwise
    Screen currentScreen = Minecraft.getInstance().screen;
    if (currentScreen instanceof AEBaseScreen) {
      backwards = ((AEBaseScreen<?>) currentScreen).isHandlingRightClick();
    }
    onPress.handle(this, backwards);
  }

  @Nullable
  private ButtonAppearance getApperance() {
    if (this.getSetting() != null && this.getCurrentValue() != null) {
      return appearances.get(new EnumPair<>(this.getSetting(), this.getCurrentValue()));
    }
    return null;
  }

  public Setting<T> getSetting() {
    return this.buttonSetting;
  }

  public void set(T e) {
    if (this.currentValue != e) {
      this.currentValue = e;
    }
  }

  public T getNextValue(boolean backwards) {
    return EnumCycler.rotateEnum(currentValue, backwards, validValues);
  }

  @Override
  public List<Component> getTooltipMessage() {

    if (this.buttonSetting == null || this.currentValue == null) {
      return Collections.emptyList();
    }

    var buttonAppearance = appearances.get(new EnumPair<>(this.buttonSetting, this.currentValue));
    if (buttonAppearance == null) {
      return Collections.singletonList(ButtonToolTips.NoSuchMessage.text());
    }

    return buttonAppearance.tooltipLines;
  }

  @Override
  protected Icon getIcon() {
    var app = getApperance();
    if (app != null && app.icon != null) {
      return app.icon;
    }
    return Icon.TOOLBAR_BUTTON_BACKGROUND;
  }

  @Override
  protected Item getItemOverlay() {
    var app = getApperance();
    if (app != null && app.item != null) {
      return app.item;
    }
    return null;
  }

  private static <T extends Enum<T>> void sendToServer(SettingToggleButton<T> button, boolean backwards) {
    ServerboundPacket message = new ConfigButtonPacket(button.getSetting(), backwards);
    PacketDistributor.sendToServer(message);
  }

  private static <T extends Enum<T>> void registerApp(Icon icon, Setting<T> setting, T val,
                                                      ButtonToolTips title, ButtonToolTips hint) {
    registerApp(icon, setting, val, title, hint.text());
  }

  private static <T extends Enum<T>> void registerApp(ItemLike item, Setting<T> setting, T val,
                                                      ButtonToolTips title, Component... tooltipLines) {
    var lines = new ArrayList<Component>();
    lines.add(title.text());
    Collections.addAll(lines, tooltipLines);

    appearances.put(
        new EnumPair<>(setting, val),
        new ButtonAppearance(null, item.asItem(), lines));
  }

  private static <T extends Enum<T>> void registerApp(Icon icon, Setting<T> setting, T val,
                                                      ButtonToolTips title, Component... tooltipLines) {
    var lines = new ArrayList<Component>();
    lines.add(title.text());
    Collections.addAll(lines, tooltipLines);

    appearances.put(
        new EnumPair<>(setting, val),
        new ButtonAppearance(icon, null, lines));
  }

  private record EnumPair<T extends Enum<T>>(Setting<T> setting, T value) {
    @Override
      public int hashCode() {
        return this.setting.hashCode() ^ this.value.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == null) {
          return false;
        }
        if (this.getClass() != obj.getClass()) {
          return false;
        }
        final EnumPair<?> other = (EnumPair<?>) obj;
        return other.setting == this.setting && other.value == this.value;
      }
    }

  private record ButtonAppearance(@Nullable Icon icon, @Nullable Item item, List<Component> tooltipLines) {
  }
}
