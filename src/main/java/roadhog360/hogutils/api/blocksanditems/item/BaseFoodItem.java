package roadhog360.hogutils.api.blocksanditems.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.ISubtypesBase;

import java.util.Map;

public abstract class BaseFoodItem extends ItemFood implements ISubtypesBase {

    /// This CANNOT be an array, NotEnoughIDs has NEGATIVE metas, so we need this instead.
    private final Map<Integer, IIcon> icons = new Int2ObjectArrayMap<>();
    private final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    public BaseFoodItem(int hunger, float saturation, boolean wolfLoves, String... types) {
        super(hunger, saturation, wolfLoves);
        for(int i = 0; i < types.length; i++) {
            if(types[i] != null && !types[i].isEmpty()) {
                getTypes().put(i, types[i]);
            }
        }
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }

        setHasSubtypes(getTypes().size() > 1);
        setCreativeTab(CreativeTabs.tabFood);
    }

    public BaseFoodItem(int hunger, boolean wolfLoves, String... types) {
        this(hunger, 0.6F, wolfLoves, types);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return getIcons().getOrDefault(stack.getItemDamage(), super.getIcon(stack, pass));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        getIcons().clear();
        for (Map.Entry<Integer, String> entry : getTypes().entrySet()) {
            getIcons().put(entry.getKey(),
                reg.registerIcon(
                    BaseHelper.getTextureName(entry.getValue(), getTextureDomain(entry.getValue()), getTextureSubfolder(entry.getValue()))
                ));
        }

        itemIcon = getIcons().getOrDefault(0, reg.registerIcon(iconString == null ? "missingno" : iconString));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if(!getHasSubtypes()) {
            return getUnlocalizedName();
        }

        String type = getTypes().get(stack.getItemDamage());
        return type == null ? getUnlocalizedName().replace("item.", "")
            : BaseHelper.getUnlocalizedName(type, getNameDomain(type));
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + BaseHelper.getUnlocalizedName(unlocalizedName, getNameDomain(unlocalizedName));
    }

    @Override
    protected String getIconString() {
        return BaseHelper.getTextureName(iconString, getTextureDomain(iconString), getTextureSubfolder(iconString));
    }

    public BaseFoodItem setNames(String name) {
        setUnlocalizedName(name);
        setTextureName(name);
        return this;
    }

    @Override
    public Map<Integer, IIcon> getIcons() {
        return icons;
    }

    @Override
    public Map<Integer, String> getTypes() {
        return types;
    }

    //TODO: Meta-sensitive eating stats and better utils for potions.

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack p_77626_1_)
    {
        return itemUseDuration; //We can't call the super because for some reason it doesn't use this value.
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player)
    {

        return super.onItemRightClick(itemStackIn, worldIn, player);
    }

    public int func_150905_g(ItemStack itemStackIn)
    {
        return super.func_150905_g(itemStackIn);
    }

    public float func_150906_h(ItemStack itemStackIn)
    {
        return super.func_150906_h(itemStackIn);
    }
}
