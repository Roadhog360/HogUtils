package roadhog360.hogutils.mixins.late;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import roadhog360.hogutils.api.blocksanditems.utils.base.RegistryEntry;
import roadhog360.hogutils.api.utils.GenericUtils;

@Mixin(value = RegistryEntry.class, remap = false)
public class MixinRegistryEntrySanitization {
    @Shadow
    @Final
    protected String name;

    @WrapMethod(method = {"register"}, remap = false)
    private void sanitize(Operation<Void> original) {
        if (!GenericUtils.isLowerAlphanumeric(name)) {
            throw new IllegalArgumentException(
                "Don't register a non-alphanumeric name! Just because you can doesn't mean you should!" +
                    "Forge should prevent this, so I'm doing their work for them..." +
                    "If you want to use my helper tools, names that alphanumeric ONLY with underscores (_) and forward slashes (/) are allowed!"
            );
        }
    }
}
