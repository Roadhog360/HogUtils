package roadhog360.hogutils.mixins.early.geninfo;

import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roadhog360.hogutils.api.world.IGeneratingCheck;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer implements IGeneratingCheck {
    final ThreadLocal<AtomicInteger> chunksGenerating = ThreadLocal.withInitial(() -> new AtomicInteger(0));

    @Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;func_150809_p()V"))
    private void pushGeneratingCheck(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_, CallbackInfo ci) {
        chunksGenerating.get().incrementAndGet();
    }

    @Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setChunkModified()V", shift = At.Shift.AFTER))
    private void popGeneratingCheck(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_, CallbackInfo ci) {
        chunksGenerating.get().decrementAndGet();
    }

    @Override
    public boolean hu$isGenerating() {
        return chunksGenerating.get().get() > 0;
    }
}
