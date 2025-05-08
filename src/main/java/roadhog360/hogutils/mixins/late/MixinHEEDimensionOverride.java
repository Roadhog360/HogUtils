package roadhog360.hogutils.mixins.late;

import chylex.hee.world.DimensionOverride;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roadhog360.hogutils.HogUtils;

@Mixin(value = DimensionOverride.class, remap = false)
public class MixinHEEDimensionOverride {
    private static BiomeGenBase prevBiome;

    /// Kneecaps the HEE DRM that prevents the end dimension provider from being changed, by simply tricking it into thinking the end has already been set to {@link BiomeGenHardcoreEnd}.
    @WrapOperation(method = "postInit", at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/BiomeGenBase;field_76779_k:Lnet/minecraft/world/biome/BiomeGenBase;", ordinal = 0), remap = false)
    private static BiomeGenBase disarmInstanceOf(Operation<BiomeGenBase> original) {
        BiomeGenBase endBiome = BiomeGenBase.getBiome(9);
        if(endBiome instanceof BiomeGenHardcoreEnd) {
            return endBiome; // Don't run extra code
        } else {
            prevBiome = endBiome; // Store the previous value of getBiome(9)
            // We need this for later since instantiating a biome sets it into the biome array automatically.
            return new BiomeGenHardcoreEnd(9); // Return the dummy biome
        }
    }

    /// Make sure we clean up after the previous code.
    @Inject(method = "postInit", at = @At(value = "INVOKE", target = "Lchylex/hee/world/biome/BiomeGenHardcoreEnd;overrideMobLists()V", shift = At.Shift.AFTER), remap = false)
    private static void restoreCorrectBiome(CallbackInfo ci) {
        if(prevBiome != null) {
            BiomeGenBase.getBiomeGenArray()[9] = prevBiome; // Restore the previous biome value, since creating a new biome instance changed this.
        }
    }

    @WrapMethod(method = "verifyIntegrity")
    private static void nullifyDRM3(Operation<Void> original) {
        try {
            original.call();
        } catch (Exception dontGiveAShit) {
            HogUtils.LOG.warn("HEE end provider integrity check disarmed. If the end looks wrong, report it to Roadhog360, NOT GTNH");
        }
    }
}
