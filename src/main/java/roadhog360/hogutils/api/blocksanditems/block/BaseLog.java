package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;

import java.util.List;
import java.util.Map;

public abstract class BaseLog extends BlockLog implements ISubtypesBlock {

    private static final IIcon[] DUMMY = new IIcon[] {};

    protected final Map<Integer, IIcon> icons_side = new Int2ObjectArrayMap<>();
    protected final Map<Integer, IIcon> icons_top = new Int2ObjectArrayMap<>();
    protected final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    public BaseLog(String... types) {
        super();

        for(int i = 0; i < types.length; ++i) {
            if (types[i] != null && !types[i].isEmpty()) {
                this.getTypes().put(i, types[i]);
                this.getTypes().put(i + 4, types[i]);
                this.getTypes().put(i + 8, types[i]);
                this.getTypes().put(i + 12, types[i]);
            }
        }

        if (this.getTypes().containsKey(0)) {
            this.setNames(this.getTypes().get(0));
        }

        BaseHelper.setupStepSound(this);
    }

    public BaseLog setNames(String name) {
        this.setBlockName(name);
        this.setBlockTextureName(name);
        return this;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if(!usesMap()) {
            super.getSubBlocks(itemIn, tab, list);
        } else for (Map.Entry<Integer, String> entry : getTypes().entrySet()) {
            if(isMetadataEnabled(entry.getKey()) && entry.getKey() % 16 < 4) {
                list.add(new ItemStack(itemIn, 1, entry.getKey()));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getSideIcon(int p_150163_1_)
    {
        return icons_side.getOrDefault(p_150163_1_, blockIcon);
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getTopIcon(int p_150161_1_)
    {
        return icons_top.getOrDefault(p_150161_1_, blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        field_150167_a = DUMMY;
        field_150166_b = DUMMY;
        icons_side.clear();
        icons_top.clear();

        for(Map.Entry<Integer, String> entry : this.getTypes().entrySet()) {
            IIcon side = reg.registerIcon(BaseHelper.getTextureName(entry.getValue(), this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue())));
            IIcon top = reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_top", this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue())));
            icons_side.put(entry.getKey(), side);
            icons_side.put(entry.getKey() + 4, side);
            icons_side.put(entry.getKey() + 8, side);
            icons_side.put(entry.getKey() + 12, side);
            icons_top.put(entry.getKey(), top);
            icons_top.put(entry.getKey() + 4, top);
            icons_top.put(entry.getKey() + 8, top);
            icons_top.put(entry.getKey() + 12, top);
        }

        blockIcon = getIcons().getOrDefault(0, reg.registerIcon(textureName == null ? "missingno" : getTextureName()));
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
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return getMaterial() == Material.wood;
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return isFlammable(aWorld, aX, aY, aZ, aSide) ? 5 : 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return isFlammable(aWorld, aX, aY, aZ, aSide) ? 5 : 0;
    }

    @Override
    public Map<Integer, IIcon> getIcons() {
        return icons_side;
    }

    @Override
    public Map<Integer, String> getTypes() {
        return types;
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        String type = this.getTypes().get(stack.getItemDamage());
        return type == null ? getUnlocalizedName().replace("tile.", "")
            : BaseHelper.getUnlocalizedName(type, this.getNameDomain(type));
    }

    @Override
    public boolean usesMap() {
        return getTypes().size() > 4;
    }
}
