package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.blocksanditems.block.container.BlockMetaPair;
import roadhog360.hogutils.api.hogtags.containers.InheritorContainer;
import roadhog360.hogutils.api.hogtags.containers.TagContainerMeta;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableMeta;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class BlockTags extends TagContainerMeta<Block, BlockMetaPair> {

    public static final String CONTAINER_ID = "minecraft:blocks";

    public BlockTags(Block block) {
        super(REVERSE_LOOKUP_TABLE, INHERITOR_CONTAINER, block);
    }

    /// Adds the following tags to the specified block.
    public static void addTags(Block block, int meta, String... tags) {
        ((ITaggableMeta) block).addTags(meta, tags);
    }

    public static void addTags(Block block, String... tags) {
        addTags(block, OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Removes the following tags from the specified block.
    /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
    /// It may also be present in multiple lists in the inheritance tree.
    ///
    /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:blocks`.
    public static void removeTags(Block block, int meta, String... tags) {
        ((ITaggableMeta) block).removeTags(meta, tags);
    }

    public static void removeTags(Block block, String... tags) {
        removeTags(block, OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Get the tags for the passed in block.
    public static Set<String> getTags(Block block, int meta) {
        return ((ITaggableMeta) block).getTags(meta);
    }

    public static Set<String> getTags(Block block) {
        return getTags(block, OreDictionary.WILDCARD_VALUE);
    }

    public static boolean hasTag(@NonNull Block item, @NonNull String tag) {
        return hasTag(item, OreDictionary.WILDCARD_VALUE, tag);
    }

    /// Returns true if the passed in block has any of the listed tags.
    public static boolean hasTag(@NonNull Block block, int meta, @NonNull String tag) {
        return getTags(block, meta).contains(tag);
    }

    private static final Map<String, SetPair<BlockMetaPair>> REVERSE_LOOKUP_TABLE = new Object2ObjectOpenHashMap<>();
    private static final InheritorContainer<BlockMetaPair> INHERITOR_CONTAINER =
        new InheritorContainer<>(REVERSE_LOOKUP_TABLE, key -> getInTag((String) key));

    /// Get the blocks for the passed in tag via {@link BlockMetaPair} objects.
    ///
    /// {@link BlockMetaPair#get()} gets the {@link Block} object, whilst {@link BlockMetaPair#getMeta()} retrieves its metadata.
    /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
    /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
    ///
    /// If accessing the library via reflection and not via a compile dependency,
    /// a downcast with ({@link Set}<{@link Pair}<{@link Block}, {@link Integer}>) can safely be used.
    public static Set<BlockMetaPair> getInTag(String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(String inheritor, String... toInherit) {
        INHERITOR_CONTAINER.addInheritors(inheritor, toInherit);
    }

    public static void removeInheritors(String inheritor, String... toRemove) {
        INHERITOR_CONTAINER.removeInheritors(inheritor, toRemove);
    }

    public static Set<String> getInheritors(String tag) {
        return INHERITOR_CONTAINER.getInherited(tag);
    }
}
