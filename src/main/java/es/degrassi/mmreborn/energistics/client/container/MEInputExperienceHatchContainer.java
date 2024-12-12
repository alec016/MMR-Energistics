package es.degrassi.mmreborn.energistics.client.container;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.SetStockAmountMenu;
import appeng.menu.slot.AppEngSlot;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputExperienceHatchEntity;
import es.degrassi.mmreborn.energistics.common.entity.MEInputSourceHatchEntity;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MEInputExperienceHatchContainer extends GridConnectedContainer<MEInputExperienceHatchEntity> {
  private final MEHatchSize size;

  public static final String ACTION_OPEN_SET_AMOUNT = "setAmount";
  private final List<Slot> configSlots = new ArrayList<>();

  private static final int PAGE = 36;
  private static final int LINE = 18;

  public static final MenuType<MEInputExperienceHatchContainer> TYPE = MenuTypeBuilder
      .create(MEInputExperienceHatchContainer::new, MEInputExperienceHatchEntity.class)
      .build(ModularMachineryRebornEnergistics.rl("input_experience_hatch"));
  public static final MenuType<MEInputExperienceHatchContainer> ADVANCED_TYPE = MenuTypeBuilder
      .create(MEInputExperienceHatchContainer::new, MEInputExperienceHatchEntity.class)
      .build(ModularMachineryRebornEnergistics.rl("advanced_input_experience_hatch"));

  protected MEInputExperienceHatchContainer(int containerId, Inventory inv, MEInputExperienceHatchEntity entity) {
    super(entity, inv.player, entity.getSize().isAdvanced() ? ADVANCED_TYPE : TYPE, containerId);
    registerClientAction(ACTION_OPEN_SET_AMOUNT, Integer.class, this::openSetAmountMenu);
    this.size = entity.getSize();
    var storage = entity.getStorage().createMenuWrapper();
    for (int x = 0; x < storage.size(); x++) {
      int page = x / PAGE;
      int row = (x - page * PAGE) / LINE;
      this.addSlot(new AppEngSlot(storage, x), MMRSemantics.INPUT_STORAGE_PATTERN[2 * page + row]);
    }
  }

  public void openSetAmountMenu(int configSlot) {
    if (isClientSide()) {
      sendClientAction(ACTION_OPEN_SET_AMOUNT, configSlot);
    } else {
      var stack = getHost().getConfig().getStack(configSlot);
      if (stack != null) {
        SetStockAmountMenu.open((ServerPlayer) getPlayer(), getLocator(), configSlot, stack.what(), (int) stack.amount());
      }
    }
  }

  public void showPage(int page) {
    for (int index = 0; index < (getSize().isAdvanced() ? 2 : 1); index++) {
      var slots = this.getSlots(MMRSemantics.INPUT_STORAGE_PATTERN[index]);
      for (var slot : slots) {
        if (slot instanceof AppEngSlot as) {
          as.setActive(page == 0);
        }
      }
    }
  }

  @Override
  protected void loadSettingsFromHost(IConfigManager cm) {
    this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
  }
}
