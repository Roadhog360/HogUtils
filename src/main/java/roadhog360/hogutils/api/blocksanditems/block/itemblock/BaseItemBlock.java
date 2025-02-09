package roadhog360.hogutils.api.blocksanditems.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

public class BaseItemBlock extends ItemBlock {

    public BaseItemBlock(Block block) {
        super(block);
        if (!(block instanceof ISubtypesBlock)) {
            throw new IllegalArgumentException("BaseItemBlock instantiation needs to be given a block instance that implements ISubBlocksBlock!" +
                "Passed in " + block.getClass().getCanonicalName());
        }
        setHasSubtypes(((ISubtypesBlock) block).getTypes().size() > 1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if(!getHasSubtypes()) {
            return getUnlocalizedName();
        }
        return "tile." + ((ISubtypesBlock) field_150939_a/*blockInstance*/).getDisplayName(stack);
    }

    @Override
    public int getMetadata(int meta) {
        if(!getHasSubtypes()) {
            return super.getMetadata(meta);
        }
        return ((ISubtypesBlock) field_150939_a/*blockInstance*/).getTypes().containsKey(meta) ? meta : 0;
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return field_150939_a/*blockInstance*/.getIcon(2, p_77617_1_);
    }
}
