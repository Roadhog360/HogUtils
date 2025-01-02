package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.IItemAndBlockBase;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;

import java.util.Random;

public abstract class BaseDoor extends BlockDoor implements IItemAndBlockBase {

    public BaseDoor(Material material, String name) {
        super(material);
        disableStats();
        setHardness(3.0F);
        setBlockTextureName(name + "_door");
        setBlockName(name + "_door");
        BaseHelper.setupStepSound(this);
        setCreativeTab(CreativeTabs.tabBlock);
    }

    public BaseDoor(String type) {
        this(Material.wood, type);
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return (meta & 8) != 0 ? null : Item.getItemFromBlock(this);
    }

    @Override
    public int getRenderType() {
        return BlockRenderers.DOOR.getRenderId();
    }

    @Override
    public String getItemIconName() {
        return getTextureName();
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
        field_150017_a = new IIcon[2];
        field_150016_b = new IIcon[2];
        blockIcon = field_150017_a[0] = reg.registerIcon(getTextureName() + "_top");
        field_150016_b[0] = reg.registerIcon(getTextureName() + "_bottom");
        field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
        field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
    }
}
