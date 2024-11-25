package roadhog360.hogutils.api.hogtags;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.hogtags.mappings.BlockTagMapping;
import roadhog360.hogutils.api.hogtags.mappings.ItemTagMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// Modern-esque tag system.
/// Uses [Fabric common tags standard](https://fabricmc.net/wiki/community:common_tags) as a standard rather than the vanilla or Forge standard tags.
/// New tags and containers added by HogUtils will have the "hogutils" domain.
///
/// Internal backend for HogTags. Not intended to be called from.
/// This package is just to help keep its internal components private; Please use the HogTags class to call to this, don't reflect or mixin here.
/// If you think you can optimize this, or otherwise need a change, please submit a PR
public final class HogTags {
    private HogTags() {}

    private static final BiMap<String, TagContainer<?>> TAG_CONTAINERS = HashBiMap.create(new Object2ObjectArrayMap<>());
    static {
        registerTagContainer(HogTagsHelper.BlockTags.CONTAINER_ID, new TagContainer<BlockTagMapping>(BlockTagMapping.class){
            @Override
            protected Set<String> getExtraTags(BlockTagMapping object) {
                if(object.getMeta() != OreDictionary.WILDCARD_VALUE) {
                    return getBaseTags(BlockTagMapping.of(object.getObject(), OreDictionary.WILDCARD_VALUE));
                }
                return super.getExtraTags(object);
            }
        });

        registerTagContainer(HogTagsHelper.ItemTags.CONTAINER_ID, new TagContainer<ItemTagMapping>(ItemTagMapping.class){
            @Override
            protected Set<String> getExtraTags(ItemTagMapping object) {
                if(object.getMeta() != OreDictionary.WILDCARD_VALUE) {
                    return getBaseTags(ItemTagMapping.of(object.getObject(), OreDictionary.WILDCARD_VALUE));
                }
                return super.getExtraTags(object);
            }
        });

        registerTagContainer(HogTagsHelper.BiomeTags.CONTAINER_ID, new TagContainer<BiomeGenBase>(BiomeGenBase.class));
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

    public static <T> void registerTagContainer(String containerID, TagContainer<T> container) {
        if(TAG_CONTAINERS.containsKey(containerID)) {
            throw new IllegalArgumentException("Tag container with ID " + containerID + " is already registered!");
        }
        if(TAG_CONTAINERS.containsValue(container)) {
            throw new IllegalArgumentException("Tag container " + container + " is already registered! ");
        }
        TAG_CONTAINERS.put(checkContainerIDForModID(containerID), container);
    }

    // Might be useful in the future so keeping these commented for now
    // Fetching the container from ID is messy, so I'm gonna enforce specifying the ID.
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
    public static <E> List<E> getObjectsForTag(String containerID, String tag) {
        return (List<E>) getTagContainerFromID(containerID).getObjectsInTag(tag);
    }

    public static void addInheritorsToTag(String containerID, String tag, String... inherits) {
        getTagContainerFromID(containerID).addInheritors(tag, inherits);
    }

    public static void removeInheritorsFromTag(String containerID, String tag, String... inherits) {
        getTagContainerFromID(containerID).removeInheritors(tag, inherits);
    }

    public static Set<String> getInheritors(String containerID, String tag) {
        return getTagContainerFromID(containerID).getInheritorsRecursive(tag);
    }

    public static class TagContainer<T> {
        protected final Class<?> typeToEnforce;

        protected final Map<T, Set<String>> BASE_TAGS_MAP = new Reference2ObjectArrayMap<>();

        protected final Map<T, List<String>> LOOKUPS = new Reference2ObjectArrayMap<>();
        protected final Map<String, List<T>> REVERSE_LOOKUPS = new Object2ObjectArrayMap<>();

        private final Map<String, SetPair<String>> INHERITORS = new Object2ObjectArrayMap<>();

        public TagContainer(Class<?> typeToEnforce) {
            this.typeToEnforce = typeToEnforce;
        }

        public void putTags(T objToTag, String... tags) {
            isValid(objToTag, true);
            //Run tag filters
            HogTagsHelper.applyFiltersToTags(tags);

            //Add the tags to the object > tag list lookup
            Collections.addAll(BASE_TAGS_MAP.computeIfAbsent(objToTag, o -> new ObjectArraySet<>()), tags);

            invalidateCaches();
        }

        public void removeTags(T objToUntag, String... tags) {
            isValid(objToUntag, true);
            HogTagsHelper.applyFiltersToTags(tags);
            BASE_TAGS_MAP.get(objToUntag).removeIf(s -> ArrayUtils.contains(tags, s));

            //TODO: Is it worth it to just remove the list entirety if it is emptied?

            invalidateCaches();
        }

        private void addInheritors(String tag, String... inherits) {
            HogTagsHelper.applyFiltersToTags(inherits);
            tag = HogTagsHelper.applyFiltersToTag(tag);
            Collections.addAll(INHERITORS.computeIfAbsent(tag, o -> new SetPair<>(new ObjectArraySet<>())).getUnlocked(), inherits);

            doRecursionSanity();
            invalidateCaches();
        }

        private void doRecursionSanity() {
            // Checks every entry in the list to be sure there's no list that also contains the key
            // I feel like this probably needs work, something tells me this is not enough
            // Probably does not stop tags inheriting each other from different lists
            INHERITORS.forEach((tagToCheck, value) -> {
                if (value.getLocked().contains(tagToCheck)) {
                    throw new UnsupportedOperationException("Recursion in inheritors detected!");
                }
            });
        }

        private void removeInheritors(String tag, String... inherits) {
            HogTagsHelper.applyFiltersToTags(inherits);
            tag = HogTagsHelper.applyFiltersToTag(tag);

            INHERITORS.get(tag).getUnlocked().removeIf(s -> ArrayUtils.contains(inherits, s));

            invalidateCaches();
        }

        private Set<String> getBaseInheritors(String tag) {
            SetPair<String> tags = INHERITORS.get(tag);
            if(tags == null) {
                return ImmutableSet.of();
            }
            return tags.getLocked();
        }

        private Set<String> getInheritorsRecursive(String tag) {
            Set<String> tags = getBaseInheritors(tag);
            Set<String> inheritors = new ObjectArraySet<>();
            inheritors.addAll(tags);
            for(String tagToInherit : tags) {
                inheritors.addAll(getInheritorsRecursive(tagToInherit));
            }
            return new ObjectArraySet<>(inheritors);
        }

        private List<String> getTags(T object) {
            isValid(object, true);
            List<String> lookupResult = LOOKUPS.get(object);
            if(lookupResult != null) {
                return lookupResult;
            }

            Set<String> extraTags = getExtraTags(object);
            Set<String> baseTags = getBaseTags(object);
            if(!extraTags.isEmpty() || !baseTags.isEmpty()) {
                List<String> finalTags = new ObjectArrayList<>();
                finalTags.addAll(baseTags);
                finalTags.addAll(extraTags);

                List<String> inheritors = new ObjectArrayList<>();
                for(String tag : finalTags) {
                    inheritors.addAll(getInheritorsRecursive(tag));
                }
                finalTags.addAll(inheritors);

                LOOKUPS.put(object, new ObjectImmutableList<>(finalTags));
                return finalTags;
            }

            // I don't think we need to cache empty lists. Unlike reverse lookups this doesn't iterate over the entire registry.
            // Should be "fine"
//            LOOKUPS.put(object, ObjectImmutableList.of());
            return ObjectImmutableList.of();
        }

        /// Add extra tags to the list if desired. Return null if it's empty.
        ///
        /// Item and Block tags use this to fetch the wildcard tags that should apply to all items.
        protected Set<String> getExtraTags(T object) {
            return ImmutableSet.of();
        }

        /// Gets the tags ONLY for this object, and not any extras. Used for debugging and internal purposes, it returns the raw list.
        /// Returns an empty immutable list if there's no list associated with the object.
        protected final Set<String> getBaseTags(T object) {
            Set<String> tags = BASE_TAGS_MAP.get(object);
            if(tags == null) {
                return ImmutableSet.of();
            }
            return tags;
        }

        private List<T> getObjectsInTag(String tag) {
            // TODO: This could probably be more efficient. It iterates over the entire tag map for every new tag passed in.
            // There is almost certainly a way to optimize this to just one pass where we bake the whole reverse lookup maps in one go.

            tag = HogTagsHelper.applyFiltersToTag(tag);
            List<T> lookupResult = REVERSE_LOOKUPS.get(tag);
            if (lookupResult != null) {
                return lookupResult;
            }

            List<T> set = new ObjectArrayList<>();
            for (Map.Entry<T, Set<String>> entry : BASE_TAGS_MAP.entrySet()) {
                List<String> tags = getTags(entry.getKey());
                if (tags.contains(tag)) {
                    set.add(entry.getKey());
                }
            }

            if(set.isEmpty()) {
                // TODO: This could cause leakage if a mod checks for a lot of empty tags. The above rewrite would solve this.
                REVERSE_LOOKUPS.put(tag, ObjectImmutableList.of());
                return ObjectImmutableList.of();
            }
            REVERSE_LOOKUPS.put(tag, new ObjectImmutableList<>(set));
            return set;
        }

        private String getName() {
            return TAG_CONTAINERS.inverse().get(this);
        }

//        protected String printObjectNameForDebugging(T object) {
//            return object.toString();
//        }
//
//        @SneakyThrows
//        private void dumpTags() {
//            String name = TAG_CONTAINERS.inverse().get(this);
//            File dump = new File(Launch.minecraftHome, "dumps/tags_dump_" + name + ".json");
//            if(dump.createNewFile()) {
//                PrintWriter writer = new PrintWriter(dump, StandardCharsets.UTF_8);
//                StringBuilder builder = new StringBuilder("HogTags info for registry " + name + "\n\n");
//                for(val entrySet : TAGS_MAP.entrySet()) {
//                    builder.append("");
//                }
//                writer.print(builder.toString());
//                writer.close();
//            }
//        }

        /// Returns `TRUE` if the passed in object is a valid type for this TagsContainer, `FALSE` if otherwise.
        /// If the second arg is `TRUE`, the game will crash instead of returning false.
        ///
        /// The `TRUE` arg is used by put/remove to crash the game if the tag isn't valid.
        /// The `FALSE` arg is used when fetching a valid tag container for the object.
        ///
        /// Will always return `TRUE` if typeToEnforce is null.
        public boolean isValid(Object object, boolean enforce) {
            if(typeToEnforce == null) return true; //No type enforcement present

            if (typeToEnforce.isInstance(object)) {
                return true;
            }
            if (enforce) {
                throw new IllegalStateException("This object (" + object + ") isn't a valid type for this tag container! (Type must be of " + typeToEnforce + ")");
            }
            return false;
        }

        /// Invalidates reverse lookup cache
        protected void invalidateCaches() {
            REVERSE_LOOKUPS.clear();
            LOOKUPS.clear();
        }

        protected final <E> Set<E> getLockedSet(SetPair<E> set) {
            return set.getLocked();
        }

        @Override
        public String toString() {
            return "TagContainer{" + "typeToEnforce=" + typeToEnforce + '}';
        }

        protected static final class SetPair<E> extends Pair<Set<E>, Set<E>> {

            private final Set<E> unlocked, locked;

            public SetPair(Set<E> unlocked) {
                this.unlocked = unlocked;
                this.locked = Collections.unmodifiableSet(unlocked);
            }

            private Set<E> getUnlocked() {
                return unlocked;
            }

            @SuppressWarnings("getter")
            public Set<E> getLocked() {
                return locked;
            }

            @Override
            public Set<E> getLeft() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<E> getRight() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<E> getValue() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<E> setValue(final Set<E> value) {
                throw new UnsupportedOperationException();
            }

            private static final SetPair<?> EMPTY = new SetPair<>(ImmutableSet.of());

            @SuppressWarnings("unchecked")
            public static <E> SetPair<E> getEmpty() {
                return (SetPair<E>) EMPTY;
            }
        }
    }
}
