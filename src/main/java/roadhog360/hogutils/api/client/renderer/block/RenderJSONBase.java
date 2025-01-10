package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.Quad;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.util.DirectionUtil;
import com.gtnewhorizon.gtnhlib.util.ObjectPooler;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import roadhog360.hogutils.api.utils.FastRandom;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public abstract class RenderJSONBase extends RendererBase {

    protected final ThreadLocal<ObjectPooler<Quad>> quadPool = ThreadLocal.withInitial(() -> new ObjectPooler<>(Quad::new));
    protected static final FastRandom modelRand = new FastRandom();

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
        doRender(getInventoryModel(block, meta), block, meta, null, 0, 0, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        doRender(getInventoryModel(block, meta), block, world.getBlockMetadata(x, y, z), world, x, y, z);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    protected void doInventoryRender() {

    }

    protected void doRender(QuadProvider model, Block block, int meta, @Nullable IBlockAccess world, int x, int y, int z) {
        Random random = model instanceof World fullWorld ? fullWorld.rand : modelRand;
        Tessellator tesselator = Tessellator.instance;
        BlockPos pos = new BlockPos(x, y, z);
// I forget if JSON models are dynamic, you can hardcode this since they're all the same.
// You only need the check for a general renderer that doesn't know what kind of QuadProvider it's getting
        final Supplier<QuadView> sq = model.isDynamic() ? quadPool.get()::getInstance : null;

// This is packed as ABGR but the tesselator wants RGBA, gotta unpack
        final int color = world == null ? block.getRenderColor(meta) : block.colorMultiplier(world, x, y, z)/*model.getColor(world, pos, block, meta, rand)*/;
        final int r = color & 255; // just take the lower byte
// these three shift down by 1, 2, and 3 bytes respectively, then take the lowest byte
        final int g = color >> 8 & 255;
        final int b = color >> 16 & 255;
        final int a = color >> 24 & 255;

// ALL_DIRECTIONS comes from NHLib too - caches .values() to avoid allocating a bunch
        for (ForgeDirection dir : DirectionUtil.ALL_DIRECTIONS) {
            // Saves a little performance if you cull faces ASAP, although you'd have to write this yourself
            if (world != null && this.isCulled(world, pos, block, meta, dir)) continue;

            // iterates over the quads and dumps em into the tesselator, nothing special
            for (final QuadView quad : model.getQuads(world, pos, block, meta, dir, random, color, sq)) {
                for (int i = 0; i < 4; ++i) {
                    RenderBlocks.getInstance().setOverrideBlockTexture(Blocks.stone.getIcon(0,0));
                    tesselator.setColorRGBA(r, g, b, a);
                    float xf = quad.getX(i);
                    float yf = quad.getY(i);
                    float zf = quad.getZ(i);
                    tesselator.addVertexWithUV(xf, yf, zf, quad.getTexU(i), quad.getTexV(i));
                }
            }
        }
    }

    public abstract QuadProvider getInventoryModel(Block block, int meta);

    public abstract QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z);

    protected boolean isCulled(IBlockAccess world, BlockPos pos, Block block, int meta, ForgeDirection dir) {
        return block.shouldSideBeRendered(world, pos.getX(), pos.getY(), pos.getZ(), dir.ordinal());
    }

    protected ResourceLocation getResLoc(@Nullable String domain, @NonNull String loc) {
        return new ResourceLocation((domain != null ? domain + ":" : "") + "models/block/" + loc + ".json");
    }
}
