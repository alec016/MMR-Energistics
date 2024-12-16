package es.degrassi.mmreborn.energistics.common.entity.base;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import com.google.common.collect.ImmutableSet;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.item.ControllerItem;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.energistics.api.services.crafting.PatternBusLogic;
import es.degrassi.mmreborn.energistics.api.services.crafting.PatternBusLogicHost;
import es.degrassi.mmreborn.energistics.client.container.MEPatternBusContainer;
import es.degrassi.mmreborn.energistics.common.block.MEBlock;
import es.degrassi.mmreborn.energistics.common.block.prop.MEHatchSize;
import es.degrassi.mmreborn.energistics.common.network.server.SSyncControllerPosInComponent;
import es.degrassi.mmreborn.energistics.common.util.Mods;
import gripe._90.arseng.me.key.SourceKeyType;
import lombok.Getter;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class MEPatternBus extends MEEntity implements PatternBusLogicHost {
  protected final PatternBusLogic logic = createLogic();
  @Getter
  protected BlockPos controllerPos;

  public MEPatternBus(BlockPos pos, BlockState blockState, MEHatchSize size) {
    super(pos, blockState, size);
  }

  public void setControllerPos(BlockPos controllerPos) {
    if (!(getLevel().getBlockEntity(controllerPos) instanceof MachineControllerEntity)) return;
    this.controllerPos = controllerPos;
    if (!getLevel().isClientSide()) {
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
          new SSyncControllerPosInComponent(getBlockPos(), controllerPos));
    }
  }

  protected PatternBusLogic createLogic() {
    return new PatternBusLogic(this.getMainNode(), this, getSize().getSlots());
  }

  public void updateSubscription() {
    this.logic.onMainNodeStateChanged();
    if (shouldSubscribe()) {
      autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
    } else if (autoIOSubs != null) {
      autoIOSubs.unsubscribe();
      autoIOSubs = null;
    }
  }

  protected void onReady() {
    super.onReady();
    this.logic.updatePatterns();
  }

  @Override
  protected void syncME() {

  }

  @Override
  public InterfaceLogic getInterfaceLogic() {
    return null;
  }

  @Override
  public IConfigManager getConfigManager() {
    return logic.getConfigManager();
  }

  @Override
  public IUpgradeInventory getUpgrades() {
    return UpgradeInventories.empty();
  }

  @Override
  public int getPriority() {
    return logic.getPriority();
  }

  @Override
  public void setPriority(int newValue) {
    logic.setPriority(newValue);
  }

  @Override
  public GenericStackInv getConfig() {
    return null;
  }

  @Override
  public GenericStackInv getStorage() {
    ConfigInventory inv;
    ConfigInventory.Builder builder =
        ConfigInventory.storage(logic.getPatternInventory().size() + logic.getReturnInv().size())
        .allowOverstacking(true)
        .supportedTypes(AEKeyType.items(), AEKeyType.fluids());
    if (Mods.isAppExLoaded())
      builder.supportedType(ExperienceKeyType.TYPE);
    if (Mods.isAppMekLoaded())
      builder.supportedType(MekanismKeyType.TYPE);
    if (Mods.isAppArsLoaded())
      builder.supportedType(SourceKeyType.TYPE);
    inv = builder.build();

    AppEngInternalInventory internal = logic.getPatternInventory();
    for (int i = 0; i < internal.size(); i++) {
      ItemStack stack = internal.getStackInSlot(i);
      inv.insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, logic.getActionSource());
    }

    for (int i = 0; i < logic.getReturnInv().size(); i++) {
      GenericStack stack = logic.getReturnInv().getStack(i);
      if (stack == null) continue;
      inv.insert(stack.what(), stack.amount(), Actionable.MODULATE, logic.getActionSource());
    }

    return inv;
  }

  @Override
  public void openMenu(Player player, MenuHostLocator locator) {
    if (!getLevel().isClientSide()) {
      if (getSize().isAdvanced())
        MenuOpener.open(MEPatternBusContainer.ADVANCED_TYPE, player, locator);
      else
        MenuOpener.open(MEPatternBusContainer.TYPE, player, locator);
    }
  }

  @Override
  public void returnToMainMenu(Player player, ISubMenu subMenu) {
    if (getSize().isAdvanced())
      MenuOpener.open(MEPatternBusContainer.ADVANCED_TYPE, player, subMenu.getLocator());
    else
      MenuOpener.open(MEPatternBusContainer.TYPE, player, subMenu.getLocator());
  }

  @Override
  public PatternBusLogic getLogic() {
    return logic;
  }

  @Override
  public EnumSet<Direction> getTargets() {
    return EnumSet.allOf(Direction.class);
  }

  public void addDrops(List<ItemStack> list) {
    logic.addDrops(list);
  }

  @Override
  public AEItemKey getTerminalIcon() {
    if (getController() == null) return AEItemKey.of(((MEBlock) getBlockState().getBlock()).item());
    return AEItemKey.of(ControllerItem.makeMachineItem(getController().getId()));
  }

  @Override
  public ImmutableSet<ICraftingLink> getRequestedJobs() {
    return ImmutableSet.of();
  }

  @Override
  public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
    return 0;
  }

  @Override
  public void jobStateChange(ICraftingLink link) {

  }

  @Override
  public @Nullable MachineComponent<?> provideComponent() {
    return null;
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    this.logic.writeToNBT(nbt, pRegistries);
    if (controllerPos != null)
      nbt.putLong("controllerPos", controllerPos.asLong());
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    this.logic.readFromNBT(nbt, pRegistries);
    if (nbt.contains("controllerPos"))
      controllerPos = BlockPos.of(nbt.getLong("controllerPos"));
  }
}
