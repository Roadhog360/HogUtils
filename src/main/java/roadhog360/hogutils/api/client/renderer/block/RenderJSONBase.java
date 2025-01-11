package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.client.model.GeometryHelper;
import com.gtnewhorizon.gtnhlib.client.model.NdQuadBuilder;
import com.gtnewhorizon.gtnhlib.client.model.NormalHelper;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.Axis;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.Quad;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.util.DirectionUtil;
import com.gtnewhorizon.gtnhlib.client.renderer.util.MathUtil;
import com.gtnewhorizon.gtnhlib.util.ObjectPooler;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import roadhog360.hogutils.api.utils.FastRandom;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public abstract class RenderJSONBase extends RendererBase {

    protected final ThreadLocal<ObjectPooler<Quad>> quadPool = ThreadLocal.withInitial(() -> new ObjectPooler<>(Quad::new));
    protected static final FastRandom modelRand = new FastRandom();

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glDisable(GL11.GL_LIGHTING);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        doRender(getInventoryModel(block, meta), block, meta, renderer, null, 0, 0, 0);
        tess.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        renderJSONInWorld(world, x, y, z, block, modelId, renderer, meta);
        return true;
    }

    /// Override this to add more render steps, render more than one model at once
    protected void renderJSONInWorld(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer, int meta) {
        doRender(getWorldModel(world, x, y, z, meta), block, meta, renderer, world, x, y, z);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    protected void doInventoryRender() {

    }

    protected void doRender(QuadProvider model, Block block, int meta, @Nullable RenderBlocks renderer,
                            @Nullable IBlockAccess world, int x, int y, int z) {
        Random random = world instanceof World fullWorld ? fullWorld.rand : modelRand;
        Tessellator tesselator = Tessellator.instance;
        BlockPos pos = new BlockPos(x, y, z);
        // I forget if JSON models are dynamic, you can hardcode this since they're all the same.
        // You only need the check for a general renderer that doesn't know what kind of QuadProvider it's getting
        final Supplier<QuadView> sq = model.isDynamic() ? quadPool.get()::getInstance : null;

        // This is packed as ABGR but the tesselator wants RGBA, gotta unpack
        final int color = world == null ? block.getRenderColor(meta) : model.getColor(world, pos, block, meta, random);
        final int r = color & 255; // just take the lower byte
        // these three shift down by 1, 2, and 3 bytes respectively, then take the lowest byte
        final int g = color >> 8 & 255;
        final int b = color >> 16 & 255;

        // ALL_DIRECTIONS comes from NHLib too - caches .values() to avoid allocating a bunch
        for (ForgeDirection dir : DirectionUtil.ALL_DIRECTIONS) {
            // Saves a little performance if you cull faces ASAP, although you'd have to write this yourself
            if (world != null && this.isCulled(world, pos, block, meta, dir)) continue;

            // TODO: Dir shading for UNKNOWN facing things
            float dirShading = world == null ? 1 : getDirShading(dir.ordinal());
            tesselator.setColorOpaque((int) (r * dirShading), (int) (g * dirShading), (int) (b * dirShading));
            if(world != null && dir != ForgeDirection.UNKNOWN) {
                tesselator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
                tesselator.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
            }

            // iterates over the quads and dumps em into the tesselator, nothing special
            for (final QuadView quad : model.getQuads(world, pos, block, meta, dir, random, color, sq)) {
                for (int i = 0; i < 4; ++i) {
                    int normal = quad.getNormal(i);
                    tesselator.addVertexWithUV(
                        getRenderX(x, quad, model, i),
                        getRenderY(y, quad, model, i),
                        getRenderZ(z, quad, model, i),
                        getU(quad, renderer, i), getV(quad, renderer, i));
                }
            }
        }
    }

    protected float getRenderX(float x, QuadView quad, QuadProvider model, int idx) {
        return quad.getX(idx) + x;
    }

    protected float getRenderY(float y, QuadView quad, QuadProvider model, int idx) {
        return quad.getY(idx) + y;
    }

    protected float getRenderZ(float z, QuadView quad, QuadProvider model, int idx) {
        return quad.getZ(idx) + z;
    }

    protected float getU(QuadView quad, RenderBlocks renderer, int idx) {
        if (renderer != null && renderer.hasOverrideBlockTexture()) {
            return idx <= 1 ? renderer.overrideBlockTexture.getMaxU() : renderer.overrideBlockTexture.getMinU();
        }

        return quad.getTexU(idx);
    }

    protected float getV(QuadView quad, RenderBlocks renderer, int idx) {
        if (renderer != null && renderer.hasOverrideBlockTexture()) {
            return idx == 0 || idx == 3 ? renderer.overrideBlockTexture.getMaxV() : renderer.overrideBlockTexture.getMinV();
        }

        return quad.getTexV(idx);
    }

    protected float getDirShading(int side) {
        return switch (side) {
            case 0 -> 0.5F;
            case 2, 3 -> 0.8F;
            case 4, 5 -> 0.6F;
            default -> 1;
        };
    }

    public abstract QuadProvider getInventoryModel(Block block, int meta);

    public abstract QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z, int meta);

    protected boolean isCulled(IBlockAccess world, BlockPos pos, Block block, int meta, ForgeDirection dir) {
        return dir != ForgeDirection.UNKNOWN && !block.shouldSideBeRendered(world, pos.getX(), pos.getY(), pos.getZ(), dir.ordinal());
    }
}
