package roadhog360.hogutils.api.blocksanditems.block.itemblock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import roadhog360.hogutils.api.blocksanditems.block.BaseLeaves;

public class BaseLeavesItemBlock extends BaseItemBlock {
    public BaseLeavesItemBlock(BaseLeaves p_i45344_1_) {
        super(p_i45344_1_);
    }

    public int getMetadata(int p_77647_1_)
    {
        return super.getMetadata(p_77647_1_ | 4);
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_)
    {
        return field_150939_a.getRenderColor(p_82790_1_.getItemDamage());
    }
}
