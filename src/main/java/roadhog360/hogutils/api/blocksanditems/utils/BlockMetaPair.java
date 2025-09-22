package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMetaPair;

public class BlockMetaPair extends ObjMetaPair<Block> {
    public BlockMetaPair(Block obj, int meta) {
        super(obj, meta);
    }

    protected BlockMetaPair(Block obj, int meta, boolean interned) {
        super(obj, meta, interned);
    }

    /// NOTE: This is the only BlockMeta2ObjectOpenHashMap which absolutely CANNOT have any view functions for it called, that being:
    ///   - {@link BlockMeta2ObjectOpenHashMap#entrySet()}
    ///   - {@link BlockMeta2ObjectOpenHashMap#keySet()}
    ///   - {@link BlockMeta2ObjectOpenHashMap#values()}
    ///
    /// This will immediately produce a {@link StackOverflowError} upon attempting to use these views, because the views also use the interner.
    /// Whilst this is a private internal value and not an API call, this is more or less a note for myself.
    private static final BlockMeta2ObjectOpenHashMap<BlockMetaPair> INTERNER = new BlockMeta2ObjectOpenHashMap<>(false);

    /// Creates or fetches a container object for the specified block, and metadata. Supports wildcard values.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in hot code paths to avoid allocation spam and reduce memory usage.
    public synchronized static BlockMetaPair intern(Block object, int meta) {
        return INTERNER.computeIfAbsent(object, meta, (i, m) -> new BlockMetaPair(i, m, true));
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
