package roadhog360.hogutils.api.hogtags.containers;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.blocksanditems.ObjMetaPair;
import roadhog360.hogutils.api.blocksanditems.block.container.BlockMetaPair;
import roadhog360.hogutils.api.blocksanditems.item.container.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.helpers.MiscHelpers;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/// Used as a partner class for {@link Block}s and {@link Item}s, via their special pair classes.
/// See the comment of {@link TagContainerBasic} for more insight as to the purpose of this class.
@ApiStatus.NonExtendable
public abstract class TagContainerMeta<InputType, ReturnType extends ObjMetaPair<InputType>> {
    protected final Map<String, SetPair<ReturnType>> revLookupTable;
    protected final InheritorContainer<ReturnType> inheritorContainer;
    protected final InputType taggable;

    protected final Int2ObjectOpenHashMap<Set<String>> tagTable = new Int2ObjectOpenHashMap<>();
    protected final Int2ObjectFunction<Set<String>> lookupContainer = new Int2ObjectFunction<>() {
        private final Int2ObjectOpenHashMap<Set<String>> lookups = new Int2ObjectOpenHashMap<>();
        private int lastAccessedMeta = Integer.MAX_VALUE;
        private Set<String> lastAccessedList;
        @Override
        public Set<String> get(int key) {
            if(key != lastAccessedMeta) {
                lastAccessedMeta = key;
                lastAccessedList = lookups.get(key);
            }
            return lastAccessedList;
        }

        @Override
        public Set<String> put(int key, Set<String> value) {
            return lookups.put(key, value);
        }

        @Override
        public void clear() {
            lastAccessedMeta = Integer.MAX_VALUE;
            lastAccessedList = null;
            lookups.clear();
        }
    };

    protected final Int2ObjectFunction<ReturnType> internFunction;

    @SuppressWarnings("unchecked")
    protected TagContainerMeta(@NonNull Map<String, SetPair<ReturnType>> revLookupTable,
                               @NonNull InheritorContainer<ReturnType> inheritorContainer,
                               @NonNull InputType blockOrItem) {
        this.revLookupTable = revLookupTable;
        this.inheritorContainer = inheritorContainer;
        this.taggable = blockOrItem;
        internFunction = blockOrItem instanceof Item item ? meta -> (ReturnType) ItemMetaPair.intern(item, meta)
            : blockOrItem instanceof Block block ? meta -> (ReturnType) BlockMetaPair.intern(block, meta) : null;
        if(internFunction == null) {
            throw new RuntimeException("internFunction was null!");
        }
    }

    public synchronized void addTags(int meta, String... tags) {
        MiscHelpers.enforceTagsSpec(tags);
        Collections.addAll(tagTable.computeIfAbsent(meta, o -> new ObjectOpenHashSet<>()), tags);

        // Maintain reverse lookup table
        for(String tag : tags) {
            revLookupTable.computeIfAbsent(tag, o -> new SetPair<>(new ObjectOpenHashSet<>())).getUnlocked().add(internFunction.get(meta));
        }

        clearCaches();
    }

    public synchronized void removeTags(int meta, String... tags) {
        MiscHelpers.enforceTagsSpec(tags);
        Set<String> set = tagTable.get(meta);
        set.removeIf(s -> ArrayUtils.contains(tags, s));
        if(set.isEmpty()) {
            tagTable.remove(meta);
        }

        for(String tag : tags) {
            SetPair<ReturnType> tagSet = revLookupTable.get(tag);
            tagSet.getUnlocked().remove(internFunction.get(meta));
            if(tagSet.getUnlocked().isEmpty()) {
                revLookupTable.remove(tag);
            }
        }

        clearCaches();
    }

    public synchronized Set<String> getTags(int meta) {
        Set<String> lookupResult = lookupContainer.get(meta);
        if(lookupResult != null) {
            return lookupResult;
        }

        Set<String> baseTags = getBaseTags(meta);
        Set<String> extraTags = meta == OreDictionary.WILDCARD_VALUE ? Collections.emptySet() : getWildcardTags();

        if(!baseTags.isEmpty() || !extraTags.isEmpty()) {
            if (extraTags != null) {
                Set<String> finalTags = new ObjectOpenHashSet<>(baseTags);
                finalTags.addAll(extraTags);

                for (String tag : finalTags) {
                    inheritorContainer.addInheritedRecursive(tag, finalTags);
                }

                lookupContainer.put(meta, Collections.unmodifiableSet(finalTags));
                return finalTags;
            }
        }

        return Collections.emptySet();
    }

    public Set<String> getBaseTags(int meta) {
        return tagTable.getOrDefault(meta, Collections.emptySet());
    }

    public Set<String> getWildcardTags() {
        return tagTable.getOrDefault(OreDictionary.WILDCARD_VALUE, Collections.emptySet());
    }

    public synchronized void clearCaches() {
        lookupContainer.clear();
    }
}
