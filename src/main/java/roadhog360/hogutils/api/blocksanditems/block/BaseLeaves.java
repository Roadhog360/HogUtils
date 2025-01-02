package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSand;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class BaseLeaves extends BlockLeaves implements ISubtypesBlock {

    private static final String[] DUMMY = new String[] {"missingno"};
    private static final IIcon[][] DUMMY_2 = new IIcon[0][0];

    protected final Map<Integer, IIcon> icons_fancy = new Int2ObjectArrayMap<>();
    protected final Map<Integer, IIcon> icons_fast = new Int2ObjectArrayMap<>();
    protected final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    public BaseLeaves(String... types) {
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

        setStepSound(BlockSand.soundTypeGrass);
    }

    public BaseLeaves setNames(String name) {
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

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.getIcons().getOrDefault(meta, blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        field_150129_M = DUMMY_2;

        icons_fancy.clear();
        icons_fast.clear();

        for(Map.Entry<Integer, String> entry : this.getTypes().entrySet()) {
            IIcon fancy = reg.registerIcon(BaseHelper.getTextureName(entry.getValue(), this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue())));
            IIcon fast = reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_opaque", this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue())));
            icons_fancy.put(entry.getKey(), fancy);
            icons_fancy.put(entry.getKey() + 4, fancy);
            icons_fancy.put(entry.getKey() + 8, fancy);
            icons_fancy.put(entry.getKey() + 12, fancy);
            icons_fast.put(entry.getKey(), fast);
            icons_fast.put(entry.getKey() + 4, fast);
            icons_fast.put(entry.getKey() + 8, fast);
            icons_fast.put(entry.getKey() + 12, fast);
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
    /// Our leaves ItemBlock won't be using this
    public String[] func_150125_e() { // Not used by our base leaves ItemBlock
        return DUMMY;
    }

    @Override
    public abstract Item getItemDropped(int meta, Random random, int fortune);

    // Both of these are needed for OF compat, and NotFine compat
    // Also makes Fancy toggling work, since for some reason the fancy/fast state of leaves is not a static field
    // Instead each leaves block gets their own but MC only ever sets the field for vanilla leaves.
    // This is why it's so common for modded leaves to ignore the fast/fancy toggle; technically that's the default behavior :P
    @Override
    public boolean isOpaqueCube() { //OptiFine compat
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return Blocks.leaves.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 30;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 60;
    }

    @Override
    public Map<Integer, IIcon> getIcons() {
        return isOpaqueCube() ? icons_fast : icons_fancy;
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
