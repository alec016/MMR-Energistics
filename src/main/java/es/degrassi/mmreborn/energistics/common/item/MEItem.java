package es.degrassi.mmreborn.energistics.common.item;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.item.ItemBlockMachineComponent;
import es.degrassi.mmreborn.common.item.ItemHatch;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.DataComponentRegistration;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Getter
public class MEItem<C extends MachineComponent<X>, X> extends ItemBlockMachineComponent implements ItemHatch {
  private static final ResourceLocation BASE_TEXTURE = ModularMachineryReborn.rl("block/casing_plain");
  private final MEHatchSize size;

  public MEItem(MEBlock<C, X> block, MEHatchSize size) {
    super(
        block,
        new Properties()
            .component(DataComponentRegistration.BASE_TEXTURE, BASE_TEXTURE)
            .component(DataComponentRegistration.OVERLAY_TEXTURE, ModularMachineryRebornEnergistics.rl("block/overlay_" + size.getSerializedName() + "_active"))
            .component(DataComponentRegistration.DEFAULT_MODEL, ModularMachineryRebornEnergistics.rl("default/hatches/" + size.getSerializedName()))
    );
    this.size = size;
  }

  @Override
  public ResourceLocation getDefaultBaseTexture() {
    return BASE_TEXTURE;
  }

  @Override
  public ResourceLocation getDefaultOverlayTexture() {
    return ModularMachineryRebornEnergistics.rl("block/overlay_" + size.getSerializedName() + "_active");
  }
}
