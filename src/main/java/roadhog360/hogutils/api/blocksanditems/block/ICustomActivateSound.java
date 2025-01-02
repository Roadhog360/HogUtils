package roadhog360.hogutils.api.blocksanditems.block;

import lombok.NonNull;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockButton;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/// Currently supports doors, trapdoors, fence gates, buttons, pressure plates, dispensers/droppers, chests, and ender chests.
/// When this interface is applied to your block, it will change the sound that is played when it is activated.
public interface ICustomActivateSound {
    /// @param world
    /// @param x
    /// @param y
    /// @param z
    /// @param prevSound The previous sound that you are overriding
    /// @return The sound to override the old one with. You do not need to specify things like ".open" or ".close" for doors; that is done for you.
    /// To override that behavior, override {@link ICustomActivateSound#getSuffix(World, int, int, int, String)} and inject your suffixing behavior.
    @Nullable
    String getSound(World world, int x, int y, int z, String prevSound);

    /// @param world
    /// @param x
    /// @param y
    /// @param z
    /// @param prevSound The previous sound that you are overriding
    /// @return The suffix to add to {@link ICustomActivateSound#getSound(World, int, int, int, String)}.
    /// Don't forget to add the dot at the start!
    @NonNull
    default String getSuffix(World world, int x, int y, int z, String prevSound) {
        if (prevSound.contains("random.door") || prevSound.contains("random.chest")) {
            if(prevSound.endsWith("open")) {
                return ".open";
            }
            if(prevSound.endsWith("close")) {
                return ".close";
            }
        } else if (this instanceof BlockButton) {
            int meta = world.getBlockMetadata(x, y, z);
            return ".click" + ((meta & 8) == 0 ? ".off" : ".on");
        } else if (this instanceof BlockBasePressurePlate) {
            int meta = world.getBlockMetadata(x, y, z);
                return ".click" + (meta == 0 ? ".off" : ".on");
        }
        return "";
    }

    default float getVolume(World world, int x, int y, int z, String prevSound, float prevVolume) {
        return prevVolume;
    }

    default float getPitch(World world, int x, int y, int z, String prevSound, float prevPitch) {
        return prevPitch;
    }
}
