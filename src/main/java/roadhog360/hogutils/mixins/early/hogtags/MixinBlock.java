package roadhog360.hogutils.mixins.early.hogtags;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import roadhog360.hogutils.api.blocksanditems.utils.MetaPackingUtils;
import roadhog360.hogutils.api.blocksanditems.utils.base.IUniqueIndex;

@Mixin(Block.class)
public class MixinBlock implements IUniqueIndex {
    @Unique
    private static int hogutils$NEXT_UNIQUE_ID = -1;
    @Unique
    private final int hogutils$taggingHash = getNextID();

    private int getNextID() {
        MetaPackingUtils.registerBlock(++hogutils$NEXT_UNIQUE_ID, (Block) (Object) this);
        return hogutils$NEXT_UNIQUE_ID;
    }

    @Override
    public int hogutils$getUniqueID() {
        return hogutils$taggingHash;
    }
}
