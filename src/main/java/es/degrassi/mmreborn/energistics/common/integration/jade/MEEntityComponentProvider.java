package es.degrassi.mmreborn.energistics.common.integration.jade;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import es.degrassi.mmreborn.energistics.common.entity.base.MEEntity;
import net.minecraft.ChatFormatting;
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
      if (nbt.contains("status", Tag.TAG_BYTE)) {
        boolean active = nbt.getByte("status") == 1;
        tooltip.add(
            Component
                .translatable("mmr.gui.me_network." + (active ? "online" : "offline"))
                .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED)
        );
      }
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ID;
  }
}
