package roadhog360.hogutils.api.blocksanditems.block;

import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/// One step sound not enough for your needs? Look no further! Here, you can specify a different block sound or even multiple at once!
public interface IMultiBlockSound {
    /// If this is null, does not run any override code
    /// TODO: At the moment, blocks placed may play the break sound instead of the place sound,
    /// if the block sound you're overriding doesn't have a unique placing sound.
    @NonNull
    Block.SoundType getSoundType(World world, int x, int y, int z, SoundMode type);

    enum SoundMode {
        BREAK,
        PLACE,
        WALK,
        HIT
    }
}
