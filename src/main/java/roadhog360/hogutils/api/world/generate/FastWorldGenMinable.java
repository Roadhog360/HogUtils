package roadhog360.hogutils.api.world.generate;

import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class FastWorldGenMinable extends WorldGenMinable {
    public FastWorldGenMinable(Block p_i45459_1_, int p_i45459_2_) {
        super(p_i45459_1_, p_i45459_2_);
    }

    public FastWorldGenMinable(Block p_i45460_1_, int p_i45460_2_, Block p_i45460_3_) {
        super(p_i45460_1_, p_i45460_2_, p_i45460_3_);
    }

    public FastWorldGenMinable(Block block, int meta, int number, Block target) {
        super(block, meta, number, target);
    }
}
