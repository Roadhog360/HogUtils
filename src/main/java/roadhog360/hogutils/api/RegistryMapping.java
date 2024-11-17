package roadhog360.hogutils.api;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class RegistryMapping<T> {

    @SuppressWarnings("rawtypes")
    private static final List<RegistryMapping> createdKeys = Lists.newLinkedList();
    @SuppressWarnings("rawtypes")
    private static final List<RegistryMapping> createdKeysWildcardUnequal = Lists.newLinkedList();

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
    /// When using `.equals` on them, if either side of the comparison has the metadata of {@link OreDictionary#WILDCARD_VALUE}, it will always return true
    public static <E> RegistryMapping<E> of(E object, int meta) {
        return of(object, meta, true);
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
        List<RegistryMapping> list = wildcardAlwaysEqual ? createdKeys : createdKeysWildcardUnequal;

        for(RegistryMapping mappingEntry : list) {
            if(mappingEntry.getObject() == object && mappingEntry.getMeta() == meta) {
                return mappingEntry;
            }
        }

        RegistryMapping mapping = new RegistryMapping<>(object, meta, wildcardAlwaysEqual);
        list.add(mapping);
        return mapping;
    }

    // Since we have the above enforcement of non-duplicate objects, is the below code needed now?
    // Maybe it's a good idea to keep it just in case.

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegistryMapping<?> mapping && object == mapping.object
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
