package roadhog360.hogutils.mixins.early.hogtags;

import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.hogtags.helpers.BiomeTags;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;

import java.util.Set;

@Mixin(BiomeGenBase.class)
public class MixinBiomeGenBase implements ITaggable {
    private final BiomeTags container = new BiomeTags((BiomeGenBase) (Object) this);

    @Override
    public synchronized void addTags(String... tags) {
        container.addTags(tags);
    }

    @Override
    public synchronized void removeTags(String... tags) {
        container.removeTags(tags);
    }

    @Override
    public synchronized Set<String> getTags() {
        return container.getTags();
    }

    public synchronized void clearCaches() {
        container.clearCaches();
    }
}
