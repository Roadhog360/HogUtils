package roadhog360.hogutils.api.utils.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.utils.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.utils.blocksanditems.IItemAndBlockBase;

public class BaseStairs extends BlockStairs implements IItemAndBlockBase {

    protected final int meta;

	public BaseStairs(Block p_i45428_1_, int p_i45428_2_) {
        super(p_i45428_1_, p_i45428_2_);
        useNeighborBrightness = true;
        meta = p_i45428_2_;
        unlocalizedName = p_i45428_1_.unlocalizedName;
        textureName = p_i45428_1_.textureName;
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        field_150149_b.registerBlockIcons(reg); //We do this in case the base block isn't actually registered
    }

    @Override
    public @Nullable String getTextureDomain(String textureName) {
        if(field_150149_b instanceof IItemAndBlockBase base) {
            return base.getTextureDomain(textureName);
        }
        return null;
    }

    @Override
    public @Nullable String getNameDomain(String unlocalizedName) {
        if(field_150149_b instanceof IItemAndBlockBase base) {
            return base.getNameDomain(textureName);
        }
        return null;
    }
}
