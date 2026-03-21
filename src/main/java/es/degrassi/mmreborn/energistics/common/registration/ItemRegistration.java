package es.degrassi.mmreborn.energistics.common.registration;

import appeng.core.definitions.ItemDefinition;
import com.google.common.base.Preconditions;
import es.degrassi.mmreborn.energistics.ModularMachineryRebornEnergistics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ItemRegistration {
  public static final DeferredRegister.Items DR =
      DeferredRegister.createItems(ModularMachineryRebornEnergistics.MODID);
  public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

  static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
                                                 Function<Item.Properties, T> factory) {

    Item.Properties p = new Item.Properties();

    Preconditions.checkArgument(id.getNamespace().equals(ModularMachineryRebornEnergistics.MODID), "Can only register for MMRE");
    var definition = new ItemDefinition<>(name, DR.registerItem(id.getPath(), factory));

    ITEMS.add(definition);

    return definition;
  }

  public static void register(final IEventBus bus) {
    DR.register(bus);
  }
}
