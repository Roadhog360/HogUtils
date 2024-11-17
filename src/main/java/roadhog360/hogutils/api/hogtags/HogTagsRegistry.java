package roadhog360.hogutils.api.hogtags;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.*;

/**
 * Internal backend for HogTags. Not intended to be called from.
 * This package is just to help keep its internal components private; Please use the HogTags class to call to this, don't reflect please.
 */
public final class HogTagsRegistry {
    private HogTagsRegistry() {}

    private static final TagContainer<RegistryMapping<Block>> BLOCK_TAGS = new TagContainer<>();
    private static final TagContainer<RegistryMapping<Item>> ITEM_TAGS = new TagContainer<>();

    //TODO add OreDict converter function here. Will be used by some "add to tags and oredict" option later
    //Hopefully this would allow people to add and get tags by the OreDictionary values.
    //Will be used by HogUtils itself on load complete to collect OreDict values and add them to tags
    //Should have a boolean arg, false for returning null or something, true would return "oredict:<passed in name>"

    @SuppressWarnings("rawtypes")
    private static <E> TagContainer getTagContainerForObject(E objToTag) {
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
        return (Set<String>) getTagContainerForObject(taggedObj).OBJECT_TO_TAGS.getOrDefault(taggedObj, Sets.newLinkedHashSet());
    }

    //Helper functions for getting object list from tags here.
    //Since we are only given a string we can't determine which list to pull from automatically, so we need to make a function for each list.
    //Tags are not unique and multiple registries (mainly item and block registries) can have the same tags in them.
    //So... we can't detect that way either.

    static Set<RegistryMapping<Block>> getBlocksInTag(String tag) {
        return BLOCK_TAGS.TAG_TO_OBJECTS.getOrDefault(tag, Sets.newLinkedHashSet());
    }

    static Set<RegistryMapping<Item>> getItemsInTag(String tag) {
        return ITEM_TAGS.TAG_TO_OBJECTS.getOrDefault(tag, Sets.newLinkedHashSet());
    }

//    @SuppressWarnings("unchecked")
//    static <E> Set<E> getObjectsForTag(E taggedObj) {
//        return (Set<String>) getTagContainerForObject(taggedObj).TAG_TO_OBJECTS.getOrDefault(taggedObj, EMPTY_SET);
//    }


    private static class TagContainer<E> {
        private final Map<E, Set<String>> OBJECT_TO_TAGS = Maps.newHashMap();
        private final Map<String, Set<E>> TAG_TO_OBJECTS = Maps.newHashMap();

        private final Map<String, Set<String>> INHERITORS = Maps.newHashMap();

        private void putTags(E objToTag, String... tags) {
            //Run tag filters
            HogTags.Utils.applyFiltersToTags(tags);
            Set<String> filteredTags = Sets.newLinkedHashSet(Lists.newArrayList(tags)); //Removes duplicate entries

            //Add the tags to the object > tag list lookup
            OBJECT_TO_TAGS.computeIfAbsent(objToTag, o -> Sets.newLinkedHashSet()).addAll(filteredTags);

            //Add the object to the tag > objects list lookup
            for(String tag : filteredTags) {
                if (TAG_TO_OBJECTS.containsKey(tag)) {
                    TAG_TO_OBJECTS.computeIfAbsent(tag, o -> Sets.newLinkedHashSet()).add(objToTag);
                }
            }

            //I think inheritor logic would go here. Should just recursively call this method. MAKE SURE THERE'S RECURSION SANITY LOL
        }

        private void removeTags(E objToUntag, String... tags) {
            HogTags.Utils.applyFiltersToTags(tags);
            Set<String> tagsInObject = OBJECT_TO_TAGS.get(objToUntag);
            for(String tag : tags) {
                tagsInObject.remove(tag);
                TAG_TO_OBJECTS.get(tag).remove(objToUntag);
            }

            //I think inheritor logic would go here. Should just recursively call this method. MAKE SURE THERE'S RECURSION SANITY LOL
        }
    }
}
