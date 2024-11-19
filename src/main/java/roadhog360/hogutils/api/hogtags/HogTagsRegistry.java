package roadhog360.hogutils.api.hogtags;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.include.com.google.common.collect.Lists;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/// Internal backend for HogTags. Not intended to be called from.
/// This package is just to help keep its internal components private; Please use the HogTags class to call to this, don't reflect or mixin here.
/// If you think you can optimize this, or otherwise need a change, please submit a PR
public final class HogTagsRegistry {
    private HogTagsRegistry() {}

    private static final TagContainer<RegistryMapping<Block>> BLOCK_TAGS = new TagContainer<>();
    private static final TagContainer<RegistryMapping<Item>> ITEM_TAGS = new TagContainer<>();

//    @SuppressWarnings("rawtypes")
//    private static final Map<String, TagContainer> TAG_REGISTRIES = Maps.newHashMap();
//    static {
//        TAG_REGISTRIES.put("hogutils:blocks", new TagContainer<Block>());
//        TAG_REGISTRIES.put("hogutils:items", new TagContainer<Items>());
//    }

    //TODO add OreDict converter function here. Will be used by some "add to tags and oredict" option later
    //Hopefully this would allow people to add and get tags by the OreDictionary values.
    //Will be used by HogUtils itself on load complete to collect OreDict values and add them to tags
    //Should have a boolean arg, false for returning null or something, true would return "oredict:<passed in name>"

    @SuppressWarnings("rawtypes")
    private static <T> TagContainer getTagContainerForObject(T objToTag) {
        if(objToTag == null) {
            throw new RuntimeException("Null object cannot be tagged!");
        }

        if(objToTag instanceof RegistryMapping<?> mapping) {
            if(mapping.getObject() instanceof Block) {
                return BLOCK_TAGS;
            } else if (mapping.getObject() instanceof Item) {
                return ITEM_TAGS;
            } else {
                //Should never happen, this means someone fucked with my registry mapping objects in cursed ways
                throw new RuntimeException("whar");
            }
        }

        throw new RuntimeException("Object of type " + objToTag.getClass() + " currently not supported for tagging!");
    }

    @SuppressWarnings("unchecked")
    static <E> void addTagsToObject(E objToTag, String... tags) {
        getTagContainerForObject(objToTag).putTags(objToTag, tags);
    }

    @SuppressWarnings("unchecked")
    static <E> void removeTagsFromObject(E objToUntag, String... tags) {
        getTagContainerForObject(objToUntag).removeTags(objToUntag, tags);
    }

    @SuppressWarnings("unchecked")
    static <E> Set<String> getTagsFromObject(E taggedObj) {
        return getTagContainerForObject(taggedObj).getTags(taggedObj);
    }

    //Helper functions for getting object list from tags here.
    //Since we are only given a string we can't determine which list to pull from automatically, so we need to make a function for each list.
    //Tags are not unique and multiple registries (mainly item and block registries) can have the same tags in them.
    //So... we can't detect that way either.

    static Set<RegistryMapping<Block>> getBlocksInTag(String tag) {
        return BLOCK_TAGS.getObjectsInTag(tag);
    }

    static Set<RegistryMapping<Item>> getItemsInTag(String tag) {
        return ITEM_TAGS.getObjectsInTag(tag);
    }

    // These are to cache calls to the above, so that way you can put them in a block's constructor or preInit (not recommended) and they'd still work fine.
    static final List<Pair<Pair<Block, Integer>, String[]>> ITEMBLOCK_ADDITION_QUEUE = Lists.newArrayList();
    static final List<Pair<Pair<Block, Integer>, String[]>> ITEMBLOCK_REMOVAL_QUEUE = Lists.newArrayList();
    private static boolean queueExecuted = false;

    /// This is for HogUtils, if you're another mod this function is going to crash because it is NOT AN API!
    /// I just need this to be public...
    public static void tagQueuedItemBlocks() {
        if(Loader.instance().activeModContainer() != null && !Loader.instance().activeModContainer().getName().equals(Tags.MOD_NAME)) {
            // Only we should call this, other mods... NO TOUCHY! NOT AN API
            throw new RuntimeException(Loader.instance().activeModContainer().getName() +
                " called some functionality that they weren't supposed to! This is a bug in THEIR MOD, report it to THEM!");
        }
        if(queueExecuted) {
            throw new RuntimeException("Oops! The tagging system ran into an error! This is a bug in HogUtils, please report this!");
        }
        // Is it ugly? Yes. Is it unreadable AND cursed? Yes. Am I going to forget how to read this in a month? Probably.
        // Am I going to describe how to untagle this? Probably not, this function isn't an API anyways lmao
        ITEMBLOCK_ADDITION_QUEUE.forEach(pair -> {
            Item item = Item.getItemFromBlock(pair.getLeft().getLeft());
            if(item != null) { // If it's null, either no ItemBlock is paired with this block, or this guy is registered outside of preInit (which is unsupported)
                HogTags.ItemTags.addTags(item, pair.getLeft().getRight(), pair.getRight());
            }
        });
        ITEMBLOCK_REMOVAL_QUEUE.forEach(pair -> {
            Item item = Item.getItemFromBlock(pair.getLeft().getLeft());
            if(item != null) { // If it's null, either no ItemBlock is paired with this block, or this guy is registered outside of preInit (which is unsupported)
                HogTags.ItemTags.removeTags(item, pair.getLeft().getRight(), pair.getRight());
            }
        });
        queueExecuted = true;
    }


    public static class TagContainer<T> {
        private final Map<T, Set<String>> OBJECT_TO_TAGS = Maps.newHashMap();

        private final Map<String, Set<String>> INHERITORS = Maps.newHashMap();

        private void putTags(T objToTag, String... tags) {
            //Run tag filters
            HogTags.Utils.applyFiltersToTags(tags);
            for (int i = 0; i < tags.length; i++) {
                tags[i] = tags[i].intern();
            }


            //Add the tags to the object > tag list lookup
            Collections.addAll(OBJECT_TO_TAGS.computeIfAbsent(objToTag, o -> Sets.newLinkedHashSet()), tags);

            //I think inheritor logic would go here. Should just recursively call this method. MAKE SURE THERE'S RECURSION SANITY LOL

            invalidateReverseLookupCache();
        }

        private void removeTags(T objToUntag, String... tags) {
            HogTags.Utils.applyFiltersToTags(tags);
            OBJECT_TO_TAGS.get(objToUntag).removeIf(s -> ArrayUtils.contains(tags, s));

            //I think inheritor logic would go here. Should just recursively call this method. MAKE SURE THERE'S RECURSION SANITY LOL

            invalidateReverseLookupCache();
        }

        private Set<String> getTags(T object) {
            return OBJECT_TO_TAGS.getOrDefault(object, Collections.emptySet());
        }

        private void invalidateReverseLookupCache() {
            TAG_TO_OBJECTS.clear();
        }

        private final Map<String, Set<T>> TAG_TO_OBJECTS = Maps.newHashMap();

        private Set<T> getObjectsInTag(String tag) {
            tag = HogTags.Utils.applyFiltersToTag(tag);
            if(!TAG_TO_OBJECTS.containsKey(tag)) {
                Set<T> set = Sets.newLinkedHashSet();
                for(Map.Entry<T, Set<String>> entry : OBJECT_TO_TAGS.entrySet()) {
                    if(entry.getValue().contains(tag)) {
                        set.add(entry.getKey());
                    }
                }
                TAG_TO_OBJECTS.put(tag, set);
                return set;
            }
            return TAG_TO_OBJECTS.get(tag);
        }
    }
}
