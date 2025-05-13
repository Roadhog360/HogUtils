package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import lombok.NonNull;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class BiomeTags {

    public static final String CONTAINER_ID = "minecraft:worldgen/biome";

    /// Adds the following tags to the specified biome.
    public static void addTags(BiomeGenBase biome, String... tags) {
        ((ITaggable<BiomeGenBase>) biome).addTags(tags);
    }

    /// Adds the following tags to the specified biome via its ID.
    public static void addTags(int id, String... tags) {
        addTags(GenericUtils.getBiomeFromID(id));
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
        removeTags(GenericUtils.getBiomeFromID(id), tags);
    }

    /// Get the tags for the passed in biome.
    public static Set<String> getTags(BiomeGenBase biome) {
        return ((ITaggable<BiomeGenBase>) biome).getTags();
    }

    /// Get the tags for the passed in biome via its ID.
    public static Set<String> getTags(int id) {
        return getTags(GenericUtils.getBiomeFromID(id));
    }

    /// Returns true if the passed in biome has any of the listed tags.
    public static boolean hasTag(@NonNull BiomeGenBase biome, @NonNull String tag) {
        return getTags(biome).contains(tag);
    }

    @ApiStatus.Internal
    public static final Map<String, SetPair<String>> INHERITOR_TABLE = new Object2ObjectRBTreeMap<>();
    @ApiStatus.Internal
    public static final Map<String, SetPair<BiomeGenBase>> REVERSE_LOOKUP_TABLE = new Object2ObjectAVLTreeMap<>();

    /// Get the {@link BiomeGenBase}s in this tag.
    public static Set<BiomeGenBase> getInTag(String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(String inheritor, String... toInherit) {
        InheritorHelper.addInheritors(REVERSE_LOOKUP_TABLE, INHERITOR_TABLE, inheritor, toInherit);

        for(String tag : ArrayUtils.add(toInherit, inheritor)) {
            for (BiomeGenBase biome : getInTag(tag)) {
                ((ITaggable<BiomeGenBase>) biome).clearCaches();
            }
        }
    }

    public static void removeInheritors(String inheritor, String... toRemove) {
        for(String tag : ArrayUtils.add(toRemove, inheritor)) {
            for (BiomeGenBase biome : getInTag(tag)) {
                ((ITaggable<BiomeGenBase>) biome).clearCaches();
            }
        }

        InheritorHelper.removeInheritors(REVERSE_LOOKUP_TABLE, INHERITOR_TABLE, inheritor, toRemove);
    }

    public static Set<String> getInheritors(String tag) {
        return INHERITOR_TABLE.get(tag).getLocked();
    }
}
