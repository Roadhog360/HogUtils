package roadhog360.hogutils.api.hogtags.mappings;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.List;
import java.util.Map;

public class BlockTagMapping extends RegistryMapping<Block> {
    private static final Map<Object, List<BlockTagMapping>> createdKeys = new Reference2ObjectArrayMap<>();

    private BlockTagMapping(Block object, int meta) {
        super(object, meta, false);
    }

    public static  BlockTagMapping of(Block object, int meta) {
        if (!(object instanceof Block)) {
            throw new IllegalArgumentException("BlockTagMapping must be a block!");
        }
        List<BlockTagMapping> bucket = createdKeys.get(object);
        if(bucket != null) {
            for(BlockTagMapping bucketItem : bucket) {
                // We already know the block is equal, just focus on the other stuff
                if(meta == bucketItem.getMeta()) {
                    return bucketItem;
                }
            }
        }

        BlockTagMapping mapping = new BlockTagMapping(object, meta);
        createdKeys.computeIfAbsent(object, o -> new ObjectArrayList<>()).add(mapping);
        return mapping;
    }
}
