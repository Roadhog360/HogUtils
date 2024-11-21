package roadhog360.hogutils.api.hogtags;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.api.hogtags.mappings.BlockTagMapping;
import roadhog360.hogutils.api.hogtags.mappings.ItemTagMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/// Internal backend for HogTags. Not intended to be called from.
/// This package is just to help keep its internal components private; Please use the HogTags class to call to this, don't reflect or mixin here.
/// If you think you can optimize this, or otherwise need a change, please submit a PR
public final class HogTagsRegistry {
    private HogTagsRegistry() {}

    private static final Map<String, TagContainer<?>> TAG_CONTAINERS = new Object2ObjectArrayMap<>();
    static {
        registerNewTagContainer(Tags.MOD_ID + ":block", new TagContainer<BlockTagMapping>(BlockTagMapping.class){
            @Override
            protected List<String> getExtraTags(BlockTagMapping object) {
                if(object.getMeta() != OreDictionary.WILDCARD_VALUE) {
                    return getBaseTags(BlockTagMapping.of(object.getObject(), OreDictionary.WILDCARD_VALUE));
                }
                return super.getExtraTags(object);
            }
        });
        registerNewTagContainer(Tags.MOD_ID + ":item", new TagContainer<ItemTagMapping>(ItemTagMapping.class){
            @Override
            protected List<String> getExtraTags(ItemTagMapping object) {
                if(object.getMeta() != OreDictionary.WILDCARD_VALUE) {
                    return getBaseTags(ItemTagMapping.of(object.getObject(), OreDictionary.WILDCARD_VALUE));
                }
                return super.getExtraTags(object);
            }
        });
    }

    private static String checkContainerIDForModID(String containerID) {
        if(!containerID.contains(":")) {
            try {
                containerID = Loader.instance().activeModContainer().getModId() + ":" + containerID;
            } catch (
                Exception e) { //This could also happen if there's an error in the OreDictionary auto-tagging system, since that'd return an invalid mod container.
                throw new RuntimeException("Could not determine mod id for unprefixed tag container ID " + containerID + "!" +
                    "\nThis could be for several reasons, sometimes Forge's mod container fetcher just doesn't work, your code could be called from mixin'd vanilla code, etc..." +
                    "\nIt's good practice to just add a mod prefix to your tag container. Do that please...");
            }
        }
        return containerID;
    }

    @SuppressWarnings("unchecked")
    public static <E> TagContainer<E> getTagContainerFromID(String containerID) {
        containerID = checkContainerIDForModID(containerID);
        TagContainer<E> container = (TagContainer<E>) TAG_CONTAINERS.get(containerID);
        if(container != null) {
            return container;
        }
        throw new RuntimeException("Attempting to get tag container for ID that doesn't exist! Passed in ID " + containerID);
    }

    public static <T> void registerNewTagContainer(String containerID, TagContainer<T> container) {
        if(TAG_CONTAINERS.containsKey(containerID)) {
            throw new IllegalArgumentException("Tag container with ID " + containerID + " is already registered!");
        }
        if(TAG_CONTAINERS.containsValue(container)) {
            throw new IllegalArgumentException("Tag container " + container + " is already registered! ");
        }
        TAG_CONTAINERS.put(checkContainerIDForModID(containerID), container);
    }

    // Might be useful in the future so keeping these commented for now
    // Fetching the container from ID is messy so I'm gonna enforce specifying the ID.
//    @SuppressWarnings("unchecked")
//    public static <T> TagContainer<T> getTagContainerForObject(T objToTag) {
//        return (TagContainer<T>) getTagContainerFromID(getTagContainerIDFromObject(objToTag));
//    }
//
//    public static <T> String getTagContainerIDFromObject(T objToTag) {
//        if(objToTag == null) {
//            throw new RuntimeException("Null object cannot be tagged!");
//        }
//
//        for(Map.Entry<String, TagContainer<?>> entry : TAG_CONTAINERS.entrySet()) {
//            if(entry.getValue().isValid(objToTag, false)) {
//                return entry.getKey();
//            }
//        }
//
//        throw new RuntimeException("Object of " + objToTag.getClass() + " currently not supported for tagging!");
//    }

    public static <E> void addTagsToObject(String containerID, E objToTag, String... tags) {
        getTagContainerFromID(containerID).putTags(objToTag, tags);
    }

    public static <E> void removeTagsFromObject(String containerID, E objToUntag, String... tags) {
        getTagContainerFromID(containerID).removeTags(objToUntag, tags);
    }

    public static <E> List<String> getTagsFromObject(String containerID, E taggedObj) {
        return getTagContainerFromID(containerID).getTags(taggedObj);
    }

    /// Only use this if you have registered your own custom taggable thing.
    /// Blocks/Items for example use this to get the metadata of the wildcard as well as the normal one.
    @SuppressWarnings("unchecked")
    public static <E> List<E> getObjectsForTagInContainer(String containerID, String tag) {
        return (List<E>) getTagContainerFromID(containerID).getObjectsInTag(tag);
    }

    public static class TagContainer<T> {
        protected final Class<?> typeToEnforce;

        protected final Map<T, List<String>> TAGS_MAP = new Reference2ObjectArrayMap<>();

        protected final Map<T, List<String>> LOOKUPS = new Reference2ObjectArrayMap<>();
        protected final Map<String, List<T>> REVERSE_LOOKUPS = new Object2ObjectArrayMap<>();

        private final Map<String, TagPair<String>> INHERITORS = new Object2ObjectArrayMap<>();

        public TagContainer(Class<?> typeToEnforce) {
            this.typeToEnforce = typeToEnforce;
        }

        public void putTags(T objToTag, String... tags) {
            isValid(objToTag, true);
            //Run tag filters
            HogTags.Utils.applyFiltersToTags(tags);

            //Add the tags to the object > tag list lookup
            Collections.addAll(TAGS_MAP.computeIfAbsent(objToTag, o -> new ObjectArrayList<>()), tags);

            invalidateCaches();
        }

        public void removeTags(T objToUntag, String... tags) {
            isValid(objToUntag, true);
            HogTags.Utils.applyFiltersToTags(tags);
            TAGS_MAP.get(objToUntag).removeIf(s -> ArrayUtils.contains(tags, s));

            //TODO: Is it worth it to just remove the list entirety if it is emptied?

            invalidateCaches();
        }

        private List<String> getTags(T object) {
            List<String> lookupResult = LOOKUPS.get(object);
            if(lookupResult != null) {
                return lookupResult;
            }

            List<String> extraTags = getExtraTags(object);
            List<String> baseTags = getBaseTags(object);
            if(!extraTags.isEmpty() || !baseTags.isEmpty()) {
                List<String> set = new ObjectArrayList<>();
                set.addAll(baseTags);
                set.addAll(extraTags);
                LOOKUPS.put(object, new ObjectImmutableList<>(set));
                return set;
            }

            // I don't think we need to cache empty lists. Unlike reverse lookups this doesn't iterate over the entire registry.
            // Should be "fine"
//            LOOKUPS.put(object, ObjectImmutableList.of());
            return ObjectImmutableList.of();
        }

        /// Add extra tags to the list if desired. Return null if it's empty.
        ///
        /// Item and Block tags use this to fetch the wildcard tags that should apply to all items.
        protected List<String> getExtraTags(T object) {
            return ImmutableList.of();
        }

        /// Gets the tags ONLY for this object, and not any extras. Used for debugging and internal purposes, it returns the raw list.
        /// Returns an empty immutable list if there's no list associated with the object.
        protected final List<String> getBaseTags(T object) {
            List<String> tags = TAGS_MAP.get(object);
            if(tags == null) {
                return ImmutableList.of();
            }
            return tags;
        }

        private List<T> getObjectsInTag(String tag) {
            tag = HogTags.Utils.applyFiltersToTag(tag);
            List<T> lookupResult = REVERSE_LOOKUPS.get(tag);
            if (lookupResult != null) {
                return lookupResult;
            }

            List<T> set = new ObjectArrayList<>();
            for (Map.Entry<T, List<String>> entry : TAGS_MAP.entrySet()) {
                List<String> tags = getTags(entry.getKey());
                if (tags.contains(tag)) {
                    set.add(entry.getKey());
                }
            }

            if(set.isEmpty()) {
                REVERSE_LOOKUPS.put(tag, ObjectImmutableList.of());
                return ObjectImmutableList.of();
            }
            REVERSE_LOOKUPS.put(tag, new ObjectImmutableList<>(set));
            return set;
        }

        /// Returns `TRUE` if the passed in object is a valid type for this TagsContainer, `FALSE` if otherwise.
        /// If the second arg is `TRUE`, the game will crash instead of returning false.
        ///
        /// The `TRUE` arg is used by put/remove to crash the game if the tag isn't valid.
        /// The `FALSE` arg is used when fetching a valid tag container for the object.
        public boolean isValid(Object object, boolean enforce) {
            if (typeToEnforce.isInstance(object)) {
                return true;
            }
            if (enforce) {
                throw new IllegalArgumentException("This object (" + object + ") isn't a valid type for this tag container! (Type must be of " + typeToEnforce + ")");
            }
            return false;
        }

        /// Invalidates reverse lookup cache
        protected void invalidateCaches() {
            REVERSE_LOOKUPS.clear();
            LOOKUPS.clear();
        }

        protected final <E> List<E> getLockedSet(TagPair<E> set) {
            return set.getLocked();
        }

        @Override
        public String toString() {
            return "TagContainer{" + "typeToEnforce=" + typeToEnforce + '}';
        }

        protected static final class TagPair<E> extends Pair<List<E>, List<E>> {

            private final List<E> unlocked, locked;

            public TagPair(List<E> unlocked) {
                this.unlocked = unlocked;
                this.locked = Collections.unmodifiableList(unlocked);
            }

            private List<E> getUnlocked() {
                return unlocked;
            }

            public List<E> getLocked() {
                return locked;
            }

            @Override
            public List<E> getLeft() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<E> getRight() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<E> getValue() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<E> setValue(final List<E> value) {
                throw new UnsupportedOperationException();
            }

            private static final TagPair<?> EMPTY = new TagPair<>(ImmutableList.of());

            @SuppressWarnings("unchecked")
            public static <E> TagPair<E> getEmpty() {
                return (TagPair<E>) EMPTY;
            }
        }
    }
}
