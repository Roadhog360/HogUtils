package roadhog360.hogutils.api.blocksanditems.utils.base;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.blocksanditems.IReferenceBase;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableMeta;

import java.util.Set;

/// Used as an easy way to make a container object for comparing block/item and meta instances.
@ApiStatus.NonExtendable
public class ObjMetaPair<BlockOrItem> extends Pair<BlockOrItem, Integer> implements IReferenceBase<BlockOrItem>, ITaggable {
    private final BlockOrItem object;
    private final transient int meta;
    protected final boolean interned;

    protected ObjMetaPair(BlockOrItem obj, int meta, boolean isInterned) {
        this.object = obj;
        this.meta = meta;
        this.interned = isInterned;
    }

    protected ObjMetaPair(BlockOrItem obj, int meta) {
        this(obj, meta, true);
    }

    public boolean isInterned() {
        return interned;
    }

    @Override
    public BlockOrItem get() {
        return object;
    }

    public int getMeta() {
        return meta;
    }

    // Since we have the above enforcement of non-duplicate objects, is the below code needed now?
    // Maybe it's a good idea to keep it just in case. The == part is the first line after all; "should" be fine

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof ObjMetaPair<?> mapping && matchesSoft(mapping.get(), mapping.getMeta());
    }

    public boolean matchesSoft(Object compareObject, int compareMeta) {
        return get() == compareObject && (getMeta() == compareMeta
            || (getMeta() == OreDictionary.WILDCARD_VALUE || compareMeta == OreDictionary.WILDCARD_VALUE));
    }

    public boolean matches(BlockOrItem compareObject, int compareMeta) {
        return get() == compareObject && getMeta() == compareMeta;
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

    /// Should only be used when being fetched from a mod via reflection that doesn't have access to this class or its children.
    @Override
    @Deprecated
    public BlockOrItem getLeft() {
        return get();
    }

    /// Should only be used when being fetched from a mod via reflection that doesn't have access to this class or its children.
    @Override
    @Deprecated
    public Integer getRight() {
        return getMeta();
    }

    /// Not supported, throws {@link UnsupportedOperationException}; this view is immutable.
    @Override
    @Deprecated
    public Integer setValue(Integer value) {
        throw new UnsupportedOperationException();
    }

    /// Used to make sure {@link Pair#of(Object, Object)} is not accidentally called from this class, to prevent confusing results.
    /// This helps remind the developer using this class to call {@link ItemMetaPair#of(Item, int)}/{@link BlockMetaPair#of(Block, int)}
    /// This is to ensure the correct calls to the below functions are guaranteed, and to remove any margin of error when calling `of`.
    /// Throws {@link UnsupportedOperationException} when called.
    @Deprecated
    public static Pair<?, ?> of(Object o1, Object o2) {
        throw new UnsupportedOperationException("Called Pair.of from ObjMetaPair!" +
            " Note that ObjMetaPair can only be of type Block or Item and metatata! (Integer)");
    }

    /// Not used for pairs.
    /// Just put this here because I don't feel like moving this ONE field to a separate interface.
    /// @return Always returns true.
    @Override
    @Deprecated
    public boolean isEnabled() {
        return true;
    }

    /// Use {@link #newItemStack(int)} or {@link #newItemStack()} for ObjMetaPair.
    /// Will ignore passed in meta as this pair already provides it.
    @Override
    @Deprecated
    public ItemStack newItemStack(int count, @Deprecated int meta) {
        return IReferenceBase.super.newItemStack(count, getMeta());
    }

    @Override
    public int compareTo(Pair<BlockOrItem, Integer> compare) {
        int result = Integer.compare(compare.getLeft().hashCode(), get().hashCode());
        if(result == 0) {
            return Integer.compare(compare.getRight(), getMeta());
        }
        return result;
    }

    @Override
    public void addTags(String... tags) {
        ((ITaggableMeta)object).addTags(meta, tags);
    }

    @Override
    public void removeTags(String... tags) {
        ((ITaggableMeta)object).removeTags(meta, tags);
    }

    @Override
    public Set<String> getTags() {
        return ((ITaggableMeta)object).getTags(meta);
    }

    @Override
    public void clearCaches() {
        ((ITaggableMeta)object).clearCaches();
    }
}
