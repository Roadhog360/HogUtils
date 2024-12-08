package roadhog360.hogutils.api.utils.blocksanditems.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import roadhog360.hogutils.api.utils.blocksanditems.block.ISubBlocksBlock;

public class BaseSlabItemBlock extends ItemSlab {
    public BaseSlabItemBlock(Block block, BlockSlab singleSlab, BlockSlab doubleSlab, boolean p_i45355_4_) {
        super(block, singleSlab, doubleSlab, p_i45355_4_);
        if (!(block instanceof ISubBlocksBlock)) {
            throw new IllegalArgumentException("BaseSlabItemBlock instantiation needs to be given a block instance that implements ISubBlocksBlock!" +
                "Passed in " + block.getClass().getCanonicalName());
        }
        setHasSubtypes(((ISubBlocksBlock) block).getTypes().size() > 2);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if(!getHasSubtypes()) {
            return super.getUnlocalizedName();
        }
        return addTilePrefix(((ISubBlocksBlock) field_150939_a/*blockInstance*/).getNameFor(stack));
    }

    private String addTilePrefix(String name) {
        return name.startsWith("tile.") ? name : "tile." + ((ISubBlocksBlock) field_150939_a/*blockInstance*/).getNameDomain() + "." + name;
    }

    @Override
    public int getMetadata(int meta) {
        if(!getHasSubtypes()) {
            return super.getMetadata(meta);
        }
        return ((ISubBlocksBlock) field_150939_a/*blockInstance*/).getTypes().containsKey(meta) ? meta % 8 : 0;
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return field_150939_a/*blockInstance*/.getIcon(2, p_77617_1_);
    }
}
