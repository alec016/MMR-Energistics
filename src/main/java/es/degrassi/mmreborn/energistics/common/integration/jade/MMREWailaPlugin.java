package es.degrassi.mmreborn.energistics.common.integration.jade;

import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import es.degrassi.mmreborn.energistics.common.registration.BlockRegistration;
import net.neoforged.neoforge.registries.DeferredHolder;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MMREWailaPlugin implements IWailaPlugin {

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(MEEntityComponentProvider.INSTANCE, MEBlock.class);
    BlockRegistration.BLOCKS.getEntries().stream().map(DeferredHolder::get).forEach(registration::usePickedResult);
  }

  @Override
  public void register(IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(MEEntityServerDataProvider.INSTANCE, MEEntity.class);
  }
}
