package roadhog360.hogutils.api.client.renderer.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import roadhog360.hogutils.api.client.OpenGLHelper;

public abstract class RendererBase implements ISimpleBlockRenderingHandler {

    private final int renderID = RenderingRegistry.getNextAvailableRenderId();


    @Override
    public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
        final Tessellator tessellator = Tessellator.instance;
        if (block.getRenderBlockPass() == 1) {
            OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            OpenGLHelper.enableBlend();
        }
        OpenGLHelper.translate(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        renderInventoryModel(block, meta, modelID, renderer, block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
        tessellator.draw();

        OpenGLHelper.translate(0.5F, 0.5F, 0.5F);
        OpenGLHelper.disableBlend();
    }

    protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        renderStandardInventoryCube(block, meta, modelId, renderer, block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
    }

    protected void renderStandardInventoryCube(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        final Tessellator tessellator = Tessellator.instance;
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
    }

    @Override
    public int getRenderId() {
        return renderID;
    }
}
