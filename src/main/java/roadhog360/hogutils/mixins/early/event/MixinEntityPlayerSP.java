package roadhog360.hogutils.mixins.early.event;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Inject(method = "playSound", at = @At(value = "FIELD", target = "Lnet/minecraftforge/event/entity/PlaySoundAtEntityEvent;name:Ljava/lang/String;"))
    private void useEventVolumeAndPitch(String name, float v, float p, CallbackInfo ci,
                                        @Local(argsOnly = true, ordinal = 0) LocalFloatRef volume,
                                        @Local(argsOnly = true, ordinal = 1) LocalFloatRef pitch,
                                        @Local PlaySoundAtEntityEvent event) {
        volume.set(event.volume);
        pitch.set(event.pitch);
    }
}
