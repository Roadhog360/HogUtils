package roadhog360.hogutils.api.blocksanditems.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.List;
import java.util.Map;

public abstract class BaseWall extends BlockWall implements ISubtypesBlock {

    /// This CANNOT be an array, NotEnoughIDs has NEGATIVE metas, so we need this instead.
    private final Map<Integer, IIcon> icons = new Int2ObjectArrayMap<>();
    private final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    public BaseWall(Material material, String... types) {
        super(Blocks.stone); //Dummy value, we really don't care what we put here because we're not using this value
        blockMaterial = material;
        for(int i = 0; i < types.length; i++) {
            if(types[i] != null && !types[i].isEmpty()) {
                getTypes().put(i, types[i]);
            }
        }
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }
    }

    public BaseWall(String... types) {
        this(Material.rock, types);
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return true;
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
        return type == null ? getUnlocalizedName().replace("tile.", "") :
            BaseHelper.depluralizeName(BaseHelper.getUnlocalizedName(type, getNameDomain(type))).replace("_plank", "") + "_fence";
    }

    @Override
    public int damageDropped(int meta) {
        if(isMetadataEnabled(meta)) {
            return meta;
        }
        return 0; // Meta is not enabled, don't know what to do, drop 0.
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
        return "tile." +
            BaseHelper.depluralizeName(BaseHelper.getUnlocalizedName(unlocalizedName, getNameDomain(unlocalizedName)))
                .replace("_plank", "") + "_fence";
    }

    @Override
    protected String getTextureName() {
        return BaseHelper.getTextureName(textureName, getTextureDomain(textureName), getTextureSubfolder(textureName));
    }

    public BaseWall setNames(String name) {
        setBlockName(name);
        setBlockTextureName(name);
        return this;
    }

    public BaseWall setHarvestTool(String toolClass, int level) {
        for (int m = GenericUtils.getMinBlockMetadata(); m <= GenericUtils.getMaxBlockMetadata(); m++) {
            setHarvestLevel(toolClass, level, m);
        }
        return this;
    }

    public BaseWall setHarvestTool(String toolClass, int level, int meta) {
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

    protected BlockHardnessHelper hardnessInfo = new BlockHardnessHelper(this);

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        return hardnessInfo.getBlockHardness(worldIn, x, y, z);
    }

    public BaseWall setHardnessValues(float hardness, int... metas) {
        hardnessInfo.setHardnessValues(hardness, metas);
        return this;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        return hardnessInfo.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    public BaseWall setResistanceValues(float resistance, int... metas) {
        hardnessInfo.setResistanceValues(resistance, metas);
        return this;
    }
}
