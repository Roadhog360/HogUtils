package roadhog360.hogutils.mixins.early.hogtags;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.blocksanditems.item.container.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.helpers.InheritorHelper;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;
import roadhog360.hogutils.api.hogtags.helpers.MiscHelpers;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableBlockItem;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Collections;
import java.util.Set;

@Mixin(Item.class)
public class MixinItem implements ITaggableBlockItem<ItemMetaPair> {
    private final Int2ObjectRBTreeMap<Set<String>> TAG_TABLE = new Int2ObjectRBTreeMap<>();
    private final Int2ObjectAVLTreeMap<Set<String>> LOOKUP_CACHE = new Int2ObjectAVLTreeMap<>();

    @Override
    public synchronized void addTags(int meta, String... tags) {
        MiscHelpers.enforceTagsSpec(tags);
        Collections.addAll(TAG_TABLE.computeIfAbsent(meta, o -> new ObjectRBTreeSet<>()), tags);

        // Maintain reverse lookup table
        for(String tag : tags) {
            ItemTags.REVERSE_LOOKUP_TABLE.computeIfAbsent(tag, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked()
                .add(ItemMetaPair.intern((Item) (Object) this, meta));
        }

        clearCaches();
    }

    @Override
    public synchronized void removeTags(int meta, String... tags) {
        MiscHelpers.enforceTagsSpec(tags);
        Set<String> set = TAG_TABLE.get(meta);
        set.removeIf(s -> ArrayUtils.contains(tags, s));
        if(set.isEmpty()) {
            TAG_TABLE.remove(meta);
        }

        for(String tag : tags) {
            SetPair<ItemMetaPair> tagSet = ItemTags.REVERSE_LOOKUP_TABLE.get(tag);
            tagSet.getUnlocked().remove(ItemMetaPair.intern((Item) (Object) this, meta));
            if(tagSet.getUnlocked().isEmpty()) {
                ItemTags.REVERSE_LOOKUP_TABLE.remove(tag);
            }
        }

        clearCaches();
    }

    @Override
    public synchronized Set<String> getTags(int meta) {
        Set<String> lookupResult = LOOKUP_CACHE.get(meta);
        if(lookupResult != null) {
            return lookupResult;
        }

        Set<String> baseTags = TAG_TABLE.getOrDefault(meta, Collections.emptySet());
        Set<String> extraTags = meta == OreDictionary.WILDCARD_VALUE ? Collections.emptySet()
            : TAG_TABLE.getOrDefault(OreDictionary.WILDCARD_VALUE, Collections.emptySet());

        if(!baseTags.isEmpty() || !extraTags.isEmpty()) {
            if (extraTags != null) {
                Set<String> finalTags = new ObjectAVLTreeSet<>();
                finalTags.addAll(baseTags);
                finalTags.addAll(extraTags);

                for (String tag : finalTags) {
                    InheritorHelper.addInheritedRecursive(tag, finalTags, ItemTags.INHERITOR_TABLE);
                }

                LOOKUP_CACHE.put(meta, Collections.unmodifiableSet(finalTags));
                return finalTags;
            }
        }

        return Collections.emptySet();
    }

    public synchronized void clearCaches() {
        LOOKUP_CACHE.clear();
    }
}
