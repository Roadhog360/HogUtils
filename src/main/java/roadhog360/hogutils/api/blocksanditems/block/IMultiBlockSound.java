package roadhog360.hogutils.api.blocksanditems.block;

import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/// One step sound not enough for your needs? You can implement this class and specify a different sound type based on meta, or other factors.
/// You can even provide a different sound for hitting, walking, breaking and placing.
public interface IMultiBlockSound {
    /// If this is null, does not run any override code
    @NonNull
    Block.SoundType getSoundType(World world, int x, int y, int z, SoundMode mode);

    enum SoundMode {
        BREAK,
        PLACE,
        WALK,
        HIT
    }
}
