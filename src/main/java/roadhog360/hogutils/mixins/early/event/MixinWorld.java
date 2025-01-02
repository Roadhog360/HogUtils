package roadhog360.hogutils.mixins.early.event;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class MixinWorld implements IBlockAccess {

    @Inject(method = "playSoundAtEntity", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private void useEventVolAndPit(Entity p_72956_1_, String p_72956_2_, float p_72956_3_, float p_72956_4_, CallbackInfo ci,
                                   @Local(argsOnly = true, ordinal = 0) LocalFloatRef volume,
                                   @Local(argsOnly = true, ordinal = 1) LocalFloatRef pitch,
                                   @Local PlaySoundAtEntityEvent event) {
        volume.set(event.volume);
        pitch.set(event.pitch);
    }
}
