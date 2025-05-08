package roadhog360.hogutils.api.hogtags.interfaces;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

/// T is the return type for the output lists. For most cases, this is probably going to be the same as the class implementing this.
/// Things like blocks and items will have it as a {@link Pair} for example.
///
/// For usage examples and how the interface should be implemented, look at MixinBiomeGenBase.
/// For usage examples on how more complex cases like Blocks and Items should be handled, look at those respective mixins.
///
/// It's recommended to use RB trees for the actively modified lists, and AVL trees for any caches.
///
/// There is no inheritor behavior here or reverse lookup behavior because that should be static, since it's not specific to any block.
public interface ITaggable<ReturnType> {
    /// Adds tags to the specified object this interface is implemented to.
    void addTags(String... tags);

    /// Removes tags from the specified object this interface is implemented to.
    void removeTags(String... tags);

    /// Gets the tags that this object has applied to it.
    Set<String> getTags();

    /// Clear any lookup caches.
    void clearCaches();
}
