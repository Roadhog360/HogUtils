package roadhog360.hogutils.api.hogtags.interfaces;

import java.util.Set;

/// For usage examples and how the interface should be implemented, look at MixinBiomeGenBase.
/// For usage examples on how more complex cases like Blocks and Items should be handled, look at those respective mixins.
///
/// There is no inheritor behavior here or reverse lookup behavior because that should be static,
/// since it's not specific to any object but shared between all of that taggable type.
public interface ITaggable {
    /// Adds tags to the specified object this interface is implemented to.
    void addTags(String... tags);

    /// Removes tags from the specified object this interface is implemented to.
    void removeTags(String... tags);

    /// Gets the tags that this object has applied to it.
    Set<String> getTags();

    /// Clear any lookup caches.
    void clearCaches();
}
