package roadhog360.hogutils.api.utils;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import lombok.NonNull;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Map;

public class MapUtils {
    private static final Map<Class<? extends Enum<?>>, Map<String, ? extends Enum<?>>> ENUM_MAPS = new Reference2ReferenceOpenHashMap<>();

    @NonNull
    public static <E extends Enum<E>> Map<String, E> enumToMap(Class<E> enumClass) {
        Map<String, E> map = (Map<String, E>) ENUM_MAPS.get(enumClass);
        if(map == null) {
            E[] constants = enumClass.getEnumConstants();
            // 1. Check if the Class object represents an Enum
            if (constants == null) {
                throw new IllegalArgumentException(enumClass.getName() + " is not an Enum type.");
            }
            map = new Object2ReferenceOpenHashMap<>();
            for (E enumValue : constants) {
                map.put(enumValue.name(), enumValue);
            }
            map = Collections.unmodifiableMap(map);
            ENUM_MAPS.put(enumClass, map);
        }
        return map;
    }

    public static <V> Map<ItemStack, V> newAdvancedItemStackMap() {
        return new Object2ObjectOpenCustomHashMap<>(new Hash.Strategy<>() {
            @Override
            public int hashCode(ItemStack o) {
                return o.getItem().hashCode() ^ o.getTagCompound().hashCode();
            }

            @Override
            public boolean equals(ItemStack a, ItemStack b) {
                return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage() && a.getTagCompound().equals(b.getTagCompound());
            }
        });
    }
}
