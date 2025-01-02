package roadhog360.hogutils.api.blocksanditems.block.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.SneakyThrows;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import roadhog360.hogutils.api.blocksanditems.block.BaseSlab;

import javax.annotation.Nullable;

public class RegistryEntryBlockSlab extends RegistryEntryBlock {

    protected final BaseSlab doubleSlab;

    public RegistryEntryBlockSlab(String name, boolean isEnabled, BaseSlab block) {
        super(name, isEnabled, block);
        doubleSlab = createDoubleSlab();
    }

    public RegistryEntryBlockSlab(String name, boolean isEnabled, BaseSlab block,
                                  @Nullable Class<? extends ItemBlock> itemBlockClass) {
        super(name, isEnabled, block, itemBlockClass);
        doubleSlab = createDoubleSlab();
    }

    public RegistryEntryBlockSlab(String name, boolean isEnabled, BaseSlab block,
                                  @Nullable Class<? extends ItemBlock> itemBlockClass, @Nullable Object... itemBlockCtorArgs) {
        super(name, isEnabled, block, itemBlockClass, itemBlockCtorArgs);
        doubleSlab = createDoubleSlab();
    }

    @SneakyThrows
    protected BaseSlab createDoubleSlab() {
        BaseSlab slab = ((BaseSlab) object).getClass().getConstructor(boolean.class, Material.class).newInstance(true, object.blockMaterial);
        slab.setSingleSlabInfo((BaseSlab) object);
        return slab;
    }

    public Block getDoubleSlab() {
        return doubleSlab;
    }

    @Override
    protected void doRegistration() {
        super.doRegistration();
        if (hasItemBlock()) { // If this block has an ItemBlock, check whether it should be registered with constructor args
            if(itemBlockCtorArgs != null) {
                GameRegistry.registerBlock(doubleSlab, itemBlockClass, name.toLowerCase(), itemBlockCtorArgs);
            } else {
                GameRegistry.registerBlock(doubleSlab, itemBlockClass, name.toLowerCase());
            }
        } else {
            GameRegistry.registerBlock(doubleSlab, null, name.toLowerCase());
        }
    }
}
