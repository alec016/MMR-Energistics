package es.degrassi.mmreborn.energistics.common.registration;

import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeTabsRegistration {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularMachineryRebornEnergistics.MODID);

  public static final Supplier<CreativeModeTab> MODULAR_MACHINERY_REBORN_TAB = CREATIVE_TABS.register(
      "modular_machinery_reborn_energistics", () -> CreativeModeTab.builder()
          .title(Component.translatable("itemGroup.modular_machinery_reborn_energistics.group"))
          .icon(ItemRegistration.ME_ADVANCED_INPUT_BUS.get()::getDefaultInstance)
          .displayItems((params, output) -> ItemRegistration.ITEMS.getEntries().forEach(entry -> output.accept(entry.get())))
          .build()
  );

  public static void register(final IEventBus bus) {
    CREATIVE_TABS.register(bus);
  }
}
