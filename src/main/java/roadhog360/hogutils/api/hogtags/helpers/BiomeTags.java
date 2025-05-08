package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class BiomeTags {

    public static final String CONTAINER_ID = "minecraft:worldgen/biome";

    public static BiomeGenBase getBiomeFromID(int id) {
        if (id >= 0 && id < BiomeGenBase.getBiomeGenArray().length && BiomeGenBase.getBiomeGenArray()[id] != null) {
            return BiomeGenBase.getBiomeGenArray()[id];
        }
        throw new IllegalArgumentException(id + " is not a valid Biome ID!");
    }

    /// Adds the following tags to the specified biome.
    public static void addTags(BiomeGenBase biome, String... tags) {
        ((ITaggable<BiomeGenBase>) biome).addTags(tags);
    }

    /// Adds the following tags to the specified biome via its ID.
    public static void addTags(int id, String... tags) {
        addTags(getBiomeFromID(id));
    }

    /// Removes the following tags to the specified biome.
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:worldgen/biome`.
    public static void removeTags(BiomeGenBase biome, String... tags) {
        ((ITaggable<BiomeGenBase>) biome).removeTags(tags);
    }

    /// Removes the following tags from the specified biome via its ID
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:worldgen/biome`.
    public static void removeTags(int id, String... tags) {
        removeTags(getBiomeFromID(id), tags);
    }

    /// Get the tags for the passed in biome.
    public static Set<String> getTags(BiomeGenBase biome) {
        return ((ITaggable<BiomeGenBase>) biome).getTags();
    }

    /// Get the tags for the passed in biome via its ID.
    public static Set<String> getTags(int id) {
        return getTags(getBiomeFromID(id));
    }

    /// Returns true if the passed in biome has any of the listed tags.
    public static boolean hasTag(@NonNull BiomeGenBase biome, @NonNull String tag) {
        return getTags(biome).contains(tag);
    }

    @ApiStatus.Internal
    public static final Object2ObjectRBTreeMap<String, SetPair<String>> INHERITOR_TABLE = new Object2ObjectRBTreeMap<>();
    @ApiStatus.Internal
    public static final Object2ObjectAVLTreeMap<String, SetPair<BiomeGenBase>> REVERSE_LOOKUP_TABLE = new Object2ObjectAVLTreeMap<>();

    /// Get the {@link BiomeGenBase}s in this tag.
    public static Set<BiomeGenBase> getInTag(String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(String tag, String... inherits) {
        for(BiomeGenBase biome : getInTag(tag)) {
            ((ITaggable<BiomeGenBase>) biome).clearCaches();
        }
        for(String inhering : inherits) {
            for (BiomeGenBase biome : getInTag(inhering)) {
                ((ITaggable<Block>) biome).clearCaches();
            }
        }

        Set<BiomeGenBase> parentObjects = REVERSE_LOOKUP_TABLE.computeIfAbsent(tag, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getLocked();
        if (parentObjects != null) {
            for (String inheriting : inherits) {
                for (BiomeGenBase object : parentObjects) {
                    REVERSE_LOOKUP_TABLE.computeIfAbsent(inheriting, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked()
                        .add(object);
                }
            }
        }

        HogTags.addInheritors(tag, INHERITOR_TABLE, inherits);
    }

    public static void removeInheritors(String tag, String... inherits) {
        for(BiomeGenBase pair : getInTag(tag)) {
            ((ITaggable<BiomeGenBase>) pair).clearCaches();
        }

        Set<BiomeGenBase> parentObjects = REVERSE_LOOKUP_TABLE.get(tag).getLocked();
        if (parentObjects != null) {
            for (String inheriting : inherits) {
                for (BiomeGenBase object : parentObjects) {
                    SetPair<BiomeGenBase> tagSet = REVERSE_LOOKUP_TABLE.get(inheriting);
                    tagSet.getUnlocked().remove(object);
                    if(!tagSet.getUnlocked().isEmpty()) {
                        REVERSE_LOOKUP_TABLE.remove(inheriting);
                    }
                }
            }
        }

        HogTags.removeInheritors(tag, INHERITOR_TABLE, inherits);
    }

    public static Set<String> getInheritors(String tag) {
        return INHERITOR_TABLE.get(tag).getLocked();
    }
}
