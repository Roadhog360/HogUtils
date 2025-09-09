package roadhog360.hogutils.api.hogtags.helpers;

import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.containers.InheritorContainer;
import roadhog360.hogutils.api.hogtags.containers.TagContainerMeta;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableMeta;
import roadhog360.hogutils.api.utils.RecipeHelper;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused"})
public final class ItemTags extends TagContainerMeta<Item, ItemMetaPair> {
    public static final String CONTAINER_ID = "minecraft:items";

    public ItemTags(Item item) {
        super(REVERSE_LOOKUP_TABLE, INHERITOR_CONTAINER, item);
    }

    /// Adds the following tags to the specified item.
    public static void addTags(@NonNull Item item, int meta, @NonNull String... tags) {
        if(tags.length == 0) {
            throw new IllegalArgumentException("Cannot add 0 tags to an item. Varargs brain fart? Tried to add 0 tags to " + item);
        }
        if(RecipeHelper.validateItems(item)) {
            ((ITaggableMeta) item).addTags(tags);
        }
    }

    public static void addTags(@NonNull Item item, @NonNull String... tags) {
        addTags(item, OreDictionary.WILDCARD_VALUE, tags);
    }

    public static void addTags(@NonNull ItemStack stack, @NonNull String... tags) {
        if(stack.getItem() == null) return;
        addTags(stack.getItem(), stack.getItemDamage(), tags);
    }

    public static void addTagsByID(@NonNull String modid, @NonNull String name, int meta, @NonNull String... tags) {
        Item item = GameRegistry.findItem(modid, name);
        if(item != null) {
            addTags(item, meta, tags);
        }
    }

    public static void addTagsByID(@NonNull String modid, @NonNull String name, @NonNull String... tags) {
        addTagsByID(modid, name, OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Removes the following tags from the specified item.
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:items`.
    public static void removeTags(@NonNull Item item, int meta, @NonNull String... tags) {
        if(tags.length == 0) {
            throw new IllegalArgumentException("Cannot remove 0 tags from an item. Varargs brain fart? Tried to remove 0 tags from " + item);
        }
        ((ITaggableMeta) item).removeTags(tags);
    }

    public static void removeTags(@NonNull Item item, @NonNull String... tags) {
        removeTags(item, OreDictionary.WILDCARD_VALUE, tags);
    }

    public static void removeTags(@NonNull ItemStack stack, @NonNull String... tags) {
        removeTags(Objects.requireNonNull(stack.getItem()), stack.getItemDamage(), tags);
    }

    /// Get the tags for the passed in item. You can pass in a Block's ItemBlock, too.
    /// (Typically obtained through {@link Item#getItemFromBlock(Block)})
    public static Set<String> getTags(@NonNull Item item, int meta) {
        return ((ITaggableMeta) item).getTags(meta);
    }

    public static boolean hasTag(@NonNull Item item, @NonNull String tag) {
        return hasTag(item, OreDictionary.WILDCARD_VALUE, tag);
    }

    /// Returns true if the passed in item has any of the listed tags.
    public static boolean hasTag(@NonNull Item item, int meta, @NonNull String tag) {
        return getTags(item, meta).contains(tag);
    }

    public static boolean hasTag(@NonNull ItemStack stack, @NonNull String tag) {
        return hasTag(Objects.requireNonNull(stack.getItem()), stack.getItemDamage(), tag);
    }

    private static final Map<String, SetPair<ItemMetaPair>> REVERSE_LOOKUP_TABLE = new Object2ObjectOpenHashMap<>();
    private static final InheritorContainer<ItemMetaPair> INHERITOR_CONTAINER =
        new InheritorContainer<>(REVERSE_LOOKUP_TABLE, key -> getInTag((String) key));

    /// Get the items for the passed in tag via {@link ItemMetaPair} objects.
    ///
    /// {@link ItemMetaPair#get()} gets the {@link Item} object, whilst {@link ItemMetaPair#getMeta()} retrieves its metadata.
    /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
    /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
    public static Set<ItemMetaPair> getInTag(String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(@NonNull String inheritor, @NonNull String... toInherit) {
        INHERITOR_CONTAINER.addInheritors(inheritor, toInherit);
    }

    public static void removeInheritors(String inheritor, @NonNull String... toRemove) {
        INHERITOR_CONTAINER.removeInheritors(inheritor, toRemove);
    }

    public static Set<String> getInheritors(@NonNull String tag) {
        return INHERITOR_CONTAINER.getInherited(tag);
    }
}
