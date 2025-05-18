package roadhog360.hogutils.api.hogtags.interfaces;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

public interface ITaggableMeta extends ITaggable {
    /// Adds tags to the specified {@link Block} or {@link Item} this interface is implemented to.
    /// Assumes that {@link OreDictionary#WILDCARD_VALUE} should be the metadata passed in.
    default void addTags(String... tags) {
        addTags(OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Removes tags from the specified {@link Block} or {@link Item} this interface is implemented to.
    /// Assumes that {@link OreDictionary#WILDCARD_VALUE} should be the metadata passed in.
    default void removeTags(String... tags) {
        removeTags(OreDictionary.WILDCARD_VALUE, tags);
    }


    /// Gets the tags that this {@link Block} or {@link Item} has applied to it.
    /// Assumes that {@link OreDictionary#WILDCARD_VALUE} should be the metadata passed in.
    default Set<String> getTags() {
        return getTags(OreDictionary.WILDCARD_VALUE);
    }

    /// Removes tags from the specified {@link Block} or {@link Item} this interface is implemented to.
    void addTags(int meta, String... tags);

    /// Removes tags from the specified {@link Block} or {@link Item} this interface is implemented to.
    void removeTags(int meta, String... tags);

    /// Gets the tags that this {@link Block} or {@link Item} has applied to it.
    Set<String> getTags(int meta);
}
