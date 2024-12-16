package es.degrassi.mmreborn.energistics.client.container;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.api.services.crafting.PatternBusLogic;
import es.degrassi.mmreborn.energistics.api.services.settings.MMRESettings;
import es.degrassi.mmreborn.energistics.common.entity.base.MEPatternBus;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class MEPatternBusContainer extends GridConnectedContainer<MEPatternBus> {
  public static final MenuType<MEPatternBusContainer> TYPE = MenuTypeBuilder
      .create(MEPatternBusContainer::new, MEPatternBus.class)
      .build(ModularMachineryRebornEnergistics.rl("pattern_bus"));
  public static final MenuType<MEPatternBusContainer> ADVANCED_TYPE = MenuTypeBuilder
      .create(MEPatternBusContainer::new, MEPatternBus.class)
      .build(ModularMachineryRebornEnergistics.rl("advanced_pattern_bus"));

  protected final PatternBusLogic logic;

  @Getter
  @GuiSync(7)
  public YesNo blockingMode = YesNo.NO;
  @Getter
  @GuiSync(8)
  public YesNo showInAccessTerminal = YesNo.YES;
  @Getter
  @GuiSync(9)
  public LockCraftingMode lockCraftingMode = LockCraftingMode.NONE;
  @Getter
  @GuiSync(10)
  public LockCraftingMode craftingLockedReason = LockCraftingMode.NONE;
  @Getter
  @GuiSync(11)
  public GenericStack unlockStack = null;

  protected MEPatternBusContainer(int id, Inventory inv, MEPatternBus entity) {
    super(entity, inv.player, entity.getSize().isAdvanced() ? ADVANCED_TYPE : TYPE, id);
    this.logic = entity.getLogic();

    var patternInv = logic.getPatternInv();
    for (int x = 0; x < patternInv.size(); x++) {
      this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.PROVIDER_PATTERN, patternInv, x), SlotSemantics.ENCODED_PATTERN);
    }
  }

  @Override
  public void broadcastChanges() {
    if (isServerSide()) {
      blockingMode = logic.getConfigManager().getSetting(MMRESettings.BLOCKING_MODE);
      showInAccessTerminal = logic.getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL);
      lockCraftingMode = logic.getConfigManager().getSetting(MMRESettings.LOCK_CRAFTING_MODE);
      craftingLockedReason = logic.getCraftingLockedReason();
      unlockStack = logic.getUnlockStack();
    }

    super.broadcastChanges();
  }

  protected void loadSettingsFromHost(IConfigManager cm) {

  }
}
