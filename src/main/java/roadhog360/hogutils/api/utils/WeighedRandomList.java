package roadhog360.hogutils.api.utils;

import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

/// Implementation of a weighed random list via arrays. The weight numbers do not have to add up to 100, and can be any number greater than 0.
public final class WeighedRandomList<T> {
    private Object[] entries = {};
    private double[] weights = {};
    private double totalWeight = 0;
    private final T defaultEntry;

    public WeighedRandomList() {
        this((T) null);
    }

    /// Creates this map, the value specified in the constructor parameter will be returned by {@link WeighedRandomList#get} if this list is empty.
    public WeighedRandomList(T defaultEntry) {
        this.defaultEntry = defaultEntry;
    }

    /// Converts a {@link Map}<{@link T}, {@link Double} to this list.
    /// Returns this {@link WeighedRandomList} for easier construction
    public WeighedRandomList(Map<T, Double> chanceMap) {
        this(chanceMap, null);
    }

    /// Converts a {@link Map}<{@link T}, {@link Double} to this list.
    /// Returns this {@link WeighedRandomList} for easier construction
    public WeighedRandomList(Map<T, Double> chanceMap, T defaultEntry) {
        this(defaultEntry);
        addAll(chanceMap);
    }

    /// Converts all entries in a {@link Map}<{@link T}, {@link Double} to this list.
    /// Returns this {@link WeighedRandomList} for easier construction
    public WeighedRandomList<T> addAll(@NonNull Map<T, Double> chanceMap) {
        chanceMap.forEach(this::put);
        return this;
    }

    /// Returns how many entries this list has.
    public int size() {
        return entries.length;
    }

    /// Returns false if this list has any entries.
    public boolean isEmpty() {
        return entries.length == 0;
    }

    /// Removes all entries from this list, resetting it to its default blank state.
    public void clear() {
        totalWeight = 0;
        entries = new Object[]{};
        weights = new double[]{};
    }

    /// Adds this element to the list. If it exists, its weight is set to the specified value
    /// If the entry does not exist, it is added and given the weight specified to start off.
    /// If this object is in this list, the previous weight associated with it is returned.
    public double put(@Nullable T object, double weight) {
        return insert(object, weight, true);
    }

    /// Adds this element to the list. If it exists, its weight is not changed.
    /// If the entry does not exist, it is added and given the weight specified to start off.
    /// If this object is in this list, the previous weight associated with it is returned.
    public double putIfAbsent(@Nullable T object, double weight) {
        return insert(object, weight, false);
    }

    private double insert(@Nullable T object, double weight, boolean reassign) {
        if(weight <= 0.0D) {
            throw new IllegalArgumentException("Weight must be greater than 0!");
        }
        int index = ArrayUtils.indexOf(entries, object);
        if(index == -1) {
            entries = ArrayUtils.add(entries, object);
            weights = ArrayUtils.add(weights, weight);
            totalWeight += weight;
            return -1;
        }
        double prevWeight = weights[index];
        if (reassign) {
            totalWeight += weight - weights[index];
            weights[index] = weight;
        }
        return prevWeight;
    }

    @Nullable
    /// Gets a random element from this list. If this list contains no objects, this function returns the second parameter.
    /// This function overrides the default return value.
    public T getOrDefault(@NonNull Random rand, @Nullable T def) {
        if(!isEmpty() && size() > 1) {
            double r = rand.nextDouble() * totalWeight;
            double accumulatedWeight = 0;
            for(int i = 0; i < size(); i++) {
                if ((accumulatedWeight += weights[i]) >= r) {
                    return (T) entries[i];
                }
            }
            throw new IllegalStateException("A critical error occured when processing a WeighedRandomList." +
                "\nThis is either a bug in HogUtils, or a mod has messed with its internals via reflection!");
        } if(size() == 1) {
            return (T) entries[0];
        }
        return def; //should only happen when there are no entries
    }

    @Nullable
    /// Gets a random element from this list. If this list contains no objects, this function returns the default value this map was created with, or null.
    public T get(@NonNull Random rand) {
        return getOrDefault(rand, defaultEntry);
    }

    /// Removes the object from this list, if it matches the weight specified.
    /// Returns the previous weight associated with this value or -1 if there was none
    public double remove(@Nullable T object, double weight) {
        int index = ArrayUtils.indexOf(entries, object);
        if(index != -1 && (weight <= 0 || weights[index] == weight)) {
            double removed = weights[index];
            entries = ArrayUtils.remove(entries, index);
            weights = ArrayUtils.remove(weights, index);
            totalWeight -= removed;
            return removed;
        }
        return -1;
    }

    /// Removes the object from this list
    public double remove(@Nullable T object) {
        return remove(object, -1);
    }

    /// Return true if this list contains the specified object.
    public boolean contains(@Nullable T object) {
        return ArrayUtils.contains(entries, object);
    }

    /// Returns true if this list contains the specified object, and it has the specified weight.
    public boolean contains(@Nullable T object, double weight) {
        int index = ArrayUtils.indexOf(entries, object);
        return index != -1 && weights[index] == weight;
    }

    /// Gets the weight of the object passed in. If the object isn't present in this list, -1 is returned.
    public double getWeight(@Nullable T object) {
        int index = ArrayUtils.indexOf(entries, object);
        return index != -1 ? weights[index] : -1;
    }
}
