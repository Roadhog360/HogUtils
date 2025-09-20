package roadhog360.hogutils.api.blocksanditems.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMetaPair;

import java.util.Map;

public class BlockMetaPair extends ObjMetaPair<Block> {
    public BlockMetaPair(Block obj, int meta) {
        super(obj, meta);
    }

    private static final Map<Block, Int2ObjectOpenHashMap<BlockMetaPair>> INTERNER = new Reference2ObjectOpenHashMap<>();

    @SuppressWarnings("unchecked")
    private synchronized static BlockMetaPair getOrCreateMapping(Block block, int meta) {
        return INTERNER.computeIfAbsent(block, o -> new Int2ObjectOpenHashMap<>())
            .computeIfAbsent(meta, o -> new BlockMetaPair(block, meta));
    }

    /// Creates or fetches a container object for the specified block, and metadata. Supports wildcard values.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in hot code paths to avoid allocation spam and reduce memory usage.
    public synchronized static BlockMetaPair intern(Block object, int meta) {
        return getOrCreateMapping(object, meta);
    }

    /// Creates or fetches a new container object for the specified block, assuming the metadata is {@link OreDictionary#WILDCARD_VALUE}.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in hot code paths to avoid allocation spam and reduce memory usage.
    public synchronized static BlockMetaPair intern(Block object) {
        return intern(object, OreDictionary.WILDCARD_VALUE);
    }

    public static BlockMetaPair of(Block block, int meta) {
        return new BlockMetaPair(block, meta);
    }
}
