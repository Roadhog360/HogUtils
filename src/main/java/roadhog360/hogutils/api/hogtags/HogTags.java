package roadhog360.hogutils.api.hogtags;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.*;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/// Modern-esque tag system.
/// Uses [Fabric common tags standard](https://fabricmc.net/wiki/community:common_tags) as a standard rather than the vanilla or Forge standard tags.
/// New tags and containers added by HogUtils will have the "hogutils" domain.
///
/// Internal backend for HogTags. Not intended to be called from.
/// This package is just to help keep its internal components private; Please use the HogTagsHelper class to call to this, don't reflect or mixin here.
/// If you think you can optimize this, or otherwise need a change, please submit a PR
public final class HogTags {
    private HogTags() {}

    private static final BiMap<String, TagContainer<?>> TAG_CONTAINERS = HashBiMap.create(new Object2ObjectArrayMap<>());
    static {
        registerTagContainer(HogTagsHelper.BlockTags.CONTAINER_ID, new TagContainer<RegistryMapping<Block>>(){
            @Override
            protected Set<String> getExtraTags(RegistryMapping<Block> object) {
                if(object.getMeta() != OreDictionary.WILDCARD_VALUE) {
                    return getBaseTags(RegistryMapping.of(object.getObject(), OreDictionary.WILDCARD_VALUE));
                }
                return super.getExtraTags(object);
            }

            @Override
            public boolean isValid(RegistryMapping<Block> object) {
                return object.getObject() instanceof Block;
            }
        });

        registerTagContainer(HogTagsHelper.ItemTags.CONTAINER_ID, new TagContainer<RegistryMapping<Item>>(){
            @Override
            protected Set<String> getExtraTags(RegistryMapping<Item> object) {
                if(object.getMeta() != OreDictionary.WILDCARD_VALUE) {
                    return getBaseTags(RegistryMapping.of(object.getObject(), OreDictionary.WILDCARD_VALUE));
                }
                return super.getExtraTags(object);
            }

            @Override
            public boolean isValid(RegistryMapping<Item> object) {
                return object.getObject() instanceof Item;
            }
        });

        registerTagContainer(HogTagsHelper.BiomeTags.CONTAINER_ID, new TagContainer<BiomeGenBase>());
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
//            if(entry.getValue().isValid(objToTag)) {
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

    public static <E> Set<String> getTagsFromObject(String containerID, E taggedObj) {
        return getTagContainerFromID(containerID).getTags(taggedObj);
    }

    /// Only use this if you have registered your own custom taggable thing.
    /// Blocks/Items for example use this to get the metadata of the wildcard as well as the normal one.
    @SuppressWarnings("unchecked")
    public static <E> Set<E> getObjectsForTag(String containerID, String tag) {
        return (Set<E>) getTagContainerFromID(containerID).getObjectsInTag(tag);
    }

    public static void addInheritorsToTag(String containerID, String tag, String... inheritors) {
        getTagContainerFromID(containerID).addInheritors(tag, inheritors);
    }

    public static void removeInheritorsFromTag(String containerID, String tag, String... inheritors) {
        getTagContainerFromID(containerID).removeInheritors(tag, inheritors);
    }

    public static Set<String> getInheritors(String containerID, String tag) {
        return getTagContainerFromID(containerID).getInheritorsRecursive(tag);
    }

    public static class TagContainer<T> {
        protected final Map<T, Set<String>> BASE_TAGS_MAP = new Reference2ObjectArrayMap<>();

        protected final Map<T, Set<String>> LOOKUPS = new Reference2ObjectArrayMap<>();
        protected final Map<String, Set<T>> REVERSE_LOOKUPS = new Object2ObjectArrayMap<>();

        private final Map<String, SetPair<String>> INHERITORS = new Object2ObjectArrayMap<>();

        public void putTags(@NonNull T objToTag, String... tags) {
            if(!isValid(objToTag)) {
                throw new IllegalArgumentException("Blocked " + objToTag + " from being added to the tag container; object not valid for container!");
            }
            //Run tag filters
            HogTagsHelper.applyFiltersToTags(tags);

            //Add the tags to the object > tag list lookup
            Collections.addAll(BASE_TAGS_MAP.computeIfAbsent(objToTag, o -> new ObjectRBTreeSet<>()), tags);

            invalidateCaches();
        }

        public void removeTags(@NonNull T objToUntag, String... tags) {
            HogTagsHelper.applyFiltersToTags(tags);
            BASE_TAGS_MAP.get(objToUntag).removeIf(s -> ArrayUtils.contains(tags, s));

            //TODO: Is it worth it to just remove the list entirety if it is emptied?

            invalidateCaches();
        }

        private void addInheritors(String tag, String... inherits) {
            HogTagsHelper.applyFiltersToTags(inherits);
            tag = HogTagsHelper.applyFiltersToTag(tag);
            for(String inheritor : inherits) {
                INHERITORS.computeIfAbsent(inheritor, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked().add(tag);
            }

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

        private Set<String> getTags(T object) {
            Set<String> lookupResult = LOOKUPS.get(object);
            if(lookupResult != null) {
                return lookupResult;
            }

            Set<String> extraTags = getExtraTags(object);
            Set<String> baseTags = getBaseTags(object);
            if(!extraTags.isEmpty() || !baseTags.isEmpty()) {
                Set<String> finalTags = new ObjectAVLTreeSet<>();
                finalTags.addAll(baseTags);
                finalTags.addAll(extraTags);

                Set<String> inheritors = new ObjectAVLTreeSet<>();
                for(String tag : finalTags) {
                    inheritors.addAll(getInheritorsRecursive(tag));
                }
                finalTags.addAll(inheritors);

                LOOKUPS.put(object, Collections.unmodifiableSet(finalTags));
                return finalTags;
            }

            // I don't think we need to cache empty lists. Unlike reverse lookups this doesn't iterate over the entire registry.
            // Should be "fine"
//            LOOKUPS.put(object, ObjectImmutableList.of());
            return ImmutableSet.of();
        }

        public boolean isValid(T object) {
            return true;
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

        private Set<T> getObjectsInTag(String tag) {
            // TODO: This could probably be more efficient. It iterates over the entire tag map for every new tag passed in.
            // There is almost certainly a way to optimize this to just one pass where we bake the whole reverse lookup maps in one go.

            tag = HogTagsHelper.applyFiltersToTag(tag);
            Set<T> lookupResult = REVERSE_LOOKUPS.get(tag);
            if (lookupResult != null) {
                return lookupResult;
            }

            Set<T> set = new ObjectAVLTreeSet<>();
            for (Map.Entry<T, Set<String>> entry : BASE_TAGS_MAP.entrySet()) {
                Set<String> tags = getTags(entry.getKey());
                if (tags.contains(tag)) {
                    set.add(entry.getKey());
                }
            }

            if(set.isEmpty()) {
                // TODO: This could cause leakage if a mod checks for a lot of empty tags. The above rewrite would solve this.
                REVERSE_LOOKUPS.put(tag, ImmutableSet.of());
                return ImmutableSet.of();
            }
            REVERSE_LOOKUPS.put(tag, Collections.unmodifiableSet(set));
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

        /// Invalidates reverse lookup cache
        protected void invalidateCaches() {
            REVERSE_LOOKUPS.clear();
            LOOKUPS.clear();
        }

        protected final <E> Set<E> getLockedSet(SetPair<E> set) {
            return set.getLocked();
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
