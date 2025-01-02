package roadhog360.hogutils.api.blocksanditems.block;

import it.unimi.dsi.fastutil.ints.Int2FloatArrayMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.Map;

public class BlockHardnessHelper {

    private final Map<Integer, Float> hardnesses = new Int2FloatArrayMap();
    private final Map<Integer, Float> resistances = new Int2FloatArrayMap();
    private final Block theBlock;

    public BlockHardnessHelper(Block theBlock) {
        this.theBlock = theBlock;
    }

    public float getBlockHardness(World worldIn, int x, int y, int z) {
        return hardnesses.getOrDefault(worldIn.getBlockMetadata(x, y, z), theBlock.blockHardness);
    }

    public void setHardnessValues(float hardness, int... metas) {
        if (metas.length == 0) {
            theBlock.setHardness(hardness);
        } else for (int meta : metas) {
            hardnesses.put(meta, hardness);
        }
    }

    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        return resistances.getOrDefault(world.getBlockMetadata(x, y, z), theBlock.blockResistance) / 5.0F;
    }

    public void setResistanceValues(float resistance, int... metas) {
        if (metas.length == 0) {
            theBlock.setResistance(resistance);
        } else for (int meta : metas) {
            resistances.put(meta, resistance);
        }
    }
}
