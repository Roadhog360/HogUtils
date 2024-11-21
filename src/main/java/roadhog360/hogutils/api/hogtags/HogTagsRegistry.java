package roadhog360.hogutils.api.hogtags;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.api.hogtags.mappings.BlockTagMapping;
import roadhog360.hogutils.api.hogtags.mappings.ItemTagMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// Internal backend for HogTags. Not intended to be called from.
/// This package is just to help keep its internal components private; Please use the HogTags class to call to this, don't reflect or mixin here.
/// If you think you can optimize this, or otherwise need a change, please submit a PR
public final class HogTagsRegistry {
    private HogTagsRegistry() {}

    private static final Map<String, TagContainer<?>> TAG_CONTAINERS = new Object2ObjectArrayMap<>();
    static {
        registerNewTagContainer(Tags.MOD_ID + ":block", new TagContainer<BlockTagMapping>(BlockTagMapping.class));
        registerNewTagContainer(Tags.MOD_ID + ":item", new TagContainer<ItemTagMapping>(ItemTagMapping.class));
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

    public static <T> void registerNewTagContainer(String containerID, TagContainer<T> container) {
        if(TAG_CONTAINERS.containsKey(containerID)) {
            throw new IllegalArgumentException("Tag container with ID " + containerID + " is already registered!");
        }
        if(TAG_CONTAINERS.containsValue(container)) {
            throw new IllegalArgumentException("Tag container " + container + " is already registered!");
        }
        TAG_CONTAINERS.put(checkContainerIDForModID(containerID), container);
    }

    @SuppressWarnings("unchecked")
    public static <T> TagContainer<T> getTagContainerForObject(T objToTag) {
        return (TagContainer<T>) getTagContainerFromID(getTagContainerIDFromObject(objToTag));
    }

    public static <T> String getTagContainerIDFromObject(T objToTag) {
        if(objToTag == null) {
            throw new RuntimeException("Null object cannot be tagged!");
        }

        for(Map.Entry<String, TagContainer<?>> entry : TAG_CONTAINERS.entrySet()) {
            if(entry.getValue().isValid(objToTag, false)) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Object of " + objToTag.getClass() + " currently not supported for tagging!");
    }

    public static TagContainer<?> getTagContainerFromID(String containerID) {
        containerID = checkContainerIDForModID(containerID);
        TagContainer<?> container = TAG_CONTAINERS.get(containerID);
        if(container != null) {
            return container;
        }
        throw new RuntimeException("Attempting to get tag container for ID that doesn't exist! Passed in ID " + containerID);
    }

    public static <E> void addTagsToObject(E objToTag, String... tags) {
        getTagContainerForObject(objToTag).putTags(objToTag, tags);
    }

    public static <E> void removeTagsFromObject(E objToUntag, String... tags) {
        getTagContainerForObject(objToUntag).removeTags(objToUntag, tags);
    }

    public static <E> List<String> getTagsFromObject(E taggedObj) {
        return getTagContainerForObject(taggedObj).getTags(taggedObj);
    }

    /// Only use this if you have registered your own custom taggable thing.
    /// Blocks/Items for example use this to get the metadata of the wildcard as well as the normal one.
    @SuppressWarnings("unchecked")
    public static <E> List<E> getObjectsForTagInContainer(String container, String tag) {
        return (List<E>) getTagContainerFromID(container).getObjectsInTag(tag);
    }

    public static class TagContainer<T> {
        protected final Class<?> typeToEnforce;

        protected final Map<T, TagPair<String>> TAGS_MAP = new Reference2ObjectArrayMap<>();
        protected final Map<String, TagPair<T>> REVERSE_LOOKUPS = new Object2ObjectArrayMap<>();

        private final Map<String, Set<String>> INHERITORS = new Object2ObjectArrayMap<>();

        public TagContainer(Class<?> typeToEnforce) {
            this.typeToEnforce = typeToEnforce;
        }

        private void putTags(T objToTag, String... tags) {
            isValid(objToTag, true);
            //Run tag filters
            HogTags.Utils.applyFiltersToTags(tags);

            //Add the tags to the object > tag list lookup
            Collections.addAll(TAGS_MAP.computeIfAbsent(objToTag, o -> new TagPair<>(new ObjectArrayList<>())).getUnlocked(), tags);

            invalidateCaches();
        }

        private void removeTags(T objToUntag, String... tags) {
            isValid(objToUntag, true);
            HogTags.Utils.applyFiltersToTags(tags);
            TAGS_MAP.get(objToUntag).getUnlocked().removeIf(s -> ArrayUtils.contains(tags, s));

            //TODO: Is it worth it to just remove the list entirety if it is emptied?

            invalidateCaches();
        }

        private List<String> getTags(T object) {
            TagPair<String> tags = TAGS_MAP.get(object);
            if(tags == null) {
                return ImmutableList.of();
            }
            return tags.getLocked();
        }

        private List<T> getObjectsInTag(String tag) {
            tag = HogTags.Utils.applyFiltersToTag(tag);
            if (!REVERSE_LOOKUPS.containsKey(tag)) {
                List<T> set = new ObjectArrayList<>();
                for (Map.Entry<T, TagPair<String>> entry : TAGS_MAP.entrySet()) {
                    List<String> tags = getTags(entry.getKey());
                    if (tags.contains(tag)) {
                        set.add(entry.getKey());
                    }
                }

                if(set.isEmpty()) {
                    REVERSE_LOOKUPS.put(tag, TagPair.getEmpty());
                    return ImmutableList.of();
                }
                REVERSE_LOOKUPS.put(tag, new TagPair<>(set));
                return set;
            }
            return REVERSE_LOOKUPS.get(tag).getLocked();
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
