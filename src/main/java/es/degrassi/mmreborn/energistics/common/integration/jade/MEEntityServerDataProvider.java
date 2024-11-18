package es.degrassi.mmreborn.energistics.common.integration.jade;

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
      tag.putBoolean("status", machine.isOnline());
      nbt.put(ModularMachineryRebornEnergistics.MODID, tag);
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }
}
