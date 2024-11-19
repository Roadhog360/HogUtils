package roadhog360.hogutils.api.hogtags;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/// Internal backend for HogTags. Not intended to be called from.
/// This package is just to help keep its internal components private; Please use the HogTags class to call to this, don't reflect or mixin here.
/// If you think you can optimize this, or otherwise need a change, please submit a PR
public final class HogTagsRegistry {
    private HogTagsRegistry() {}

    private static final Map<String, TagContainer<?>> TAG_CONTAINER_MAP = new Object2ObjectArrayMap<>();
    static {
        TAG_CONTAINER_MAP.put(Tags.MOD_ID + ":block", new TagContainer<Block>());
        TAG_CONTAINER_MAP.put(Tags.MOD_ID + ":item", new TagContainer<Item>());
    }

    @SuppressWarnings("unchecked")
    public static <T> TagContainer<T> getTagContainerForObject(T objToTag) {
        return (TagContainer<T>) getTagContainerFromID(getTagContainerIDFromObject(objToTag));
    }

    public static String getTagContainerIDFromObject(Object objToTag) {
        if(objToTag == null) {
            throw new RuntimeException("Null object cannot be tagged!");
        }

        if(objToTag instanceof RegistryMapping<?> mapping) {
            if(mapping.getObject() instanceof Block) {
                return Tags.MOD_ID + ":block";
            } else if (mapping.getObject() instanceof Item) {
                return Tags.MOD_ID + ":item";
            } else {
                //Should never happen, this means someone fucked with my registry mapping objects in cursed ways
                throw new RuntimeException("whar");
            }
        }

        throw new RuntimeException("Object of type " + objToTag.getClass() + " currently not supported for tagging!");
    }

    public static TagContainer<?> getTagContainerFromID(String containerID) {
        TagContainer<?> container = TAG_CONTAINER_MAP.get(containerID);
        if(container != null) {
            return container;
        }
        throw new RuntimeException("Attempting to get tag container for ID that doesn't exist! Passed in ID " + containerID);
    }

    static <E> void addTagsToObject(E objToTag, String... tags) {
        getTagContainerForObject(objToTag).putTags(objToTag, tags);
    }

    static <E> void removeTagsFromObject(E objToUntag, String... tags) {
        getTagContainerForObject(objToUntag).removeTags(objToUntag, tags);
    }

    static <E> Set<String> getTagsFromObject(E taggedObj) {
        return getTagContainerForObject(taggedObj).getTags(taggedObj);
    }

    //Helper functions for getting object list from tags here.
    //Since we are only given a string we can't determine which list to pull from automatically, so we need to make a function for each list.
    //Tags are not unique and multiple registries (mainly item and block registries) can have the same tags in them.
    //So... we can't detect that way either.

    @SuppressWarnings("unchecked")
    static Set<RegistryMapping<Block>> getBlocksInTag(String tag) {
        return (Set<RegistryMapping<Block>>) getTagContainerFromID(Tags.MOD_ID + ":block").getObjectsInTag(tag);
    }

    @SuppressWarnings("unchecked")
    static Set<RegistryMapping<Item>> getItemsInTag(String tag) {
        return (Set<RegistryMapping<Item>>) getTagContainerFromID(Tags.MOD_ID + ":item").getObjectsInTag(tag);
    }

    public static class TagContainer<T> {
        private final Map<T, Set<String>> OBJECT_TO_TAGS = new Reference2ObjectArrayMap<>();

        private final Map<String, Set<String>> INHERITORS = new Object2ObjectArrayMap<>();

        private void putTags(T objToTag, String... tags) {
            //Run tag filters
            HogTags.Utils.applyFiltersToTags(tags);
            for (int i = 0; i < tags.length; i++) {
                tags[i] = tags[i].intern();
            }


            //Add the tags to the object > tag list lookup
            Collections.addAll(OBJECT_TO_TAGS.computeIfAbsent(objToTag, o -> new ObjectArraySet<>()), tags);

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

        private final Map<String, Set<T>> TAG_TO_OBJECTS = new Object2ObjectArrayMap<>();

        private Set<T> getObjectsInTag(String tag) {
            tag = HogTags.Utils.applyFiltersToTag(tag);
            if(!TAG_TO_OBJECTS.containsKey(tag)) {
                Set<T> set = new ObjectArraySet<>();
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
