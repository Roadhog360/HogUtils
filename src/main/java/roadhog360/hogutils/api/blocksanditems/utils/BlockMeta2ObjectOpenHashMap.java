package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.block.Block;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMeta2ObjectOpenHashMap;

public final class BlockMeta2ObjectOpenHashMap<V> extends ObjMeta2ObjectOpenHashMap<Block, V> {
    public BlockMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        super(wildcardFallback);
    }
}
