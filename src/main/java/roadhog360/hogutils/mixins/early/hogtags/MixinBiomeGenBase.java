package roadhog360.hogutils.mixins.early.hogtags;

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.hogtags.helpers.BiomeTags;
import roadhog360.hogutils.api.hogtags.helpers.InheritorHelper;
import roadhog360.hogutils.api.hogtags.helpers.MiscHelpers;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;

import java.util.Collections;
import java.util.Set;

@Mixin(BiomeGenBase.class)
public class MixinBiomeGenBase implements ITaggable<BiomeGenBase> {
    private final Set<String> TAGS = new ObjectRBTreeSet<>();

    private Set<String> LOOKUP_CACHE;

    @Override
    public synchronized void addTags(String... tags) {
        MiscHelpers.checkTagsSpec(tags);
        Collections.addAll(TAGS, tags);

        clearCaches();
    }

    @Override
    public synchronized void removeTags(String... tags) {
        MiscHelpers.checkTagsSpec(tags);
        TAGS.removeIf(s -> ArrayUtils.contains(tags, s));

        clearCaches();
    }

    @Override
    public synchronized Set<String> getTags() {
        if(LOOKUP_CACHE != null) {
            return LOOKUP_CACHE;
        }

        if(!TAGS.isEmpty()) {
            LOOKUP_CACHE = new ObjectAVLTreeSet<>(TAGS);

            for(String tag : LOOKUP_CACHE) {
                InheritorHelper.addInheritedRecursive(tag, LOOKUP_CACHE, BiomeTags.INHERITOR_TABLE);
            }

            LOOKUP_CACHE = Collections.unmodifiableSet(LOOKUP_CACHE);
            return LOOKUP_CACHE;
        }

        return Collections.emptySet();
    }

    public synchronized void clearCaches() {
        LOOKUP_CACHE = null;
    }
}
