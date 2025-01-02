package roadhog360.hogutils.api.blocksanditems.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.ISubtypesBase;

import java.util.Map;

public abstract class BaseItem extends Item implements ISubtypesBase {
    /// This CANNOT be an array, NotEnoughIDs has NEGATIVE metas, so we need this instead.
    private final Map<Integer, IIcon> icons = new Int2ObjectArrayMap<>();
    private final Map<Integer, String> types = new Int2ObjectArrayMap<>();

    public BaseItem(String... types) {
        for(int i = 0; i < types.length; i++) {
            if(types[i] != null && !types[i].isEmpty()) {
                getTypes().put(i, types[i]);
            }
        }
        if(getTypes().containsKey(0)) {
            setNames(getTypes().get(0));
        }

        setHasSubtypes(getTypes().size() > 1);
        setCreativeTab(CreativeTabs.tabMisc);
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

    public BaseItem setNames(String name) {
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
}
