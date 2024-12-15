package es.degrassi.mmreborn.energistics.common.integration.jade;

import appeng.api.networking.IGridNode;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class MEEntityServerDataProvider implements IServerDataProvider<BlockAccessor> {
  public static final MEEntityServerDataProvider INSTANCE = new MEEntityServerDataProvider();
  public static final ResourceLocation ID = ModularMachineryRebornEnergistics.rl("me_entity_server_data_provider");
  @Override
  public void appendServerData(CompoundTag nbt, BlockAccessor accessor) {
    if (accessor.getBlockEntity() instanceof MEEntity machine && machine.getLevel() != null) {
      CompoundTag tag = new CompoundTag();
      IGridNode node = machine.getMainNode().getNode();
      if (!machine.getMainNode().hasGridBooted()) {
        tag.putBoolean("booting", true);
      } else if (node != null) {
        boolean online = node.isPowered();
        tag.putBoolean("online", online);
        boolean hasChannel = node.meetsChannelRequirements();
        tag.putBoolean("hasChannel", hasChannel);
      }
      if (machine.hasColor()) {
        tag.putString("color", machine.getGridColor().name());
      }
      nbt.put(ModularMachineryRebornEnergistics.MODID, tag);
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }
}
