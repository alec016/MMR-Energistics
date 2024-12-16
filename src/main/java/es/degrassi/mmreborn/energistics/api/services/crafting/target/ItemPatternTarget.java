package es.degrassi.mmreborn.energistics.api.services.crafting.target;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import es.degrassi.mmreborn.common.util.IOInventory;
import es.degrassi.mmreborn.common.util.ItemUtils;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Set;

@Getter
public class ItemPatternTarget implements PatternBusTarget {
  private final IItemHandlerModifiable inv;
  private final BlockPos pos;

  public ItemPatternTarget(IOInventory inv, BlockPos pos) {
    this.inv = inv;
    this.pos = pos;
  }

  public ItemPatternTarget(Level level, BlockPos pos) {
    this.inv = (IItemHandlerModifiable) level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
    this.pos = pos;
  }

  @Override
  public String getType() {
    return "item";
  }

  @Override
  public long insert(AEKey what, long amount, Actionable type) {
    if (!(what instanceof AEItemKey k)) return 0;
    return ItemUtils.tryPlaceItemInInventory(k.toStack((int) amount), inv, type.isSimulate());
  }

  @Override
  public boolean containsPatternInput(Set<AEKey> patternInputs) {
    return patternInputs.stream()
        .filter(AEItemKey::is)
        .map(AEItemKey.class::cast)
        .map(AEItemKey::getItem)
        .anyMatch(item -> {
          for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.is(item)) return true;
          }
          return false;
        });
  }
}
