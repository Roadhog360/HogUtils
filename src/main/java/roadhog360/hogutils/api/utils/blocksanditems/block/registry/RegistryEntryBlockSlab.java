package roadhog360.hogutils.api.utils.blocksanditems.block.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.SneakyThrows;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import roadhog360.hogutils.api.utils.blocksanditems.block.BaseSlab;

import javax.annotation.Nullable;

public class RegistryEntryBlockSlab extends RegistryEntryBlock {

    protected final Block doubleSlab;

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
    protected Block createDoubleSlab() {
        return object.getClass().getConstructor(BaseSlab.class).newInstance((BaseSlab)object);
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
