package roadhog360.hogutils.api.blocksanditems.item.container;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.ObjMetaPair;

import java.util.Map;

public class ItemMetaPair extends ObjMetaPair<Item> {
    public ItemMetaPair(Item obj, int meta) {
        super(obj, meta);
    }


    private static final Map<Item, Int2ObjectOpenHashMap<ItemMetaPair>> INTERNER = new Reference2ObjectOpenHashMap<>();
    private static ItemMetaPair LAST_INTERNED;

    @SuppressWarnings("unchecked")
    private synchronized static ItemMetaPair getOrCreateMapping(Item item, int meta) {
        if(LAST_INTERNED != null && item == LAST_INTERNED.get() && meta == LAST_INTERNED.getMeta()) {
            return LAST_INTERNED;
        }

        return LAST_INTERNED = INTERNER.computeIfAbsent(item, o -> new Int2ObjectOpenHashMap<>())
            .computeIfAbsent(meta, o -> new ItemMetaPair(item, meta));
    }

    /// Creates or fetches a container object for the specified item, and metadata. Supports wildcard values.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in hot to avoid allocation spam and reduce memory usage.
    public synchronized static ItemMetaPair intern(Item object, int meta) {
        return getOrCreateMapping(object, meta);
    }

    /// Creates or fetches a new container object for the specified item, assuming the metadata is {@link OreDictionary#WILDCARD_VALUE}.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in hot to avoid allocation spam and reduce memory usage.
    public synchronized static ItemMetaPair intern(Item object) {
        return intern(object, OreDictionary.WILDCARD_VALUE);
    }

    public static ItemMetaPair of(Item item, int meta) {
        return new ItemMetaPair(item, meta);
    }
}
