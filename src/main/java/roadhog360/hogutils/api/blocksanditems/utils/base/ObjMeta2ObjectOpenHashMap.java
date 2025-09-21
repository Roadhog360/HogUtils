package roadhog360.hogutils.api.blocksanditems.utils.base;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;

import java.util.*;

@ApiStatus.NonExtendable
/// While other map types can be used, this one is optimized for performance with item/block pairs.
/// It also has more clearly defined behavior for if we should fall back to looking for
/// {@link OreDictionary#WILDCARD_VALUE} if we can't find the metadata that was passed in.
public class ObjMeta2ObjectOpenHashMap<K, V> implements Map<ObjMetaPair<K>, V> {
    /// The primary map backing this {@link Map} implementation, used to store and retrieve entries at a high speed.
    private final Map<K, Int2ObjectOpenHashMap<V>> backingMap = new Reference2ObjectOpenHashMap<>();
    private final boolean wildcardFallback;
    private int size = 0;

    /// @param wildcardFallback
    /// If searching for a metadata value that's not found, should we return the entry at {@link OreDictionary#WILDCARD_VALUE} if there is one?
    protected ObjMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        this.wildcardFallback = wildcardFallback;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    /// Only accepts {@link ObjMetaPair} instances. They do not have to be interned.
    public boolean containsKey(Object key) {
        if(key instanceof ObjMetaPair<?> pair) {
            return containsKey((K) pair.get(), pair.getMeta());
        }
        throw new IllegalArgumentException("Parameter passed in was not an ObjMetaPair object!");
    }

    public boolean containsKey(K key, int meta) {
        Int2ObjectOpenHashMap<V> map = backingMap.get(key);
        return map != null && map.containsKey(meta);
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    /// Only accepts {@link ObjMetaPair} instances. They do not have to be interned.
    public V get(Object key) {
        if(key instanceof ObjMetaPair<?> pair) {
            get((K) pair.get(), pair.getMeta());
        }
        throw new IllegalArgumentException("Parameter passed in was not an ObjMetaPair object!");
    }

    @Override
    /// Only accepts {@link ObjMetaPair} instances. They do not have to be interned.
    public V getOrDefault(Object key, V defaultValue) {
        if(key instanceof ObjMetaPair<?> pair) {
            getOrDefault((K) pair.get(), pair.getMeta(), defaultValue);
        }
        throw new IllegalArgumentException("Parameter passed in was not an ObjMetaPair object!");
    }

    public V getOrDefault(K key, int meta, V defaultValue) {
        V v;
        return (((v = get(key, meta)) != null) || containsKey(key))
            ? v
            : defaultValue;
    }

    @Override
    /// Only accepts {@link ObjMetaPair} instances. They do not have to be interned.
    public @Nullable V put(ObjMetaPair<K> key, V value) {
        return put(key.get(), key.getMeta(), value);
    }

    @Override
    /// Only accepts {@link ObjMetaPair} instances. They do not have to be interned.
    public V remove(Object key) {
        if(key instanceof ObjMetaPair<?> pair) {
            return remove((K) pair.get(), pair.getMeta());
        }
        throw new IllegalArgumentException("Parameter passed in was not an ObjMetaPair object!");
    }

    @Override
    /// Only accepts {@link ObjMetaPair} instances. They do not have to be interned.
    public boolean remove(Object key, Object value) {
        if(key instanceof ObjMetaPair<?> pair) {
            return remove((K) pair.get(), pair.getMeta(), value);
        }
        throw new IllegalArgumentException("Parameter passed in was not an ObjMetaPair object!");
    }

    public boolean remove(K key, int meta, Object value) {
        Object curValue = get(key, meta);
        if (!Objects.equals(curValue, value) ||
            (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key, meta);
        return true;
    }

    @Override
    public void putAll(@NotNull Map<? extends ObjMetaPair<K>, ? extends V> m) {
        m.forEach((k, v) -> put(k.get(), k.getMeta(), v));
    }

    @Override
    public void clear() {
        size = 0;
        backingMap.clear();
    }

    @Override
    public @NotNull Set<ObjMetaPair<K>> keySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<ObjMetaPair<K>> iterator() {
                return new ObjMetaKeyIterator(entrySet().iterator());
            }

            @Override
            public int size() {
                return ObjMeta2ObjectOpenHashMap.this.size();
            }
        };
    }

    @Override
    public @NotNull Collection<V> values() {
        return new AbstractSet<>() {
            @Override
            public Iterator<V> iterator() {
                return new ObjMetaValueIterator(entrySet().iterator());
            }

            @Override
            public int size() {
                return ObjMeta2ObjectOpenHashMap.this.size();
            }
        };
    }

    @Override
    public @NotNull Set<Entry<ObjMetaPair<K>, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public @NotNull Iterator<Map.Entry<ObjMetaPair<K>, V>> iterator() {
                return new ObjMetaEntryIterator();
            }

            @Override
            public int size() {
                return ObjMeta2ObjectOpenHashMap.this.size();
            }
        };
    }

    public V put(K key, int meta, V val) {
        V ret = backingMap.computeIfAbsent(key, o -> new Int2ObjectOpenHashMap<>()).put(meta, val);
        if(ret == null) {
            size++;
        }
        return ret;
    }

    @Override
    public V putIfAbsent(ObjMetaPair<K> key, V value) {
        return putIfAbsent(key.get(), key.getMeta(), value);
    }

    public V putIfAbsent(K key, int meta, V value) {
        V v = get(key, meta);
        if (v == null) {
            v = put(key, meta, value);
        }

        return v;
    }

    public V remove(K key, int meta) {
        Int2ObjectOpenHashMap<V> map = backingMap.get(key);
        if(map == null) {
            return null;
        }
        V val = map.remove(meta);
        if(map.isEmpty()) {
            backingMap.remove(key, map);
        }
        if(val != null) {
            size--;
        }
        return val;
    }

    public V get(K key, int meta) {
        Int2ObjectOpenHashMap<V> map = backingMap.get(key);
        if(map == null) {
            return null;
        }
        V get = map.get(meta);
        return get == null && wildcardFallback ? get(key, OreDictionary.WILDCARD_VALUE) : get;
    }

    protected ObjMetaPair<K> internPair(K obj, int meta) {
        if(obj instanceof Block block) {
            return (ObjMetaPair<K>) BlockMetaPair.intern(block, meta);
        }
        if(obj instanceof Item item) {
            return (ObjMetaPair<K>) ItemMetaPair.intern(item, meta);
        }
        throw new IllegalArgumentException("Parameter passed in was not a block or item!");
    }

    // A custom iterator class for the entry set
    private class ObjMetaEntryIterator implements Iterator<Map.Entry<ObjMetaPair<K>, V>> {
        private final Iterator<Map.Entry<K, Int2ObjectOpenHashMap<V>>> outerIterator = backingMap.entrySet().iterator();
        private Map.Entry<K, Int2ObjectOpenHashMap<V>> currentOuterEntry;
        private ObjectIterator<Int2ObjectMap.Entry<V>> innerIterator;
        private Map.Entry<Integer, V> currentInnerEntry;
        private Map.Entry<ObjMetaPair<K>, V> nextEntry;

        @Override
        public boolean hasNext() {
            if (nextEntry != null) {
                return true;
            }
            while (outerIterator.hasNext() || (innerIterator != null && innerIterator.hasNext())) {
                if (innerIterator != null && innerIterator.hasNext()) {
                    currentInnerEntry = innerIterator.next();
                    nextEntry = new AbstractMap.SimpleEntry<>(internPair(currentOuterEntry.getKey(), currentInnerEntry.getKey()), currentInnerEntry.getValue());
                    return true;
                }
                currentOuterEntry = outerIterator.next();
                innerIterator = currentOuterEntry.getValue().int2ObjectEntrySet().iterator();
            }
            return false;
        }

        @Override
        public Map.Entry<ObjMetaPair<K>, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Map.Entry<ObjMetaPair<K>, V> entry = nextEntry;
            nextEntry = null;
            return entry;
        }
    }

    private abstract class ObjMetaIteratorWrapper<T> implements Iterator<T> {
        protected final Iterator<Map.Entry<ObjMetaPair<K>, V>> iter;
        ObjMetaIteratorWrapper(Iterator<Map.Entry<ObjMetaPair<K>, V>> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }
    }

    /**
     * An iterator that lazily returns the keys from the nested map.
     */
    private class ObjMetaKeyIterator extends ObjMetaIteratorWrapper<ObjMetaPair<K>> {
        ObjMetaKeyIterator(Iterator<Map.Entry<ObjMetaPair<K>, V>> iter) {
            super(iter);
        }

        @Override
        public ObjMetaPair<K> next() {
            return iter.next().getKey();
        }
    }

    /**
     * An iterator that lazily returns the values from the nested map.
     */
    private class ObjMetaValueIterator extends ObjMetaIteratorWrapper<V> {
        ObjMetaValueIterator(Iterator<Map.Entry<ObjMetaPair<K>, V>> iter) {
            super(iter);
        }

        @Override
        public V next() {
            return iter.next().getValue();
        }
    }
}
