package roadhog360.hogutils.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/// Used as an easy way to make a container object for comparing block/item and meta instances.
///
/// Here's how you should reference these in maps:
/// If you want exact/"strict" matches only (meta has to be equal regardless of wildcards) then use:
/// - Reference/Identity maps for maps
/// - Tree sets for sets (AVL if iteration speed is a priority, else AB maps are a good all-rounder for this)
/// - Lists don't really have any reference-based types. If you need one as opposed to a set feel free to PR a list implementation for HogUtils.
///
/// If you want loose matches (meta can either be equal, or it's considered a match if either side has a wildcard) then use other types of lists/sets/maps.
/// The rule of thumb here mainly being for loose matching, use things that do {@link Object#equals} and for strict matching, use things which don't.
/// And finally, if you want loose matches, be careful where you insert mapping objects that have {@link OreDictionary#WILDCARD_VALUE}.
public final class RegistryMapping<T> extends Pair<T, Integer> {
    private final T object;
    private final transient int meta;

    private RegistryMapping(T obj, int meta) {
        this.object = obj;
        this.meta = meta;
    }

    public T getObject() {
        return object;
    }

    public int getMeta() {
        return meta;
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
    // Maybe it's a good idea to keep it just in case. The == part is the first line after all; "should" be fine

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof RegistryMapping<?> mapping && matches(mapping.getObject(), mapping.getMeta());
    }

    public boolean matches(Object compareObject, int compareMeta) {
        return getObject() == compareObject && (getMeta() == compareMeta
            || (getMeta() == OreDictionary.WILDCARD_VALUE || compareMeta == OreDictionary.WILDCARD_VALUE));
    }

    @Override
    public int hashCode() {
        return object.hashCode(); // Do not hash meta so wildcards and metas all get placed into the same bucket
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "object=" + object +
            ", meta=" + meta +
            '}';
    }

    @Override
    public T getLeft() {
        return getObject();
    }

    @Override
    public Integer getRight() {
        return getMeta();
    }

    @Override
    public Integer setValue(Integer value) {
        throw new UnsupportedOperationException();
    }

    /// Used to make sure {@link Pair#of(Object, Object)} is not accidentally called from this class, to prevent confusing results.
    /// This is to ensure the correct calls to the below functions are guaranteed, and to remove any margin of error when calling `of`
    public static Pair<?, ?> of(Object o1, Object o2) {
        throw new UnsupportedOperationException("Called Pair.of from RegistryMapping!" +
            " Note that RegistryMapping can only be of type Block or Item and metatata! (Integer)");
    }

    private static final Map<Object, Int2ObjectArrayMap<RegistryMapping<?>>> INTERNER = new Reference2ObjectArrayMap<>();

    @SuppressWarnings("unchecked")
    private synchronized static <E> RegistryMapping<E> getOrCreateMapping(E object, int meta) {
        return (RegistryMapping<E>) INTERNER.computeIfAbsent(object, o -> new Int2ObjectArrayMap<>())
            .computeIfAbsent(meta, o -> new RegistryMapping<>(object, meta));
    }

    /// Creates a new contained object for the specified block, and metadata. Supports wildcard values.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a {@link OreDictionary#WILDCARD_VALUE} meta.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an *exact* metadata match, see {@link RegistryMapping}
    public synchronized static RegistryMapping<Block> of(Block object, int meta) {
        return getOrCreateMapping(object, meta);
    }

    /// Creates a new contained object for the specified block, and metadata. Supports wildcard values.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a {@link OreDictionary#WILDCARD_VALUE} meta.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an exact metadata match, see {@link RegistryMapping}
    ///
    /// Exists so that passing in non-Primitive integers doesn't call {@link Pair#of(Object, Object)} instead.
    public synchronized static RegistryMapping<Block> of(Block object, Integer meta) {
        return of(object, (int) meta);
    }

    /// Creates a new contained object for the specified block, assuming the metadata is {@link OreDictionary#WILDCARD_VALUE}.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an exact metadata match, see {@link RegistryMapping}
    public synchronized static RegistryMapping<Block> of(Block object) {
        return of(object, OreDictionary.WILDCARD_VALUE);
    }

    /// Creates a new contained object for the specified item, and metadata. Supports wildcard values.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a {@link OreDictionary#WILDCARD_VALUE} meta.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an *exact* metadata match, see {@link RegistryMapping}
    public synchronized static RegistryMapping<Item> of(Item object, int meta) {
        return getOrCreateMapping(object, meta);
    }

    /// Creates a new contained object for the specified item, and metadata. Supports wildcard values.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a {@link OreDictionary#WILDCARD_VALUE} meta.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an exact metadata match, see {@link RegistryMapping}
    ///
    /// Exists so that passing in non-Primitive integers doesn't call {@link Pair#of(Object, Object)} instead.
    public synchronized static RegistryMapping<Item> of(Item object, Integer meta) {
        return of(object, (int) meta);
    }

    /// Creates a new contained object for the specified item, assuming the metadata is {@link OreDictionary#WILDCARD_VALUE}.
    ///
    /// Comparisons against instance comparisons true if a matching object of any metadata is compared.
    /// To force {@link RegistryMapping#equals(Object)} to only return TRUE on an exact metadata match, see {@link RegistryMapping}
    public synchronized static RegistryMapping<Item> of(Item object) {
        return of(object, OreDictionary.WILDCARD_VALUE);
    }

    /// Returns a {@link RegistryMapping} based on the current ItemStack. The type can be as a {@link Block} or {@link Item}.
    ///
    /// @param stack The {@link ItemStack} to convert to a {@link RegistryMapping}
    /// @param alwaysItem If true, then even if the {@link ItemStack} contains an {@link ItemBlock}, this will return a {@link RegistryMapping<Item>} anyways,
    /// containing a reference to the {@link ItemBlock} in the stack instead of its corresponding {@link Block}.
    public static RegistryMapping<?> fromItemStack(ItemStack stack, boolean alwaysItem) {
        if(!alwaysItem && stack.getItem() instanceof ItemBlock) {
            return of(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
        }
        return of(stack.getItem(), stack.getItemDamage());
    }


    /// Returns a {@link RegistryMapping} based on the current ItemStack. The type can be as a {@link Block} or {@link Item},
    /// this depends on if the {@link ItemStack} passed in is of an {@link ItemBlock} or a regular {@link Item}.
    ///
    /// @param stack The {@link ItemStack} to convert to a {@link RegistryMapping}
    public static RegistryMapping<?> fromItemStack(ItemStack stack) {
        return fromItemStack(stack, false);
    }
}
