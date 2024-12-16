package es.degrassi.mmreborn.energistics.api.services.crafting;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.helpers.IPriorityHost;
import appeng.helpers.patternprovider.PatternContainer;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface PatternBusLogicHost extends IConfigurableObject, IPriorityHost, PatternContainer, ControllerAccessible {
  PatternBusLogic getLogic();

  /**
   * @return The block entity that is in-world and hosts the interface.
   */
  BlockEntity getBlockEntity();

  EnumSet<Direction> getTargets();

  void saveChanges();

  @Override
  default IConfigManager getConfigManager() {
    return getLogic().getConfigManager();
  }

  @Override
  default int getPriority() {
    return getLogic().getPriority();
  }

  @Override
  default void setPriority(int newValue) {
    getLogic().setPriority(newValue);
  }

  @Override
  default @Nullable IGrid getGrid() {
    return getLogic().getGrid();
  }

  AEItemKey getTerminalIcon();

  @Override
  default boolean isVisibleInTerminal() {
    return getLogic().getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL) == YesNo.YES;
  }

  @Override
  default InternalInventory getTerminalPatternInventory() {
    return getLogic().getPatternInv();
  }

  @Override
  default long getTerminalSortOrder() {
    return getLogic().getSortValue();
  }

  default PatternContainerGroup getTerminalGroup() {
    return getLogic().getTerminalGroup();
  }
}
