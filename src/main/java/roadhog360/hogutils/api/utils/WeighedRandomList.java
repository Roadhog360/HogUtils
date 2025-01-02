package roadhog360.hogutils.api.utils;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class WeighedRandomList<T> {

    private class WeighedEntry<E> {
        double weight;
        double accumulatedWeight;
        E object;
    }

    public WeighedRandomList(T defaultElement) {
        this.defaultElement = defaultElement;
    }

    public WeighedRandomList() {
        this(null);
    }

    private List<WeighedEntry<T>> entries = new ObjectArrayList<>();
    private double accumulatedWeight;

    public List<WeighedEntry<T>> getEntries() {
        return entries;
    }

    public Map<T, Double> toMap() {
        Map<T, Double> map = new Object2DoubleArrayMap<>();
        for(WeighedEntry<T> entry : entries) {
            map.put(entry.object, entry.weight);
        }
        return map;
    }

    public static <T> WeighedRandomList<T> fromMap(Map<T, Double> chanceMap, T defaultElement) {
        WeighedRandomList<T> list = new WeighedRandomList<>(defaultElement);
        for(Map.Entry<T, Double> entry : chanceMap.entrySet()) {
            list.addEntry(entry.getKey(), entry.getValue());
        }
        return list;
    }

    public static <T> WeighedRandomList<T> fromMap(Map<T, Double> chanceMap) {
        return fromMap(chanceMap, null);
    }

    //Returns itself for easier construction
    public WeighedRandomList<T> addFromMap(Map<T, Double> chanceMap) {
        for(Map.Entry<T, Double> entry : chanceMap.entrySet()) {
            this.addEntry(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public void addEntry(T object, double weight) {
        if(weight <= 0.0D) {
            throw new IllegalArgumentException("Weight must be greater than 0!");
        }
        accumulatedWeight += weight;
        WeighedEntry<T> e = new WeighedEntry<>();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        e.weight = weight;
        entries.add(e);
    }

    public T getRandom(Random rand) {
        if(accumulatedWeight > 0.0D) {
            double r = rand.nextDouble() * accumulatedWeight;

            for (WeighedEntry<T> entry : entries) {
                if (entry.accumulatedWeight >= r) {
                    if(entry.object == null) {
                        return getDefaultElement();
                    }
                    return entry.object;
                }
            }
        }
        return getDefaultElement(); //should only happen when there are no entries
    }

    private final T defaultElement;

    public T getDefaultElement() {
        return defaultElement;
    }

    public void clear() {
        accumulatedWeight = 0;
        entries.clear();
    }
}
