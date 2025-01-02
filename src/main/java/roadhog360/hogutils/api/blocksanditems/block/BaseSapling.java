package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.utils.WeighedRandomList;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class BaseSapling extends BlockSapling implements ISubtypesBlock {

    /// This CANNOT be an array, NotEnoughIDs has NEGATIVE metas, so we need this instead.
    private final Map<Integer, IIcon> icons = new Int2ObjectArrayMap<>();
    private final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    protected final Map<Integer, WeighedRandomList<WorldGenerator>> trees = new Int2ObjectArrayMap<>();

    public BaseSapling(String... types) {
        super();
        for(int i = 0; i < types.length; i++) {
            if(types[i] != null && !types[i].isEmpty()) {
                getTypes().put(i, types[i] + "_sapling");
                getTypes().put(i + 8, types[i] + "_sapling");
            }
        }
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }

        setStepSound(BlockSand.soundTypeGrass);
    }

    @Override
    public Map<Integer, IIcon> getIcons() {
        return icons;
    }

    @Override
    public Map<Integer, String> getTypes() {
        return types;
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        String type = getTypes().get(stack.getItemDamage());
        return type == null ? getUnlocalizedName().replace("tile.", "")
            : BaseHelper.getUnlocalizedName(type, getNameDomain(type));
    }

    @Override
    public boolean usesMap() {
        return getTypes().size() > 4;
    }

    @Override
    public int damageDropped(int meta) {
        if(isMetadataEnabled(meta)) {
            return meta;
        }
        return 0; // Meta is not enabled, don't know what to do, drop 0. Might replace this with an exception.
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return getIcons().getOrDefault(meta, super.getIcon(side, meta));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        getIcons().clear();
        for (Map.Entry<Integer, String> entry : getTypes().entrySet()) {
            IIcon icon = reg.registerIcon(
                BaseHelper.getTextureName(entry.getValue(), getTextureDomain(entry.getValue()), getTextureSubfolder(entry.getValue())));
            getIcons().put(entry.getKey(), icon);
            getIcons().put(entry.getKey() + 8, icon);
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

    public BaseSapling setNames(String name) {
        setBlockName(name);
        setBlockTextureName(name);
        return this;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if(usesMap()) {
            super.getSubBlocks(itemIn, tab, list);
        } else for (Map.Entry<Integer, String> entry : getTypes().entrySet()) {
            if(isMetadataEnabled(entry.getKey()) && entry.getKey() % 16 < 8) {
                list.add(new ItemStack(itemIn, 1, entry.getKey()));
            }
        }
    }


    /**
     * MCP name: {@code growTree}
     */
    @Override
    public void func_149878_d(World p_149878_1_, int p_149878_2_, int p_149878_3_, int p_149878_4_, Random p_149878_5_) {
        if (!TerrainGen.saplingGrowTree(p_149878_1_, p_149878_5_, p_149878_2_, p_149878_3_, p_149878_4_)) {
            return;
        }

        WorldGenerator tree = trees.get(p_149878_1_.getBlockMetadata(p_149878_2_, p_149878_3_, p_149878_4_)).getRandom(p_149878_1_.rand);

        if (tree != null) {
            Block block = p_149878_1_.getBlock(p_149878_2_, p_149878_3_, p_149878_4_);
            int meta = p_149878_1_.getBlockMetadata(p_149878_2_, p_149878_3_, p_149878_4_);
            p_149878_1_.setBlockToAir(p_149878_2_, p_149878_3_, p_149878_4_);
            boolean success = tree.generate(p_149878_1_, p_149878_5_, p_149878_2_, p_149878_3_, p_149878_4_);
            if (!success) {
                p_149878_1_.setBlock(p_149878_2_, p_149878_3_, p_149878_4_, block, meta, 2);
            }
        }
    }

    protected BaseSapling addTree(int meta, WorldGenerator tree) {
        addTree(meta, new WeighedRandomList<>(tree));
        return this;
    }

    protected BaseSapling addTree(int meta, WeighedRandomList<WorldGenerator> trees) {
        this.trees.put(meta, trees);
        this.trees.put(meta + 8, trees);
        return this;
    }
}
