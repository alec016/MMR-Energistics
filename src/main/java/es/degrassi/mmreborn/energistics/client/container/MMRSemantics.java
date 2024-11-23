package es.degrassi.mmreborn.energistics.client.container;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

public interface MMRSemantics {
  SlotSemantic MMR_1 = SlotSemantics.register("MMR_1", false);
  SlotSemantic MMR_2 = SlotSemantics.register("MMR_2", false);
  SlotSemantic MMR_3 = SlotSemantics.register("MMR_3", false);
  SlotSemantic MMR_4 = SlotSemantics.register("MMR_4", false);

  SlotSemantic[] INPUT_CONFIG_PATTERN = new SlotSemantic[] { MMRSemantics.MMR_1, MMRSemantics.MMR_3 };
  SlotSemantic[] INPUT_STORAGE_PATTERN = new SlotSemantic[] { MMRSemantics.MMR_2, MMRSemantics.MMR_4 };
  SlotSemantic[] OUTPUT_STORAGE_PATTERN = new SlotSemantic[] { MMRSemantics.MMR_2, MMRSemantics.MMR_4 };
}
