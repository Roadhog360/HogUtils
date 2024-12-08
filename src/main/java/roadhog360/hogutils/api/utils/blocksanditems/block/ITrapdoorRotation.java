package roadhog360.hogutils.api.utils.blocksanditems.block;

import net.minecraft.world.IBlockAccess;

public interface ITrapdoorRotation {
    /// Should we rotate the faces based on the trapdoor's orientation?
    boolean rotateFaces(IBlockAccess world, int x, int y, int z);
}
