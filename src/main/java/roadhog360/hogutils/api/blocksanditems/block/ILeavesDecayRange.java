package roadhog360.hogutils.api.blocksanditems.block;

import net.minecraft.world.World;

public interface ILeavesDecayRange {
    /// How far away should the leaves check for logs before decaying? Default 4
    byte getDecayCheckRange(World world, int x, int y, int z, int meta);
}
