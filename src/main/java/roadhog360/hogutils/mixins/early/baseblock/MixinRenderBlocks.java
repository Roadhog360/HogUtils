package roadhog360.hogutils.mixins.early.baseblock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {
    @Shadow
    public IBlockAccess blockAccess;

    @Redirect(method = "renderBlockByRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderBlocks;renderBlockDoor(Lnet/minecraft/block/Block;III)Z"))
    private boolean redirectDoorRendering(RenderBlocks instance, Block block, int x, int y, int z) {
        return BlockRenderers.DOOR.renderWorldBlock(blockAccess, x, y, z, block, BlockRenderers.DOOR.getRenderId(), instance);
    }
}
