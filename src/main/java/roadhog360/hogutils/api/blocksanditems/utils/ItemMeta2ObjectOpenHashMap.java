package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMeta2ObjectOpenHashMap;

public final class ItemMeta2ObjectOpenHashMap<V> extends ObjMeta2ObjectOpenHashMap<Item, V> {
    public ItemMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        super(wildcardFallback);
    }

    public boolean containsKey(ItemStack key) {
        return containsKey(key.getItem(), key.getItemDamage());
    }

    public V get(ItemStack key) {
        return get(key.getItem(), key.getItemDamage());
    }

    public V getOrDefault(ItemStack key, V value) {
        return getOrDefault(key.getItem(), key.getItemDamage(), value);
    }

    public @Nullable V put(ItemStack key, V value) {
        return put(key.getItem(), key.getItemDamage(), value);
    }

    public V remove(ItemStack key) {
        return remove(key.getItem(), key.getItemDamage());
    }

    public boolean remove(ItemStack key, V value) {
        return remove(key.getItem(), key.getItemDamage(), value);
    }
}
