package es.degrassi.mmreborn.energistics.common.integration.jade;

import appeng.api.util.AEColor;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class MEEntityComponentProvider implements IBlockComponentProvider {

  public static final MEEntityComponentProvider INSTANCE = new MEEntityComponentProvider();
  public static final ResourceLocation ID = ModularMachineryRebornEnergistics.rl("me_entity_component_provider");

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    if (accessor.getBlockEntity() instanceof MEEntity) {
      CompoundTag nbt = accessor.getServerData().getCompound(ModularMachineryRebornEnergistics.MODID);
      if (nbt.isEmpty()) return;
      if (nbt.contains("booting", Tag.TAG_BYTE)) {
        tooltip.add(AEStatus.BOOTING.message());
        return;
      }
      if (nbt.contains("online", Tag.TAG_BYTE) && nbt.contains("hasChannel", Tag.TAG_BYTE)) {
        boolean online = nbt.getBoolean("online");
        boolean hasChannel = nbt.getBoolean("hasChannel");
        AEStatus status = AEStatus.getAEStatus(online, hasChannel);
        tooltip.add(status.message());
      }
      if (nbt.contains("color")) {
        AEColor color = AEColor.valueOf(nbt.getString("color"));
        tooltip.add(
            Component.translatable(
                "mmre.info.node.color",
                Component.translatable(color.translationKey).withColor(color.whiteVariant)
            )
        );
      }
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }
}
