package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.IItemAndBlockBase;
import roadhog360.hogutils.api.utils.RecipeHelper;

import javax.annotation.Nullable;

public class BaseStairsCopy extends BlockStairs implements IItemAndBlockBase {

	public BaseStairsCopy(Block p_i45428_1_, int p_i45428_2_) {
        super(p_i45428_1_, p_i45428_2_);
        useNeighborBrightness = true;
        copyStairInfo();
    }

    /// TODO: Version that takes a supplier for input so it can copy blocks registered after itself?
    /// TODO: Base stairs block that doesn't require an input block?

    protected void copyStairInfo() {
        var dummyStack = new ItemStack(field_150149_b, 1, field_150151_M);
        if(!RecipeHelper.validateItems(dummyStack)) {
            throw new IllegalArgumentException("Attempted to copy disabled block to stairs!");
        }
        if(unlocalizedName == null) { //name not already specified
            if (field_150149_b instanceof ISubtypesBlock subtypesBlock) {
                unlocalizedName = BaseHelper.depluralizeName(subtypesBlock.getTypes().get(field_150151_M))
                    .replace("_plank", "") + "_stairs";
            } else {
                unlocalizedName = BaseHelper.depluralizeName(
                    dummyStack.getUnlocalizedName().replace("tile.", "")).replace("_plank", "") + "_stairs";
            }
        }
        setCreativeTab(field_150149_b.displayOnCreativeTab);
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

    // FIXES TO META-SPECIFIC BUGS START
    // These bugs are caused by the game checking the meta of the stairs rotation instead of the base copied block

    public boolean canCollideCheck(int meta, boolean includeLiquid)
    {
        return this.field_150149_b.canCollideCheck(field_150151_M, includeLiquid);
    }
}
