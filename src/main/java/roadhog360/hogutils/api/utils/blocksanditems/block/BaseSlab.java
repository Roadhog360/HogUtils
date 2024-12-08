package roadhog360.hogutils.api.utils.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.utils.blocksanditems.BaseHelper;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public abstract class BaseSlab extends BlockSlab implements ISubBlocksBlock {

    /// This CANNOT be an array, NotEnoughIDs has NEGATIVE metas, so we need this instead.
    private final Map<Integer, IIcon> icons = new Int2ObjectArrayMap<>();
    private final Map<Integer, String> types = new Int2ObjectArrayMap<>();
    protected Block droppedBlock = this;

    /// Creates a **SINGLE SLAB** instance
    public BaseSlab(Material material, String... types) {
        this(false, material);
        if (types.length > 8) {
            throw new IllegalArgumentException("Slabs can't have more than 8 subtypes! Tried to register a slab with " + types.length);
            // TODO: Edit this to skip every 8th so it can work with extended ID slabs.
        }

        for(int i = 0; i < types.length; i++) {
            if(types[i] != null && !types[i].isEmpty()) {
                getTypes().put(i, types[i]);
                getTypes().put(i + 8, types[i]);
            }
        }
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }
    }

    /// Creates a **SINGLE SLAB** instance
    public BaseSlab(Material material) {
        this(false, material);
    }

    /// Creates the double slab from the single slab block instance.
    /// This needs to be overridden (even if it's just a super(singleSlab) call) if you use RegistryEntryBlockSlab
    public BaseSlab(BaseSlab singleSlab) {
        this(true, singleSlab.getMaterial());
        getTypes().putAll(singleSlab.getTypes());
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }
        setBlockTextureName(singleSlab.textureName);
        setBlockName(singleSlab.unlocalizedName);
        droppedBlock = singleSlab;
    }

    protected BaseSlab(boolean isDouble, Material material) {
        super(isDouble, material);
        useNeighborBrightness = !isDouble;
        opaque = isDouble;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(droppedBlock);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(droppedBlock);
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
    public String func_150002_b(int meta) {
        String type = getTypes().get(meta);
        return type == null ? getUnlocalizedName() : type;
    }

    @Override
    public String getNameFor(ItemStack stack) {
        return func_150002_b(stack.getItemDamage());
    }

    public BaseSlab setNames(String name) {
        setBlockName(name);
        setBlockTextureName(name);
        return this;
    }

    public BaseSlab setHarvestTool(String toolClass, int level) {
        for (int m = GenericUtils.getMinBlockMetadata(); m <= GenericUtils.getMaxBlockMetadata(); m++) {
            setHarvestLevel(toolClass, level, m);
        }
        return this;
    }

    public BaseSlab setHarvestTool(String toolClass, int level, int meta) {
        setHarvestLevel(toolClass, level, meta);
        setHarvestLevel(toolClass, level, meta + 8);
        return this;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if(getTypes().isEmpty()) {
            super.getSubBlocks(itemIn, tab, list);
        } else for (Map.Entry<Integer, String> entry : getTypes().entrySet()) {
            if(isMetadataEnabled(entry.getKey()) && entry.getKey() % 16 < 8) {
                // Every 8 metas are hidden from Creative. "should" work with ID extenders, untested
                list.add(new ItemStack(itemIn, 1, entry.getKey()));
            }
        }
    }

    protected final BlockHardnessHelper hardnessInfo = new BlockHardnessHelper(this);

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        return hardnessInfo.getBlockHardness(worldIn, x, y, z);
    }

    public BaseSlab setHardnessValues(float hardness, int... metas) {
        hardnessInfo.setHardnessValues(hardness, metas);
        int[] metaUpper = ArrayUtils.clone(metas);
        IntStream.range(0, metas.length).forEach(i -> metaUpper[i] += 8);
        hardnessInfo.setHardnessValues(hardness, metaUpper);
        return this;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        return hardnessInfo.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    public BaseSlab setResistanceValues(float resistance, int... metas) {
        hardnessInfo.setResistanceValues(resistance, metas);
        int[] metaUpper = ArrayUtils.clone(metas);
        IntStream.range(0, metas.length).forEach(i -> metaUpper[i] += 8);
        hardnessInfo.setResistanceValues(resistance, metaUpper);
        return this;
    }
}
