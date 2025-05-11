package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.blocksanditems.item.container.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableBlockItem;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class ItemTags {
    public static final String CONTAINER_ID = "minecraft:items";

    /// Adds the following tags to the specified item.
    public static void addTags(Item item, int meta, String... tags) {
        ((ITaggableBlockItem<ItemMetaPair>) item).addTags(tags);
    }

    public static void addTags(Item item, String... tags) {
        addTags(item, OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Removes the following tags from the specified item.
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:items`.
    public static void removeTags(Item item, int meta, String... tags) {
        ((ITaggableBlockItem<ItemMetaPair>) item).removeTags(tags);
    }

    public static void removeTags(Item item, String... tags) {
        removeTags(item, OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Get the tags for the passed in item. You can pass in a Block's ItemBlock, too.
    /// (Typically obtained through {@link Item#getItemFromBlock(Block)})
    public static Set<String> getTags(Item item, int meta) {
        return ((ITaggableBlockItem<ItemMetaPair>) item).getTags(meta);
    }

    public static boolean hasTag(@NonNull Item item, @NonNull String tag) {
        return hasTag(item, OreDictionary.WILDCARD_VALUE, tag);
    }

    /// Returns true if the passed in item has any of the listed tags.
    public static boolean hasTag(@NonNull Item item, int meta, @NonNull String tag) {
        return getTags(item, meta).contains(tag);
    }

    @ApiStatus.Internal
    public static final Object2ObjectRBTreeMap<String, SetPair<String>> INHERITOR_TABLE = new Object2ObjectRBTreeMap<>();
    @ApiStatus.Internal
    public static final Object2ObjectAVLTreeMap<String, SetPair<ItemMetaPair>> REVERSE_LOOKUP_TABLE = new Object2ObjectAVLTreeMap<>();

    /// Get the items for the passed in tag via {@link ItemMetaPair} objects.
    ///
    /// {@link ItemMetaPair#get()} gets the {@link Item} object, whilst {@link ItemMetaPair#getMeta()} retrieves its metadata.
    /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
    /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
    public static Set<ItemMetaPair> getInTag(String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(String inheritor, String... toInherit) {
        for(String tag : ArrayUtils.add(toInherit, inheritor)) {
            for (ItemMetaPair pair : getInTag(tag)) {
                ((ITaggable<Item>) pair.get()).clearCaches();
            }
        }

        InheritorHelper.addInheritors(REVERSE_LOOKUP_TABLE, INHERITOR_TABLE, inheritor, toInherit);
    }

    public static void removeInheritors(String inheritor, String... toRemove) {
        for(String tag : ArrayUtils.add(toRemove, inheritor)) {
            for (ItemMetaPair pair : getInTag(tag)) {
                ((ITaggable<Item>) pair.get()).clearCaches();
            }
        }

        InheritorHelper.removeInheritors(REVERSE_LOOKUP_TABLE, INHERITOR_TABLE, inheritor, toRemove);
    }

    public static Set<String> getInheritors(String tag) {
        return INHERITOR_TABLE.get(tag).getLocked();
    }
}
