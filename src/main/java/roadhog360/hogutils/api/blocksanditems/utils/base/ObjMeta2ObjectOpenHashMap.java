package roadhog360.hogutils.api.blocksanditems.utils.base;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ApiStatus.NonExtendable
/// While other map types can be used, this one is optimized for performance with item/block pairs.
/// It also has more clearly defined behavior for if we should fall back to looking for
/// {@link OreDictionary#WILDCARD_VALUE} if we can't find the metadata that was passed in.
public class ObjMeta2ObjectOpenHashMap<K, V> implements Map<ObjMetaPair<K>, V> {
    /// The primary map backing this {@link Map} implementation, used to store and retrieve entries at a high speed.
    private final Map<K, Int2ObjectOpenHashMap<V>> backingMap = new Reference2ObjectOpenHashMap<>();
    /// Used to calculate size, as well as provide returns for the required functions to implement the Map interface.
    private final Map<ObjMetaPair<K>, V> helperMap = new Reference2ObjectOpenHashMap<>();
    private final boolean wildcardFallback;
    /// Used to cache the last access and the result to reach consecutive results faster.
    private final MutablePair<ObjMetaPair<K>, V> lastAccess = new MutablePair();

    /// @param wildcardFallback
    /// If searching for a metadata value that's not found, should we return the entry at {@link OreDictionary#WILDCARD_VALUE} if there is one?
    public ObjMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        this.wildcardFallback = wildcardFallback;
    }

    @Override
    public int size() {
        return helperMap.size();
    }

    @Override
    public boolean isEmpty() {
        return helperMap.isEmpty();
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
        return helperMap.containsValue(value);
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
        backingMap.clear();
        helperMap.clear();
        lastAccess.setLeft(null);
        lastAccess.setRight(null);
    }

    @Override
    public @NotNull Set<ObjMetaPair<K>> keySet() {
        return helperMap.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return helperMap.values();
    }

    @Override
    public @NotNull Set<Entry<ObjMetaPair<K>, V>> entrySet() {
        return helperMap.entrySet();
    }

    public V put(K key, int meta, V val) {
        helperMap.put(internPair(key, meta), val);
        return backingMap.computeIfAbsent(key, o -> new Int2ObjectOpenHashMap<>()).put(meta, val);
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
        lastAccess.setLeft(null);
        lastAccess.setRight(null);
        helperMap.remove(internPair(key, meta));
        Int2ObjectOpenHashMap<V> map = backingMap.get(key);
        if(map == null) {
            return null;
        }
        V val = map.remove(meta);
        if(map.isEmpty()) {
            backingMap.remove(key, map);
        }
        return val;
    }

    public V get(K key, int meta) {
        if(lastAccess.getLeft().get() == key && lastAccess.getLeft().getMeta() == meta) { // Check if the last access was identical to this one
            return lastAccess.getRight(); // If it is, just return that result, I am speed.
        }
        Int2ObjectOpenHashMap<V> map = backingMap.get(key);
        if(map == null) {
            return null;
        }
        V get = map.get(meta);
        if(get != null) {
            lastAccess.setLeft(internPair(key, meta));
            lastAccess.setRight(get);
        }
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
}
