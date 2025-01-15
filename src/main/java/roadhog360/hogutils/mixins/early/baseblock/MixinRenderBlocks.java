package roadhog360.hogutils.mixins.early.baseblock;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {
    @Shadow
    public IBlockAccess blockAccess;

    @Redirect(method = "renderBlockByRenderType", remap = false,
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderBlockDoor(Lnet/minecraft/block/Block;III)Z"))
    private boolean redirectDoorRendering(RenderBlocks instance, Block block, int x, int y, int z) {
        return BlockRenderers.DOOR.renderWorldBlock(blockAccess, x, y, z, block, BlockRenderers.DOOR.getRenderId(), instance);
    }

    @Redirect(method = "renderBlockAsItem", remap = false,
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 7),
            to = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 8)),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderBlocks;getBlockIconFromSide(Lnet/minecraft/block/Block;I)Lnet/minecraft/util/IIcon;"))
    private IIcon redirectWrongIconAccesses(RenderBlocks instance, Block block, int side, @Local(argsOnly = true) int meta) {
        return instance.getBlockIconFromSideAndMetadata(block, side, meta);
    }
}
