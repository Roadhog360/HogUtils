package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMeta2ObjectOpenHashMap;

public final class ItemMeta2ObjectOpenHashMap<V> extends ObjMeta2ObjectOpenHashMap<Item, V> {
    /// @param wildcardFallback
    /// If searching for a metadata value that's not found, should we return the entry at {@link OreDictionary#WILDCARD_VALUE} if there is one?
    public ItemMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        super(wildcardFallback);
    }

    /// Only evaluates the item and metadata, the NBT data for this {@link ItemStack} is NOT evaluated.
    public boolean containsKey(ItemStack key) {
        return containsKey(key.getItem(), key.getItemDamage());
    }

    /// Only evaluates the item and metadata, the NBT data for this {@link ItemStack} is NOT evaluated.
    public V get(ItemStack key) {
        return get(key.getItem(), key.getItemDamage());
    }

    /// Only evaluates the item and metadata, the NBT data for this {@link ItemStack} is NOT evaluated.
    public V getOrDefault(ItemStack key, V value) {
        return getOrDefault(key.getItem(), key.getItemDamage(), value);
    }

    /// Only evaluates the item and metadata, the NBT data for this {@link ItemStack} is NOT evaluated.
    public @Nullable V put(ItemStack key, V value) {
        return put(key.getItem(), key.getItemDamage(), value);
    }

    /// Only evaluates the item and metadata, the NBT data for this {@link ItemStack} is NOT evaluated.
    public V remove(ItemStack key) {
        return remove(key.getItem(), key.getItemDamage());
    }

    /// Only evaluates the item and metadata, the NBT data for this {@link ItemStack} is NOT evaluated.
    public boolean remove(ItemStack key, V value) {
        return remove(key.getItem(), key.getItemDamage(), value);
    }
}
