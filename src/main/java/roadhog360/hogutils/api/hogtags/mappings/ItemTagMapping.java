package roadhog360.hogutils.api.hogtags.mappings;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.item.Item;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.List;
import java.util.Map;

/// RegistryMapping extended for specifically item tags, since the tags registry requires new object types.
public class ItemTagMapping extends RegistryMapping<Item> {
    private static final Map<Object, List<ItemTagMapping>> createdKeys = new Reference2ObjectArrayMap<>();

    private ItemTagMapping(Item object, int meta) {
        super(object, meta, false);
    }

    public static  ItemTagMapping of(Item object, int meta) {
        if (!(object instanceof Item)) {
            throw new IllegalArgumentException("ItemTagMapping must be an item!");
        }
        List<ItemTagMapping> bucket = createdKeys.get(object);
        if(bucket != null) {
            for(ItemTagMapping bucketItem : bucket) {
                // We already know the block is equal, just focus on the other stuff
                if(meta == bucketItem.getMeta()) {
                    return bucketItem;
                }
            }
        }

        ItemTagMapping mapping = new ItemTagMapping(object, meta);
        createdKeys.computeIfAbsent(object, o -> new ObjectArrayList<>()).add(mapping);
        return mapping;
    }
}
