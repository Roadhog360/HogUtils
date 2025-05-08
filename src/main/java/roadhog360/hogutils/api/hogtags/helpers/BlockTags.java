package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.blocksanditems.block.container.BlockMetaPair;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableBlockItem;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class BlockTags {
    public static final String CONTAINER_ID = "minecraft:blocks";

    /// Adds the following tags to the specified block.
    public static void addTags(Block block, int meta, String... tags) {
        ((ITaggableBlockItem<BlockMetaPair>) block).addTags(meta, tags);
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
        ((ITaggableBlockItem<BlockMetaPair>) block).removeTags(meta, tags);
    }

    public static void removeTags(Block block, String... tags) {
        removeTags(block, OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Get the tags for the passed in block.
    public static Set<String> getTags(Block block, int meta) {
        return ((ITaggableBlockItem<BlockMetaPair>) block).getTags(meta);
    }

    public static boolean hasTag(@NonNull Block item, @NonNull String tag) {
        return hasTag(item, OreDictionary.WILDCARD_VALUE, tag);
    }

    /// Returns true if the passed in block has any of the listed tags.
    public static boolean hasTag(@NonNull Block block, int meta, @NonNull String tag) {
        return getTags(block, meta).contains(tag);
    }

    @ApiStatus.Internal
    public static final Object2ObjectRBTreeMap<String, SetPair<String>> INHERITOR_TABLE = new Object2ObjectRBTreeMap<>();
    @ApiStatus.Internal
    public static final Object2ObjectAVLTreeMap<String, SetPair<BlockMetaPair>> REVERSE_LOOKUP_TABLE = new Object2ObjectAVLTreeMap<>();

    /// Get the blocks for the passed in tag via {@link BlockMetaPair} objects.
    ///
    /// {@link BlockMetaPair#get()} gets the {@link Block} object, whilst {@link BlockMetaPair#getMeta()} retrieves its metadata.
    /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
    /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
    public static Set<BlockMetaPair> getInTag(String tag) {
        return REVERSE_LOOKUP_TABLE.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    public static void addInheritors(String tag, String... inherits) {
        for(BlockMetaPair pair : getInTag(tag)) {
            ((ITaggable<Block>) pair.get()).clearCaches();
        }

        Set<BlockMetaPair> parentObjects = REVERSE_LOOKUP_TABLE.computeIfAbsent(tag, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getLocked();
        if (parentObjects != null) {
            for (String inheriting : inherits) {
                for (BlockMetaPair object : parentObjects) {
                    REVERSE_LOOKUP_TABLE.computeIfAbsent(inheriting, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked()
                        .add(object);
                }
            }
        }

        HogTags.addInheritors(tag, INHERITOR_TABLE, inherits);
    }

    public static void removeInheritors(String tag, String... inherits) {
        for(BlockMetaPair pair : getInTag(tag)) {
            ((ITaggable<Block>) pair.get()).clearCaches();
        }
        for(String inhering : inherits) {
            for (BlockMetaPair pair : getInTag(inhering)) {
                ((ITaggable<Block>) pair.get()).clearCaches();
            }
        }

        Set<BlockMetaPair> parentObjects = REVERSE_LOOKUP_TABLE.get(tag).getLocked();
        if (parentObjects != null) {
            for (String inheriting : inherits) {
                for (BlockMetaPair object : parentObjects) {
                    SetPair<BlockMetaPair> tagSet = REVERSE_LOOKUP_TABLE.get(inheriting);
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
