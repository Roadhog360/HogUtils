package roadhog360.hogutils.api.gui.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class OptionalSlot extends Slot {

    private boolean isAvailable;
    private final int xDisplayPos;
    private final int yDisplayPos;


    public OptionalSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        xDisplayPos = p_i1824_3_;
        yDisplayPos = p_i1824_4_;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Slot setAvailable(boolean available) {
        isAvailable = available;
        if(available) {
            xDisplayPosition = xDisplayPos;
            yDisplayPosition = yDisplayPos;
        } else {
            xDisplayPosition = Short.MIN_VALUE;
            yDisplayPosition = Short.MIN_VALUE;
        }
        return this;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(!isAvailable()) {
            return false;
        }
        return super.isItemValid(stack);
    }

    @Override
    public void putStack(ItemStack p_75215_1_) {
        if(!isAvailable()) {
            return;
        }
        super.putStack(p_75215_1_);
    }

    @Override
    public boolean isSlotInInventory(IInventory p_75217_1_, int p_75217_2_) {
        if(!isAvailable()) {
            return false;
        }
        return super.isSlotInInventory(p_75217_1_, p_75217_2_);
    }

    @Override
    public boolean getHasStack() {
        if(!isAvailable()) {
            return false;
        }
        return super.getHasStack();
    }
}
