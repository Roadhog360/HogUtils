package roadhog360.hogutils.api.utils;

import java.util.function.Supplier;

public class CachedSupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private volatile boolean initialized = false;
    private T suppliedValue;

    public CachedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!initialized) {
            setValue();
        }
        return suppliedValue;
    }

    protected synchronized void setValue() {
        if (!initialized) { // If multiple threads get past the first check, only one will win.
            suppliedValue = supplier.get();
            initialized = true;
        }
    }

    public synchronized boolean clear() {
        if (initialized) {
            suppliedValue = null;
            initialized = false;
            return true;
        }
        return false;
    }
}
