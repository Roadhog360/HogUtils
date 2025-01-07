package roadhog360.hogutils.mixins.early.setblocksniper;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import roadhog360.hogutils.api.world.SetBlockSniper;

@Mixin(value = Chunk.class, priority = 0)
public class MixinChunk {

//    @WrapOperation(method = "<init>(Lnet/minecraft/world/World;[Lnet/minecraft/block/Block;II)V",
//        at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;func_150818_a(IIILnet/minecraft/block/Block;)V"))
//    private void snipeChunkCtorMetaless(ExtendedBlockStorage instance, int x, int y, int z,
//                                        Block blockIn, Operation<Void> original) {
//        SetBlockSniper.Sniper sniper = SetBlockSniper.getSniperFor(blockIn, 0);
//        if(sniper == null) {
//            original.call(instance, x, y, z, blockIn);
//        } else {
//            Block replacement = sniper.replaceBlock(blockIn, 0);
//            if(replacement != null) {
//                original.call(instance, x, y, z, replacement);
//                int replacementMeta = sniper.replaceMeta(blockIn, 0);
//                if (replacementMeta != 0) {
//                    instance.setExtBlockMetadata(x, y, z, replacementMeta);
//                }
//            }
//        }
//    }
//
//    @Inject(method = "<init>(Lnet/minecraft/world/World;[Lnet/minecraft/block/Block;[BII)V",
//        at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;func_150818_a(IIILnet/minecraft/block/Block;)V"))
//    private void snipeChunkCtor(World world, Block[] blockArray, byte[] metaArray, int chunkX, int chunkZ, CallbackInfo ci,
//                                @Local(ordinal = 6) LocalIntRef meta, @Local LocalRef<Block> block) {
//        Block blockIn = block.get();
//        int metaIn = meta.get();
//        SetBlockSniper.Sniper sniper = SetBlockSniper.getSniperFor(blockIn, metaIn);
//        if(sniper != null) {
//            Block replacement = sniper.replaceBlock(blockIn, metaIn);
//            if(replacement != null) {
//                block.set(replacement);
//                meta.set(sniper.replaceMeta(blockIn, metaIn));
//            }
//        }
//    }

    @Inject(method = "func_150807_a", at = @At(value = "HEAD"))
    private void snipeSetBlock(int x, int y, int z, Block blockIn, int metaIn, CallbackInfoReturnable<Boolean> cir,
                               @Local(argsOnly = true) LocalRef<Block> block, @Local(argsOnly = true, ordinal = 3) LocalIntRef meta) {
        SetBlockSniper.Sniper sniper = SetBlockSniper.getSniperFor((Chunk) (Object)this, x, y, z, blockIn, metaIn);
        if(sniper != null) {
            Block replacement = sniper.replaceBlock((Chunk) (Object)this, x, y, z, blockIn, metaIn);
            if(replacement != null) {
                block.set(replacement);
                meta.set(sniper.replaceMeta((Chunk) (Object)this, x, y, z, blockIn, metaIn));
            }
        }
    }
}
