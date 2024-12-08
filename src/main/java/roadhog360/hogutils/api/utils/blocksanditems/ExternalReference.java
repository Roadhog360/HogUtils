package roadhog360.hogutils.api.utils.blocksanditems;

import cpw.mods.fml.common.registry.GameRegistry;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.function.Supplier;

/// Designed to store references to another mod's blocks as well as a few helper utilities relating to them.
/// Reason for having this is "poking" a class of static references to {@link GameRegistry#findBlock(String, String)} (or findItem) could "ruin" references
/// if the static variables all initialize before a mod's preInit is reached in which one of the references points to.
///
/// This would cause the references to falsely return null instead of the correct item, which means you may want to lazily load the references as needed.
/// This alongside {@link RecipeHelper#validateItems(Object...)} or the recipe registering helper functions, would also allow you to register recipes
/// referencing modded blocks/items without having to check that they're enabled because the validator functions would do it for you.
public abstract class ExternalReference<T> implements IReferenceBase<T> {
    protected final String modID, namespace;
    private T object;
    private final Supplier<T> supplier;

    protected ExternalReference(String modID, String namespace) {
        this.modID = modID;
        this.namespace = namespace;
        this.supplier = getSupplier();
    }

    protected ExternalReference(String objectID) {
        this(objectID.split(":")[0], objectID.split(":")[1]);
    }

    public T get() {
        if (object == null) {
            object = supplier.get();
        }
        return object;
    }

    /// Could return false if this is called before the targeted mod actually loads this content.
    public boolean isEnabled() {
        return get() != null;
    }

    protected abstract Supplier<T> getSupplier();
}
