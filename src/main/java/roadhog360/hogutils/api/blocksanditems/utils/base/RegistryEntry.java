package roadhog360.hogutils.api.blocksanditems.utils.base;

import roadhog360.hogutils.api.blocksanditems.IReferenceBase;
import roadhog360.hogutils.api.utils.GenericUtils;

public abstract class RegistryEntry<T> implements IReferenceBase<T> {
    protected final boolean isEnabled;
    protected final T object;
    protected final String name;

    protected RegistryEntry(String name, boolean isEnabled, T object) {
        this.name = name;
        this.isEnabled = isEnabled;
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /// Is wrapped with {@link GenericUtils#isLowerAlphanumeric(String)} at runtime to verify the name is an
    /// alphanumeric lowercase name, only allowing dots (.) and forward slashes (/) besides that.
    public void register() {
        if (isEnabled()) {
            doRegistration();
        }
    }

    protected abstract void doRegistration();
}
