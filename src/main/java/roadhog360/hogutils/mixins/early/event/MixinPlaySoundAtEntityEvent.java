package roadhog360.hogutils.mixins.early.event;

import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import roadhog360.hogutils.api.event.IUnfinalizedSoundEvent;

@Mixin(PlaySoundAtEntityEvent.class)
public class MixinPlaySoundAtEntityEvent implements IUnfinalizedSoundEvent {

    @Mutable
    @Final
    @Shadow(remap = false)
    public float volume;

    @Mutable
    @Final
    @Shadow(remap = false)
    public float pitch;

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
