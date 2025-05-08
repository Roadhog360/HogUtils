package roadhog360.hogutils.mixins.early.baseblock;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockLeaves;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import roadhog360.hogutils.api.blocksanditems.block.ILeavesDecayRange;

@Mixin(BlockLeaves.class)
public class MixinBlockLeaves {
    @ModifyVariable(method = "updateTick", at = @At(value = "STORE"), ordinal = 0)
    private byte editLeafDecayVal(byte value,
                                  @Local(argsOnly = true) World world,
                                  @Local(argsOnly = true, ordinal = 0) int x,
                                  @Local(argsOnly = true, ordinal = 1) int y,
                                  @Local(argsOnly = true, ordinal = 2) int z,
                                  @Local(ordinal = 3) int meta) {
        if(this instanceof ILeavesDecayRange leaves) {
            return leaves.getDecayCheckRange(world, x, y, z, meta);
        }
        return value;
    }
}
