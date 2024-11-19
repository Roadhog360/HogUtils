package roadhog360.hogutils.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RegistryMapping<T> {

    @SuppressWarnings("rawtypes")
    private static final Set<RegistryMapping> createdKeys = Sets.newHashSet();
    @SuppressWarnings("rawtypes")
    private static final Set<RegistryMapping> createdKeysWildcardUnequal = Sets.newHashSet();

    private final T object;
    private final transient int meta;
    private final transient boolean wildcardAlwaysEqual;

    private RegistryMapping(T obj, int meta, boolean wildcardAlwaysEqual) {
        this.object = obj;
        this.meta = meta;
        this.wildcardAlwaysEqual = wildcardAlwaysEqual;
    }

    public T getObject() {
        return object;
    }

    public int getMeta() {
        return meta;
    }

    /// Creates a new contained object for the specified block or item, and metadata. Supports wildcard values.
    ///
    /// If the last arg is `TRUE`, comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a {@link OreDictionary#WILDCARD_VALUE} meta.
    ///
    /// If the last arg is `FALSE`, this instance only returns true if the object it is being compared against has the same metadata as it,
    /// not counting wildcards unless that object also has a wildcard for its metadata.
    ///
    /// This only affects the left hand side of the comparison; which would be if you are calling Object#equals on THIS instead of passing it in as the argument.
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E> RegistryMapping<E> of(E object, int meta, boolean wildcardAlwaysEqual) {
        if (!(object instanceof Block) && !(object instanceof Item)) {
            throw new IllegalArgumentException("RegistryMapping must be either an item or a block!");
        }
        //This is probably all pointless. Perhaps we can have better "interner at home" logic in the future
//        Set<RegistryMapping> list = wildcardAlwaysEqual ? createdKeys : createdKeysWildcardUnequal;
//
//        for(RegistryMapping mappingEntry : list) {
//            if(mappingEntry.getObject() == object && mappingEntry.getMeta() == meta) {
//                return mappingEntry;
//            }
//        }
//
//        RegistryMapping mapping = new RegistryMapping<>(object, meta, wildcardAlwaysEqual);
////        list.add(0, mapping);
////        while (list.size() > 100) {
////            list.remove(100);
////        }
//        return mapping;
        return new RegistryMapping<>(object, meta, wildcardAlwaysEqual);
    }

    /// Creates a new contained object for the specified block or item, and metadata. Supports wildcard values.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a {@link OreDictionary#WILDCARD_VALUE} meta.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an exact metadata match, see {@link RegistryMapping#of(Object, int, boolean)}
    public static <E> RegistryMapping<E> of(E object, int meta) {
        return of(object, meta, true);
    }

    public static RegistryMapping<Item> fromItemStack(ItemStack stack, boolean wildcardAlwaysEqual) {
        return of(stack.getItem(), stack.getItemDamage(), true);
    }

    public static RegistryMapping<Item> fromItemStack(ItemStack stack) {
        return fromItemStack(stack, true);
    }

    /// Creates a new ItemStack with stack size of 1.
    ///
    /// Throws IllegalArgumentException if this RegistryMapping is a block, and the block has no associated {@link net.minecraft.item.ItemBlock}.
    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    /// Creates a new ItemStack with the stack size specified.
    ///
    /// Throws IllegalArgumentException if this RegistryMapping is a block, and the block has no associated {@link net.minecraft.item.ItemBlock}.
    public ItemStack newItemStack(int count) {
        if(getObject() instanceof Item item) {
            return new ItemStack(item, count, getMeta());
        }
        if(getObject() instanceof Block block) {
            Item item = Item.getItemFromBlock(block);
            if(item == null) {
                throw new IllegalArgumentException("Cannot create ItemStack from itemless block!");
            }
            return new ItemStack(item, count, getMeta());
        }
        throw new IllegalArgumentException("RegistryMapping must be either an item or a block!");
    }

    // Since we have the above enforcement of non-duplicate objects, is the below code needed now?
    // Maybe it's a good idea to keep it just in case.

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof RegistryMapping<?> mapping && object == mapping.object
            && ((wildcardAlwaysEqual && (meta == OreDictionary.WILDCARD_VALUE && mapping.meta == OreDictionary.WILDCARD_VALUE))
            || meta == mapping.meta);
    }

    @Override
    public int hashCode() {
        return object.hashCode(); // Do not hash meta so wildcards and metas all get placed into the same bucket
    }

    @Override
    public String toString() {
        return "RegistryMapping{" +
            "object=" + object +
            ", meta=" + meta +
            ", wildcardAlwaysEqual=" + wildcardAlwaysEqual +
            '}';
    }
}
