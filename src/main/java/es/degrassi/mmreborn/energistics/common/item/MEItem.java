package es.degrassi.mmreborn.energistics.common.item;

import es.degrassi.mmreborn.common.item.ItemBlockMachineComponent;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import lombok.Getter;

@Getter
public class MEItem extends ItemBlockMachineComponent {
  private final MEHatchSize size;

  public MEItem(MEBlock block, MEHatchSize size) {
    super(block, new Properties());
    this.size = size;
  }
}
