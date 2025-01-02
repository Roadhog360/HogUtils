package roadhog360.hogutils.api.blocksanditems.item.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import roadhog360.hogutils.api.blocksanditems.RegistryEntry;

public class RegistryEntryItem extends RegistryEntry<Item> {
    protected RegistryEntryItem(String name, boolean isEnabled, Item object) {
        super(name, isEnabled, object);
    }

    @Override
    protected void doRegistration() {
        GameRegistry.registerItem(object, name);
    }
}
