package roadhog360.hogutils.api.utils.blocksanditems.block;

import net.minecraft.world.World;

public interface ICustomDoorSound {
    String getDoorSound(World world, int x, int y, int z);

    default float getDoorVolume(World world, int x, int y, int z) {
        return 1.0F;
    }

    default float getDoorPitch(World world, int x, int y, int z) {
        return 1.0F;
    }

    default void doDoorSounds(World world, int x, int y, int z) {
        world.playSound(x + 0.5F, y + 0.5F, z + 0.5F,
            this.getDoorSound(world, x, y, z),
            this.getDoorVolume(world, x, y, z),
            this.getDoorPitch(world, x, y, z), false);
    }
}
