package roadhog360.hogutils.api.blocksanditems.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.block.BaseDoor;
import roadhog360.hogutils.api.blocksanditems.block.BaseLeaves;
import roadhog360.hogutils.api.blocksanditems.block.BaseSlab;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;
import roadhog360.hogutils.api.blocksanditems.block.itemblock.BaseDoorItemBlock;
import roadhog360.hogutils.api.blocksanditems.block.itemblock.BaseItemBlock;
import roadhog360.hogutils.api.blocksanditems.block.itemblock.BaseLeavesItemBlock;
import roadhog360.hogutils.api.blocksanditems.block.itemblock.BaseSlabItemBlock;
import roadhog360.hogutils.api.blocksanditems.utils.base.RegistryEntry;

public class RegistryEntryBlock extends RegistryEntry<Block> {
    /**
     * null == NO ItemBlock
     */
    @Nullable
    protected final Class<? extends ItemBlock> itemBlockClass;
    @NonNull
    protected final Object[] itemBlockCtorArgs;

    public RegistryEntryBlock(String name, boolean isEnabled, Block block) {
        super(name, isEnabled, block);
        this.itemBlockClass = getItemBlock(block);
        this.itemBlockCtorArgs = new Object[0];
    }

    public RegistryEntryBlock(String name, boolean isEnabled, Block block,
                                  @Nullable Class<? extends ItemBlock> itemBlockClass) {
        this(name, isEnabled, block, itemBlockClass, new Object[0]);
    }

    public RegistryEntryBlock(String name, boolean isEnabled, Block block,
                                  @Nullable Class<? extends ItemBlock> itemBlockClass, @Nullable Object... itemBlockCtorArgs) {
        super(name, isEnabled, block);
        this.itemBlockClass = itemBlockClass;
        this.itemBlockCtorArgs = itemBlockCtorArgs == null ? new Object[0] : itemBlockCtorArgs;
    }

    /// Used when no ItemBlock is passed; we guess what it should get.
    /// This is used for convenience so every single block with sub blocks doesn't need to declare that;
    /// only special ItemBlocks that don't fit this criteria or no (null) ItemBlocks should need deliberate declaration.
    protected Class<? extends ItemBlock> getItemBlock(Block block) {
        return block instanceof BaseSlab ? BaseSlabItemBlock.class
              : block instanceof BaseDoor ? BaseDoorItemBlock.class
              : block instanceof BaseLeaves ? BaseLeavesItemBlock.class
              : block instanceof ISubtypesBlock ? BaseItemBlock.class
              : ItemBlock.class;
    }

    public boolean hasItemBlock() {
        return itemBlockClass != null;
    }

    public Item getItemBlock() {
        return Item.getItemFromBlock(object);
    }

    @Override
    protected void doRegistration() {
        if (hasItemBlock()) { // If this block has an ItemBlock, check whether it should be registered with constructor args
            GameRegistry.registerBlock(object, itemBlockClass, name.toLowerCase(), itemBlockCtorArgs);
        } else {
            GameRegistry.registerBlock(object, null, name.toLowerCase());
        }
    }
}
