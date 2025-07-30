package roadhog360.hogutils.api.blocksanditems.utils.base;

import roadhog360.hogutils.api.blocksanditems.IReferenceBase;

import java.util.Collection;

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

    public void register() {
        checkName(name.toLowerCase());
        if (isEnabled()) {
            doRegistration();
        }
    }

    protected abstract void doRegistration();

    /// Enforce lower alphanumeric with underscores and slashes
    /// I do the lowercasing automatically but I check anyways in case this function is called from a custom class
    protected void checkName(String name) {
        if (!name.matches("^[a-z0-9_/]*$")) {
            throw new IllegalArgumentException(
                "Don't register a non-alphanumeric name! Just because you can doesn't mean you should!" +
                    "Forge should prevent this, so I'm doing their work for them..." +
                    "If you want to use my helper tools, alphanumeric ONLY with underscores (_) and forward slashes! (/)"
            );
        }
    }

    public static void registerAll(Collection<RegistryEntry<?>> list) {
        list.forEach(RegistryEntry::register);
    }
}
