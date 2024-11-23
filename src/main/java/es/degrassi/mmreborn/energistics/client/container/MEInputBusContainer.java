package es.degrassi.mmreborn.energistics.client.container;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.SetStockAmountMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.FakeSlot;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEInputBusEntity;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MEInputBusContainer extends GridConnectedContainer<MEInputBusEntity> {
  private final MEHatchSize size;

  public static final String ACTION_OPEN_SET_AMOUNT = "setAmount";
  private final List<Slot> configSlots = new ArrayList<>();

  private static final int PAGE = 36;
  private static final int LINE = 18;

  public static final MenuType<MEInputBusContainer> TYPE = MenuTypeBuilder
      .create(MEInputBusContainer::new, MEInputBusEntity.class)
      .build(ModularMachineryRebornEnergistics.rl("input_bus"));
  public static final MenuType<MEInputBusContainer> ADVANCED_TYPE = MenuTypeBuilder
      .create(MEInputBusContainer::new, MEInputBusEntity.class)
      .build(ModularMachineryRebornEnergistics.rl("advanced_input_bus"));

  protected MEInputBusContainer(int containerId, Inventory inv, MEInputBusEntity entity) {
    super(entity, inv.player, entity.getSize().isAdvanced() ? ADVANCED_TYPE : TYPE, containerId);
    registerClientAction(ACTION_OPEN_SET_AMOUNT, Integer.class, this::openSetAmountMenu);
    this.size = entity.getSize();
    var config = entity.getConfig().createMenuWrapper();
    for (int x = 0; x < config.size(); x++) {
      int page = x / PAGE;
      int row = (x - page * PAGE) / LINE;
      this.configSlots.add(this.addSlot(new FakeSlot(config, x), MMRSemantics.INPUT_CONFIG_PATTERN[2 * page + row]));
    }
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
    for (int index = 0; index < (getSize().isAdvanced() ? 2 : 1); index ++) {
      var slots = this.getSlots(MMRSemantics.INPUT_CONFIG_PATTERN[index]);
      slots.addAll(this.getSlots(MMRSemantics.INPUT_STORAGE_PATTERN[index]));
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
