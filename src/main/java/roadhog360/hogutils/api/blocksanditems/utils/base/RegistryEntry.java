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

    /// Preceded with {@link GenericUtils#isLowerAlphanumeric(String)} to verify the name is an
    /// alphanumeric lowercase name, only allowing underscores (_) and forward slashes (/) besides that.
    public final void register() {
        if (!GenericUtils.isLowerAlphanumeric(name)) {
            throw new IllegalArgumentException(
                "Non alphanumeric name detected whilst attempting to register object " + get().getClass().getName()
            );
        }
        if (isEnabled()) {
            doRegistration();
        }
    }

    protected abstract void doRegistration();
}
