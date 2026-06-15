package roadhog360.hogutils.api.blocksanditems.utils;

import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.utils.base.IUniqueIndex;

public final class MetaPackingUtils {
    private MetaPackingUtils() {}

    public static long pack(Block block, int meta) {
        return pack((IUniqueIndex) block, meta);
    }

    public static long pack(Item item, int meta) {
        return pack((IUniqueIndex) item, meta);
    }

    public static long pack(@NonNull ItemStack stack) {
        return pack((IUniqueIndex) stack.getItem(), stack.getItemDamage());
    }

    private static long pack(IUniqueIndex obj, int meta) {
        return pack(obj.hogutils$getUniqueID(), meta);
    }

    public static long pack(int obj, int meta) {
        return (((long) obj) & 0xFFFFFFFFL) | (long) meta << 32;
    }

    public static boolean matchesSoft(Block block, int meta, long compareTo) {
        return matchesSoft((IUniqueIndex) block, meta, compareTo);
    }

    public static boolean matches(Block block, int meta, long compareTo) {
        return matches((IUniqueIndex) block, meta, compareTo);
    }

    public static boolean matchesSoft(Item item, int meta, long compareTo) {
        return matchesSoft((IUniqueIndex) item, meta, compareTo);
    }

    public static boolean matches(Item item, int meta, long compareTo) {
        return matches((IUniqueIndex) item, meta, compareTo);
    }

    public static boolean matches(ItemStack stack, long compareTo) {
        return matches((IUniqueIndex) stack.getItem(), stack.getItemDamage(), compareTo);
    }

    public static boolean matches(Block block, long compareTo) {
        return matches(block, OreDictionary.WILDCARD_VALUE, compareTo);
    }

    public static boolean matches(Item item, long compareTo) {
        return matches(item, OreDictionary.WILDCARD_VALUE, compareTo);
    }

    private static boolean matchesSoft(IUniqueIndex obj, int meta, long compareTo) {
        return matchesSoft(pack(obj.hogutils$getUniqueID(), meta), compareTo);
    }

    public static boolean matchesSoft(long compare1, long compare2) {
        if(getIDFromLong(compare1) == getIDFromLong(compare2)) {
            int meta1 = getMetaFromLong(compare1);
            int meta2 = getMetaFromLong(compare2);
            return meta1 == meta2 || meta1 == OreDictionary.WILDCARD_VALUE || meta2 == OreDictionary.WILDCARD_VALUE;
        }
        return false;
    }

    private static boolean matches(IUniqueIndex obj, int meta, long compareTo) {
        return pack(obj, meta) == compareTo;
    }

    private static boolean matchesObj(IUniqueIndex obj, long compareTo) {
        return obj.hogutils$getUniqueID() == (int) compareTo;
    }

    public static boolean matchesMeta(int meta, long compareTo) {
        return meta == getMetaFromLong(compareTo);
    }

    public static int getMetaFromLong(long packed) {
        return (int) (packed >> 32);
    }

    public static int getIDFromLong(long packed) { return (int) packed;}

    @Nullable
    public static Block getBlockFromLong(long packed) {
        int index = (int) packed;
        if(index < 0 || index >= BLOCK_INDICES.length) {
            return null;
        }
        return BLOCK_INDICES[index];
    }

    @Nullable
    public static Item getItemFromLong(long packed) {
        int index = (int) packed;
        if(index < 0 || index >= ITEM_INDICES.length) {
            return null;
        }
        return ITEM_INDICES[index];
    }

    private static Block[] BLOCK_INDICES = new Block[0];
    private static Item[] ITEM_INDICES = new Item[0];

    @ApiStatus.Internal
    public static void registerBlock(int id, Block block) {
        if(id != BLOCK_INDICES.length) {
            throw new RuntimeException("This block ID was already registered!");
        }
        BLOCK_INDICES = ArrayUtils.add(BLOCK_INDICES, block);
    }

    @ApiStatus.Internal
    public static void registerItem(int id, Item item) {
        if(id != ITEM_INDICES.length) {
            throw new RuntimeException("This item ID was already registered!");
        }
        ITEM_INDICES = ArrayUtils.add(ITEM_INDICES, item);
    }
}
