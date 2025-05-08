package roadhog360.hogutils.api.blocksanditems.block.container;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.ObjMetaPair;

import java.util.Map;

public class BlockMetaPair extends ObjMetaPair<Block> {
    public BlockMetaPair(Block obj, int meta) {
        super(obj, meta);
    }

    private static final Map<Block, Int2ObjectArrayMap<BlockMetaPair>> INTERNER = new Reference2ObjectArrayMap<>();

    @SuppressWarnings("unchecked")
    private synchronized static BlockMetaPair getOrCreateMapping(Block object, int meta) {
        return INTERNER.computeIfAbsent(object, o -> new Int2ObjectArrayMap<>())
            .computeIfAbsent(meta, o -> new BlockMetaPair(object, meta));
    }

    /// Creates or fetches a container object for the specified block, and metadata. Supports wildcard values.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in code that will be run a lot to avoid allocation spam.
    public synchronized static BlockMetaPair intern(Block object, int meta) {
        return getOrCreateMapping(object, meta);
    }

    /// Creates or fetches a new container object for the specified block, assuming the metadata is {@link OreDictionary#WILDCARD_VALUE}.
    /// The instances returned by this function are always the same object when the same arguments passed in.
    /// This is useful for reference-based code or using it in code that will be run a lot to avoid allocation spam.
    public synchronized static BlockMetaPair intern(Block object) {
        return intern(object, OreDictionary.WILDCARD_VALUE);
    }
}
