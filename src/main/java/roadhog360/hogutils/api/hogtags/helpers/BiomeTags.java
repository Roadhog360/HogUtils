package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.NonNull;
import net.minecraft.world.biome.BiomeGenBase;
import roadhog360.hogutils.api.hogtags.containers.InheritorContainer;
import roadhog360.hogutils.api.hogtags.containers.TagContainerBasic;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class BiomeTags extends TagContainerBasic<BiomeGenBase> {

    public static final String CONTAINER_ID = "minecraft:worldgen/biome";

    public BiomeTags(@NonNull BiomeGenBase containerObject) {
        super(REVERSE_LOOKUP_TABLE, INHERITOR_CONTAINER, containerObject);
    }

    /// Adds the following tags to the specified biome.
    public static void addTags(@NonNull BiomeGenBase biome, @NonNull String... tags) {
        if(tags.length == 0) {
            throw new IllegalArgumentException("Cannot add 0 tags to a biome. Varargs brain fart? Tried to add 0 tags to " + biome);
        }
        ((ITaggable) biome).addTags(tags);
    }

    /// Adds the following tags to the specified biome via its ID.
    public static void addTags(int id, @NonNull String... tags) {
        addTags(GenericUtils.getBiomeFromID(id));
    }

    /// Removes the following tags to the specified biome.
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:worldgen/biome`.
    public static void removeTags(@NonNull BiomeGenBase biome, @NonNull String... tags) {
        if(tags.length == 0) {
            throw new IllegalArgumentException("Cannot remove 0 tags from a biome. Varargs brain fart? Tried to remove 0 tags from " + biome);
        }
        ((ITaggable) biome).removeTags(tags);
    }

    /// Removes the following tags from the specified biome via its ID
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:worldgen/biome`.
    public static void removeTags(int id, @NonNull String... tags) {
        removeTags(GenericUtils.getBiomeFromID(id), tags);
    }

    /// Get the tags for the passed in biome.
    public static Set<String> getTags(@NonNull BiomeGenBase biome) {
        return ((ITaggable) biome).getTags();
    }

    /// Get the tags for the passed in biome via its ID.
    public static Set<String> getTags(int id) {
        return getTags(GenericUtils.getBiomeFromID(id));
    }

    /// Returns true if the passed in biome has any of the listed tags.
    public static boolean hasTag(@NonNull BiomeGenBase biome, @NonNull String tag) {
        return getTags(biome).contains(tag);
    }

    private static final Map<String, SetPair<BiomeGenBase>> REVERSE_LOOKUP_TABLE = new Object2ObjectOpenHashMap<>();
    private static final InheritorContainer<BiomeGenBase> INHERITOR_CONTAINER =
        new InheritorContainer<>(REVERSE_LOOKUP_TABLE, key -> getInTag((String) key));

    /// Get the {@link BiomeGenBase}s in this tag.
    public static Set<BiomeGenBase> getInTag(@NonNull String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(@NonNull String inheritor, @NonNull String... toInherit) {
        INHERITOR_CONTAINER.addInheritors(inheritor, toInherit);
    }

    public static void removeInheritors(@NonNull String inheritor, @NonNull String... toRemove) {
        INHERITOR_CONTAINER.removeInheritors(inheritor, toRemove);
    }

    public static Set<String> getInheritors(@NonNull String tag) {
        return INHERITOR_CONTAINER.getInherited(tag);
    }
}
