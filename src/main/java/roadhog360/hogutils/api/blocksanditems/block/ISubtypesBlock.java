package roadhog360.hogutils.api.blocksanditems.block;

import net.minecraft.item.ItemStack;
import roadhog360.hogutils.api.blocksanditems.ISubtypesBase;

public interface ISubtypesBlock extends ISubtypesBase {
    /// Used for providing the block-specific name to the ItemBlock without creating a new ItemBlock for every specific name
    String getDisplayName(ItemStack stack);
}
