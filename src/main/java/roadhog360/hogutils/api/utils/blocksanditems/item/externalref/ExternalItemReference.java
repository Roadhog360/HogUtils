package roadhog360.hogutils.api.utils.blocksanditems.item.externalref;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import roadhog360.hogutils.api.utils.blocksanditems.ExternalReference;

import java.util.function.Supplier;

public final class ExternalItemReference extends ExternalReference<Item> {
    public ExternalItemReference(String modID, String namespace) {
        super(modID, namespace);
    }

    public ExternalItemReference(String itemID) {
        super(itemID);
    }

    @Override
    protected Supplier<Item> getSupplier() {
        return () -> GameRegistry.findItem(modID, namespace);
    }
}
