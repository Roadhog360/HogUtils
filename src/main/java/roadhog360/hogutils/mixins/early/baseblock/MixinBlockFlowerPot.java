package roadhog360.hogutils.mixins.early.baseblock;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import roadhog360.hogutils.api.blocksanditems.block.IPotableData;

@Mixin(BlockFlowerPot.class)
public class MixinBlockFlowerPot {
    @ModifyReturnValue(method = "func_149928_a", at = @At(value = "RETURN"))
    private boolean isCustomFlower(boolean original, @Local(argsOnly = true) Block block, @Local(argsOnly = true) int meta) {
        if(original) {
            return true;
        }
        if(block instanceof IPotableData flower) {
            return flower.isPotable(meta);
        }
        return false;
    }
}
