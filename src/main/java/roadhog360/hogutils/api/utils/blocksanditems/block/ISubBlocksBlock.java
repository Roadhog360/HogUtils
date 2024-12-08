package roadhog360.hogutils.api.utils.blocksanditems.block;

import net.minecraft.item.ItemStack;
import roadhog360.hogutils.api.utils.blocksanditems.ISubtypesBase;

public interface ISubBlocksBlock extends ISubtypesBase {
    /// Used for providing the block-specific name to the ItemBlock without creating a new ItemBlock for every specific name
    String getNameFor(ItemStack stack);
}
