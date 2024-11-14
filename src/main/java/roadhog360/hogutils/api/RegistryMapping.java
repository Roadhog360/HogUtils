package roadhog360.hogutils.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class RegistryMapping<T> {

    private static final RegistryMapping keyInstance = new RegistryMapping<>();
    private T object;
    private transient int meta;
    private transient boolean wildcardAlwaysEqual;

    /**
     * Used by the mutable key instance to create a mutable key instance without a parameterized type, bypassing the below restrictions.
     * Not intended for general use.
     */
    private RegistryMapping() {
        wildcardAlwaysEqual = true;
        object = null;
        meta = 0;
    }

    /**
     * Used to store a block and meta mapping that can be matched with a new instance, or a global key instance.
     * Used by raw ores and deepslate generation. This object works as a map key, create a new instance when putting it in the map.
     * See RegistryMapping#getKeyFor for ability to retrieve a RegistryMapping from a list or map without spamming new instances.
     * </p>
     * If the last arg is TRUE, comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a wildcard meta.
     * </p>
     * If the last arg is FALSE, this instance only returns true if the object it is being compared against has the same metadata as it,
     * not counting wildcards unless that object also has a wildcard for its metadata.
     * </p>
     * This only affects the left hand side of the comparison; which would be the object that Object#equals is being called on, and not the passed in comparison.
     */
    public RegistryMapping(T obj, int meta, boolean wildcardAlwaysEqual) {
        if (!(obj instanceof Block) && !(obj instanceof Item)) {
            throw new IllegalArgumentException("RegistryMapping must be either an item or a block!");
        }
        this.wildcardAlwaysEqual = wildcardAlwaysEqual;
        setObject(obj);
        setMeta(meta);
    }

    public RegistryMapping(T obj, int meta) {
        this(obj, meta, true);
    }

    public T getObject() {
        return object;
    }

    public int getMeta() {
        return meta;
    }

    //TODO removed making it so either side being a wildcard always returns true if the meta
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegistryMapping<?> mapping && object == mapping.object
            && ((wildcardAlwaysEqual&& (meta == OreDictionary.WILDCARD_VALUE && mapping.meta == OreDictionary.WILDCARD_VALUE))
            || meta == mapping.meta);
    }

    @Override
    public int hashCode() {
        return object.hashCode(); // Do not hash meta so wildcards and metas all get placed into the same bucket
    }

    /**
     * Returns a recycled RegistryMapping instance of the specified type. This does NOT CREATE A NEW INSTANCE!
     * This is used by things like deepslate registry as a key object without spamming the garbage collector with tons of new instances.
     * So you should use "new RegistryMapping<?></?>(obj, meta) to create a new RegistryMapping, again, this DOES NOT CREATE A NEW INSTANCE!
     * @return
     */
    public static <E> RegistryMapping<E> getKeyFor(E object, int meta) {
        return getKeyFor(object, meta, true);
    }

    /**
     * Makes sure this object isn't the global key instance. Good for sanity checking a registry.
     */
    public static boolean isKeyInstance(RegistryMapping obj) {
        return obj == keyInstance;
    }

    /**
     * Returns a recycled RegistryMapping instance of the specified type. This does NOT CREATE A NEW INSTANCE!
     * This is used by things like deepslate registry as a key object without spamming the garbage collector with tons of new instances.
     * So you should use "new RegistryMapping<?></?>(obj, meta)" to create a new RegistryMapping, again, this DOES NOT CREATE A NEW INSTANCE!
     * If the last arg is TRUE, comparisons against instance comparisons true if a matching object of any metadata is compared, if this has a wildcard meta.
     * </p>
     * If the last arg is FALSE, this instance only returns true if the object it is being compared against has the same metadata as it,
     * not counting wildcards unless that object also has a wildcard for its metadata.
     * </p>
     * This only affects the left hand side of the comparison; which would be if you are calling Object#equals on THIS instead of passing it in as the argument.
     */
    public static <E> RegistryMapping<E> getKeyFor(E object, int meta, boolean wildcardAlwaysEqual) {
        keyInstance.wildcardAlwaysEqual = wildcardAlwaysEqual;
        return keyInstance.setObject(object).setMeta(meta);
    }

    private RegistryMapping<T> setObject(T obj) {
        if (!(obj instanceof Block) && !(obj instanceof Item)) {
            throw new IllegalArgumentException("RegistryMapping must be either an item or a block!");
        }
        this.object = obj;
        return this;
    }

    private RegistryMapping<T> setMeta(int meta) {
        this.meta = meta;
        return this;
    }
}
