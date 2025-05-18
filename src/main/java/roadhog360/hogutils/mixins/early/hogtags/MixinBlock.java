package roadhog360.hogutils.mixins.early.hogtags;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggableMeta;

import java.util.Set;

@Mixin(Block.class)
public class MixinBlock implements ITaggableMeta {

    private final BlockTags container = new BlockTags((Block) (Object) this);

    @Override
    public synchronized void addTags(int meta, String... tags) {
        container.addTags(meta, tags);
    }

    @Override
    public synchronized void removeTags(int meta, String... tags) {
        container.removeTags(meta, tags);
    }

    @Override
    public synchronized Set<String> getTags(int meta) {
        return container.getTags(meta);
    }

    public synchronized void clearCaches() {
        container.clearCaches();
    }
}
