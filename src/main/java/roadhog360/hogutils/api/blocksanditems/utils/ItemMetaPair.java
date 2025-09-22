package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMetaPair;

public class ItemMetaPair extends ObjMetaPair<Item> {
    public ItemMetaPair(Item obj, int meta) {
        super(obj, meta);
    }

    protected ItemMetaPair(Item obj, int meta, boolean interned) {
        super(obj, meta, interned);
    }

    /// NOTE: This is the only ItemMeta2ObjectOpenHashMap which absolutely CANNOT have any view functions for it called, that being:
    ///   - {@link ItemMeta2ObjectOpenHashMap#entrySet()}
    ///   - {@link ItemMeta2ObjectOpenHashMap#keySet()}
    ///   - {@link ItemMeta2ObjectOpenHashMap#values()}
    ///
    /// This will immediately produce a {@link StackOverflowError} upon attempting to use these views, because the views also use the interner.
    /// Whilst this is a private internal value and not an API call, this is more or less a note for myself.
    private static final ItemMeta2ObjectOpenHashMap<ItemMetaPair> INTERNER = new ItemMeta2ObjectOpenHashMap<>(false);

    /// Creates or fetches a container object for the specified item, and metadata. Supports wildcard values.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in hot to avoid allocation spam and reduce memory usage.
    public synchronized static ItemMetaPair intern(Item object, int meta) {
        return INTERNER.computeIfAbsent(object, meta, (i, m) -> new ItemMetaPair(i, m, true));
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
