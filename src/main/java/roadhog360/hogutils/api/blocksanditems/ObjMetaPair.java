package roadhog360.hogutils.api.blocksanditems;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

/// Used as an easy way to make a container object for comparing block/item and meta instances.
@ApiStatus.NonExtendable
public class ObjMetaPair<T> extends Pair<T, Integer> implements IReferenceBase<T> {
    private final T object;
    private final transient int meta;

    public ObjMetaPair(T obj, int meta) {
        this.object = obj;
        this.meta = meta;
    }

    @Override
    public T get() {
        return object;
    }

    public int getMeta() {
        return meta;
    }

    // Since we have the above enforcement of non-duplicate objects, is the below code needed now?
    // Maybe it's a good idea to keep it just in case. The == part is the first line after all; "should" be fine

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof ObjMetaPair<?> mapping && matches(mapping.get(), mapping.getMeta());
    }

    public boolean matches(Object compareObject, int compareMeta) {
        return get() == compareObject && (getMeta() == compareMeta
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
        return get();
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
        throw new UnsupportedOperationException("Called Pair.of from ObjMetaPair!" +
            " Note that ObjMetaPair can only be of type Block or Item and metatata! (Integer)");
    }

    /// Not really used for this purpose.
    /// Just put this here because I don't feel like moving this specific value to a separate interface.
    @Override
    @Deprecated
    public boolean isEnabled() {
        return true;
    }

    /// Use {@link #newItemStack(int)} or {@link #newItemStack()} for ObjMetaPair.
    @Override
    @Deprecated
    public ItemStack newItemStack(int count, @Deprecated int meta) {
        return IReferenceBase.super.newItemStack(count, getMeta());
    }

    @Override
    public int compareTo(Pair<T, Integer> compare) {
        int result = Integer.compare(compare.getLeft().hashCode(), get().hashCode());
        if(result == 0) {
            return Integer.compare(compare.getRight(), getMeta());
        }
        return result;
    }
}
