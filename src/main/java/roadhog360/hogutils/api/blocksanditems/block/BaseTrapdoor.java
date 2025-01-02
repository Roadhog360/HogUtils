package roadhog360.hogutils.api.blocksanditems.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.IItemAndBlockBase;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;

public abstract class BaseTrapdoor extends BlockTrapDoor implements IItemAndBlockBase {
    protected BaseTrapdoor(Material material, String name) {
        super(material);
        setHardness(3.0F);
        setBlockTextureName(name + "_door");
        setBlockName(name + "_door");
        setStepSound(getMaterial() == Material.iron ? Block.soundTypeMetal : Block.soundTypeWood);
    }

    public BaseTrapdoor(String type) {
        this(Material.wood, type);
    }

    @Override
    public String getUnlocalizedName() {
        return "tile." + BaseHelper.getUnlocalizedName(unlocalizedName, getNameDomain(unlocalizedName));
    }

    @Override
    protected String getTextureName() {
        return BaseHelper.getTextureName(textureName, getTextureDomain(textureName), getTextureSubfolder(textureName));
    }

    @Override
    public int getRenderType() {
        return BlockRenderers.TRAPDOOR.getRenderId();
    }

    /// Should this trapdoor rotate its faces depending on which way it's rotated when placed?
    public boolean hasRotatedRendering(IBlockAccess world, int x, int y, int z) {
        return true;
    }
}
