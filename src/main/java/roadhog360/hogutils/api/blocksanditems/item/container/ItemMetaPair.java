package roadhog360.hogutils.api.blocksanditems.item.container;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.ObjMetaPair;

import java.util.Map;

public class ItemMetaPair extends ObjMetaPair<Item> {
    public ItemMetaPair(Item obj, int meta) {
        super(obj, meta);
    }


    private static final Map<Item, Int2ObjectArrayMap<ItemMetaPair>> INTERNER = new Reference2ObjectArrayMap<>();

    @SuppressWarnings("unchecked")
    private synchronized static ItemMetaPair getOrCreateMapping(Item object, int meta) {
        return INTERNER.computeIfAbsent(object, o -> new Int2ObjectArrayMap<>())
            .computeIfAbsent(meta, o -> new ItemMetaPair(object, meta));
    }

    /// Creates or fetches a container object for the specified item, and metadata. Supports wildcard values.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in code that will be run a lot to avoid allocation spam.
    public synchronized static ItemMetaPair intern(Item object, int meta) {
        return getOrCreateMapping(object, meta);
    }

    /// Creates or fetches a new container object for the specified item, assuming the metadata is {@link OreDictionary#WILDCARD_VALUE}.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in code that will be run a lot to avoid allocation spam.
    public synchronized static ItemMetaPair intern(Item object) {
        return intern(object, OreDictionary.WILDCARD_VALUE);
    }
}
