package roadhog360.hogutils.mixins.early.hogtags;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roadhog360.hogutils.api.event.OreRegisterEventPre;

/// Handles the pre-register OreDict event as well as the auto-tagging logic
@Mixin(OreDictionary.class)
public class MixinOreDictionary {

    @Inject(method = "registerOreImpl", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/oredict/OreDictionary;getOreID(Ljava/lang/String;)I"), cancellable = true)
    private static void preRegisterEvent(String name, ItemStack ore, CallbackInfo ci) {
        Event event = new OreRegisterEventPre(name, ore);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled()) {
            ci.cancel();
        }
    }
}
