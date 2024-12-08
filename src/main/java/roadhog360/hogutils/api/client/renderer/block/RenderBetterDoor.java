package roadhog360.hogutils.api.client.renderer.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class RenderBetterDoor extends RendererBase {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);

        if ((meta & 8) != 0) {
            if (world.getBlock(x, y - 1, z) != block) {
                return false;
            }
        } else if (world.getBlock(x, y + 1, z) != block) {
            return false;
        }

        if ((meta & 8) != 0) {
            renderer.uvRotateTop = meta % 6;
        }
        boolean flag = renderer.renderStandardBlock(block, x, y, z);
        renderer.uvRotateTop = 0;
        return flag;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

}
