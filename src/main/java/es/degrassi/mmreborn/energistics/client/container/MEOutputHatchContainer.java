package es.degrassi.mmreborn.energistics.client.container;

import appeng.api.util.IConfigManager;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.entity.MEOutputHatchEntity;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

@Getter
public class MEOutputHatchContainer extends GridConnectedContainer<MEOutputHatchEntity> {
  private final MEHatchSize size;

  private static final int PAGE = 18;
  private static final int LINE = 9;

  public static final MenuType<MEOutputHatchContainer> TYPE = MenuTypeBuilder
      .create(MEOutputHatchContainer::new, MEOutputHatchEntity.class)
      .build(ModularMachineryRebornEnergistics.rl("output_hatch"));
  public static final MenuType<MEOutputHatchContainer> ADVANCED_TYPE = MenuTypeBuilder
      .create(MEOutputHatchContainer::new, MEOutputHatchEntity.class)
      .build(ModularMachineryRebornEnergistics.rl("advanced_output_hatch"));

  protected MEOutputHatchContainer(int containerId, Inventory inv, MEOutputHatchEntity entity) {
    super(entity, inv.player, entity.getSize().isAdvanced() ? ADVANCED_TYPE : TYPE, containerId);
    this.size = entity.getSize();
    var storage = entity.getStorage().createMenuWrapper();
    for (int x = 0; x < storage.size(); x++) {
      int page = x / PAGE;
      int row = (x - page * PAGE) / LINE;
      this.addSlot(new AppEngSlot(storage, x), MMRSemantics.OUTPUT_STORAGE_PATTERN[2 * page + row]);
    }
  }

  public void showPage(int page) {
    for (int index = 0; index < (getSize().isAdvanced() ? 2 : 1); index ++) {
      var slots = this.getSlots(MMRSemantics.OUTPUT_STORAGE_PATTERN[index]);
      for (var slot : slots) {
        if (slot instanceof AppEngSlot as) {
          as.setActive(page == 0);
        }
      }
    }
  }

  @Override
  protected void loadSettingsFromHost(IConfigManager cm) {

  }
}
