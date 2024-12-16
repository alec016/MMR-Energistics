package es.degrassi.mmreborn.energistics.api.services.settings;

import appeng.api.config.AccessRestriction;
import appeng.api.config.CondenserOutput;
import appeng.api.config.CopyMode;
import appeng.api.config.CpuSelectionMode;
import appeng.api.config.FullnessMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.InscriberInputCapacity;
import appeng.api.config.LevelEmitterMode;
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
import com.google.common.base.Preconditions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class MMRESettings {
  private static final Map<String, Setting<?>> SETTINGS = new HashMap<>();


  @SafeVarargs
  private synchronized static <T extends Enum<T>> Setting<T> register(String name, T firstOption, T... moreOptions) {
    Preconditions.checkState(!SETTINGS.containsKey(name));
    var setting = new Setting<>(name, firstOption.getDeclaringClass(), EnumSet.of(firstOption, moreOptions));
    SETTINGS.put(name, setting);
    return setting;
  }

  private synchronized static <T extends Enum<T>> Setting<T> register(String name, Class<T> enumClass) {
    Preconditions.checkState(!SETTINGS.containsKey(name));
    var setting = new Setting<T>(name, enumClass);
    SETTINGS.put(name, setting);
    return setting;
  }

  public static final Setting<LockCraftingMode> LOCK_CRAFTING_MODE = register("mmre_lock_crafting_mode",
      LockCraftingMode.NONE, LockCraftingMode.LOCK_UNTIL_PULSE, LockCraftingMode.LOCK_WHILE_HIGH, LockCraftingMode.LOCK_WHILE_LOW);

  public static final Setting<LevelEmitterMode> LEVEL_EMITTER_MODE = register("level_emitter_mode",
      LevelEmitterMode.class);
  public static final Setting<RedstoneMode> REDSTONE_EMITTER = register("redstone_emitter", RedstoneMode.HIGH_SIGNAL,
      RedstoneMode.LOW_SIGNAL);
  public static final Setting<RedstoneMode> REDSTONE_CONTROLLED = register("redstone_controlled", RedstoneMode.class);
  public static final Setting<CondenserOutput> CONDENSER_OUTPUT = register("condenser_output", CondenserOutput.class);
  public static final Setting<PowerUnit> POWER_UNITS = register("power_units", PowerUnit.class);
  public static final Setting<AccessRestriction> ACCESS = register("access", AccessRestriction.READ_WRITE,
      AccessRestriction.READ, AccessRestriction.WRITE);
  public static final Setting<SortDir> SORT_DIRECTION = register("sort_direction", SortDir.class);
  public static final Setting<SortOrder> SORT_BY = register("sort_by", SortOrder.class);
  public static final Setting<YesNo> SEARCH_TOOLTIPS = register("search_tooltips", YesNo.YES, YesNo.NO);
  public static final Setting<ViewItems> VIEW_MODE = register("view_mode", ViewItems.class);
  public static final Setting<RelativeDirection> IO_DIRECTION = register("io_direction", RelativeDirection.LEFT,
      RelativeDirection.RIGHT);
  public static final Setting<YesNo> BLOCKING_MODE = register("blocking_mode", YesNo.YES, YesNo.NO);
  public static final Setting<OperationMode> OPERATION_MODE = register("operation_mode", OperationMode.class);
  public static final Setting<FullnessMode> FULLNESS_MODE = register("fullness_mode", FullnessMode.class);
  public static final Setting<YesNo> CRAFT_ONLY = register("craft_only", YesNo.YES, YesNo.NO);
  public static final Setting<FuzzyMode> FUZZY_MODE = register("fuzzy_mode", FuzzyMode.class);
  public static final Setting<TerminalStyle> TERMINAL_STYLE = register("terminal_style", TerminalStyle.SMALL,
      TerminalStyle.MEDIUM, TerminalStyle.TALL, TerminalStyle.FULL);
  public static final Setting<ShowPatternProviders> TERMINAL_SHOW_PATTERN_PROVIDERS = register(
      "show_pattern_providers", ShowPatternProviders.class);

  public static final Setting<CopyMode> COPY_MODE = register("copy_mode", CopyMode.class);
  public static final Setting<YesNo> PATTERN_ACCESS_TERMINAL = register("pattern_access_terminal", YesNo.YES,
      YesNo.NO);
  public static final Setting<YesNo> CRAFT_VIA_REDSTONE = register("craft_via_redstone", YesNo.YES, YesNo.NO);
  public static final Setting<StorageFilter> STORAGE_FILTER = register("storage_filter", StorageFilter.class);
  public static final Setting<YesNo> PLACE_BLOCK = register("place_block", YesNo.YES, YesNo.NO);
  public static final Setting<SchedulingMode> SCHEDULING_MODE = register("scheduling_mode", SchedulingMode.class);
  public static final Setting<YesNo> OVERLAY_MODE = register("overlay_mode", YesNo.YES, YesNo.NO);
  public static final Setting<YesNo> FILTER_ON_EXTRACT = register("filter_on_extract", YesNo.YES, YesNo.NO);
  public static final Setting<CpuSelectionMode> CPU_SELECTION_MODE = register("crafting_scheduling_mode",
      CpuSelectionMode.class);
  public static final Setting<YesNo> INSCRIBER_SEPARATE_SIDES = register("inscriber_separate_sides", YesNo.NO,
      YesNo.YES);
  public static final Setting<YesNo> AUTO_EXPORT = register("auto_export", YesNo.NO, YesNo.YES);

  @Deprecated(forRemoval = true)
  public static final Setting<YesNo> INSCRIBER_BUFFER_SIZE = register("inscriber_buffer_size", YesNo.NO, YesNo.YES);
  public static final Setting<InscriberInputCapacity> INSCRIBER_INPUT_CAPACITY = register("inscriber_input_capacity",
      InscriberInputCapacity.class);

  public static Setting<?> getOrThrow(String name) {
    var setting = SETTINGS.get(name);
    if (setting == null) {
      throw new IllegalArgumentException("Unknown setting '" + name + "'");
    }
    return setting;
  }
}
