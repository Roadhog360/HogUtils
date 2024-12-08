package roadhog360.hogutils.api.utils.blocksanditems;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


/// Should only pass in {@link Block} or {@link Item} as the type
public interface IReferenceBase<T> {
    default ItemStack newItemStack() {
        return newItemStack(1);
    }

    default ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    /// Returns null if the block is not registered or not available
    default ItemStack newItemStack(int count, int meta) {
        // This looks kinda stupid, hopefully there is a better way to do this?
        if(get() instanceof Block block) {
            return new ItemStack(block, count, meta);
        }
        if(get() instanceof Item item) {
            return new ItemStack(item, count, meta);
        }
        throw new IllegalStateException("This error is being reached because some mod messed with a HogUtils class using reflection, or implemented it incorrectly!");
    }

    /// Get the block/item in this reference container.
    T get();

    boolean isEnabled();
}
