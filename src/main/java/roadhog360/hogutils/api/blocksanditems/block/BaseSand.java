package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.List;
import java.util.Map;

public abstract class BaseSand extends BlockSand implements ISubtypesBlock {

    /// This CANNOT be an array, NotEnoughIDs has NEGATIVE metas, so we need this instead.
    private final Map<Integer, IIcon> icons = new Int2ObjectArrayMap<>();
    private final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    public BaseSand(String... types) {
        super();
        for(int i = 0; i < types.length; i++) {
            if(types[i] != null && !types[i].isEmpty()) {
                getTypes().put(i, types[i]);
            }
        }
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }

        setStepSound(BlockSand.soundTypeSand);
        setCreativeTab(CreativeTabs.tabBlock);
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
            getIcons().put(entry.getKey(),
                reg.registerIcon(
                    BaseHelper.getTextureName(entry.getValue(), getTextureDomain(entry.getValue()), getTextureSubfolder(entry.getValue()))
                ));
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

    public BaseSand setNames(String name) {
        setBlockName(name);
        setBlockTextureName(name);
        return this;
    }

    public BaseSand setHarvestTool(String toolClass, int level) {
        for (int m = GenericUtils.getMinBlockMetadata(); m <= GenericUtils.getMaxBlockMetadata(); m++) {
            setHarvestLevel(toolClass, level, m);
        }
        return this;
    }

    public BaseSand setHarvestTool(String toolClass, int level, int meta) {
        setHarvestLevel(toolClass, level, meta);
        return this;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if(getTypes().isEmpty()) {
            super.getSubBlocks(itemIn, tab, list);
        } else for (Map.Entry<Integer, String> entry : getTypes().entrySet()) {
            if(isMetadataEnabled(entry.getKey())) {
                list.add(new ItemStack(itemIn, 1, entry.getKey()));
            }
        }
    }

    protected final BlockHardnessHelper hardnessInfo = new BlockHardnessHelper(this);

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        return hardnessInfo.getBlockHardness(worldIn, x, y, z);
    }

    public BaseSand setHardnessValues(float hardness, int... metas) {
        hardnessInfo.setHardnessValues(hardness, metas);
        return this;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        return hardnessInfo.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    public BaseSand setResistanceValues(float resistance, int... metas) {
        hardnessInfo.setResistanceValues(resistance, metas);
        return this;
    }

    public EntityFallingBlock getFallingBlock(World world, double x, double y, double z, BlockFalling block, int meta) {
        return new EntityFallingBlock(world, x, y, z, block, meta);
    }

    protected void func_149830_m(World p_149830_1_, int p_149830_2_, int p_149830_3_, int p_149830_4_) {
        if (func_149831_e/*canFallBelow*/(p_149830_1_, p_149830_2_, p_149830_3_ - 1, p_149830_4_) && p_149830_3_ >= 0) {
            byte b0 = 32;

            if (!fallInstantly && p_149830_1_.checkChunksExist(p_149830_2_ - b0, p_149830_3_ - b0, p_149830_4_ - b0, p_149830_2_ + b0, p_149830_3_ + b0, p_149830_4_ + b0)) {
                if (!p_149830_1_.isRemote) {
                    EntityFallingBlock entityfallingblock = getFallingBlock(p_149830_1_, (float) p_149830_2_ + 0.5F, (float) p_149830_3_ + 0.5F, (float) p_149830_4_ + 0.5F, this, p_149830_1_.getBlockMetadata(p_149830_2_, p_149830_3_, p_149830_4_));
                    this.func_149829_a(entityfallingblock); // onStartFalling
                    p_149830_1_.spawnEntityInWorld(entityfallingblock);
                }
            } else {
                p_149830_1_.setBlockToAir(p_149830_2_, p_149830_3_, p_149830_4_);

                while (func_149831_e/*canFallBelow*/(p_149830_1_, p_149830_2_, p_149830_3_ - 1, p_149830_4_) && p_149830_3_ > 0) {
                    --p_149830_3_;
                }

                if (p_149830_3_ > 0) {
                    p_149830_1_.setBlock(p_149830_2_, p_149830_3_, p_149830_4_, this);
                }
            }
        }
    }
}
