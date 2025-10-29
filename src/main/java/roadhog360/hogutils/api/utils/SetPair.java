package roadhog360.hogutils.api.utils;

import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Set;

/// Creates a pair from a {@link Set}. One is modifiable and a copy is made from {@link Collections#unmodifiableSet}. (Referred to as "unlocked" and "ulocked")
/// This is so internally a function can modify a set whilst returning a version that cannot be modified.
/// The unlocked version of the set should be considered internal and shouldn't be exposed.
public final class SetPair<E> {
    private final Set<E> unlocked, locked;

    public SetPair(Set<E> unlocked) {
        this.unlocked = unlocked;
        this.locked = Collections.unmodifiableSet(unlocked);
    }

    public Set<E> getUnlocked() {
        return unlocked;
    }

    @SuppressWarnings("getter")
    public Set<E> getLocked() {
        return locked;
    }

    private static final SetPair<?> EMPTY = new SetPair<>(ImmutableSet.of());

    @SuppressWarnings("unchecked")
    public static <E> SetPair<E> getEmpty() {
        return (SetPair<E>) EMPTY;
    }
}
