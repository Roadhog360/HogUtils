package roadhog360.hogutils.api.blocksanditems.utils.base;

/// Used to give every copy of an object a unique hash. It's on the implementer how to use this.
/// Used on blocks and items to give every one a unique hash for mapping purposes.
public interface IUniqueIndex {
    /// Implemented on blocks and items during construction to assign them a unique ID, mainly used for tagging.
    /// This ID is not guaranteed to be persistent between game reloads and should not be stored directly in data that is accessible between runs.
    int hogutils$getUniqueID();
}
