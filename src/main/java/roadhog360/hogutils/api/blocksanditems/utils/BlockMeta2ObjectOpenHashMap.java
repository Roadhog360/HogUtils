package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMeta2ObjectOpenHashMap;

public final class BlockMeta2ObjectOpenHashMap<V> extends ObjMeta2ObjectOpenHashMap<Block, V> {
    /// @param wildcardFallback
    /// If searching for a metadata value that's not found, should we return the entry at {@link OreDictionary#WILDCARD_VALUE} if there is one?
    public BlockMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        super(wildcardFallback);
    }
}
