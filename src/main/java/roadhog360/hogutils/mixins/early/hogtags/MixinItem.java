package roadhog360.hogutils.mixins.early.hogtags;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import roadhog360.hogutils.api.blocksanditems.utils.MetaPackingUtils;
import roadhog360.hogutils.api.blocksanditems.utils.base.IUniqueIndex;

@Mixin(Item.class)
public class MixinItem implements IUniqueIndex {
    @Unique
    private static int hogutils$NEXT_UNIQUE_ID = -1;
    @Unique
    private final int hogutils$taggingHash = getNextID();

    private int getNextID() {
        MetaPackingUtils.registerItem(++hogutils$NEXT_UNIQUE_ID, (Item) (Object) this);
        return hogutils$NEXT_UNIQUE_ID;
    }

    @Override
    public int hogutils$getUniqueID() {
        return hogutils$taggingHash;
    }
}
