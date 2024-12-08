package roadhog360.hogutils.api.utils.blocksanditems.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;
import roadhog360.hogutils.api.utils.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.utils.blocksanditems.IItemAndBlockBase;

public abstract class BaseTrapdoor extends BlockTrapDoor implements IItemAndBlockBase {
    protected BaseTrapdoor(Material material, String name) {
        super(material);
        setHardness(3.0F);
        setBlockTextureName(name + "_door");
        setBlockName(name + "_door");
        setStepSound(getMaterial() == Material.iron ? Block.soundTypeMetal : Block.soundTypeWood);
    }

    @Override
    public String getUnlocalizedName() {
        return "tile." + BaseHelper.getUnlocalizedName(unlocalizedName, getNameDomain(unlocalizedName));
    }

    @Override
    protected String getTextureName() {
        return BaseHelper.getTextureName(textureName, getTextureDomain(textureName), getTextureSubfolder(textureName));
    }

    public BaseTrapdoor(String type) {
        this(Material.wood, type);
    }

    @Override
    public int getRenderType() {
        return BlockRenderers.TRAPDOOR.getRenderId();
    }
}
