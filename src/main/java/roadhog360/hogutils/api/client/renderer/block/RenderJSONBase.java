package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.json.JsonModel;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.Quad;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.util.DirectionUtil;
import com.gtnewhorizon.gtnhlib.client.renderer.util.MathUtil;
import com.gtnewhorizon.gtnhlib.util.ObjectPooler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import roadhog360.hogutils.api.client.OpenGLHelper;
import roadhog360.hogutils.api.client.renderer.helper.AOHelper;
import roadhog360.hogutils.api.utils.FastRandom;
import roadhog360.hogutils.api.utils.MathUtils;

import java.util.Random;
import java.util.function.Supplier;

public abstract class RenderJSONBase implements IJSONBlockRenderingHandler {

    protected final ThreadLocal<ObjectPooler<Quad>> quadPool = ThreadLocal.withInitial(() -> new ObjectPooler<>(Quad::new));
    protected static final FastRandom modelRand = new FastRandom();

    protected final ThreadLocal<Vector3f> inventoryNormVec = ThreadLocal.withInitial(() -> new Vector3f(0, 0, 0));
    protected final ThreadLocal<Vector3f> worldNormVec = ThreadLocal.withInitial(() -> new Vector3f(0, 0, 0));

    private final int modelID;

    private final ThreadLocal<AOHelper> lightingHelper = ThreadLocal.withInitial(AOHelper::new);

    public RenderJSONBase() {
        modelID = RenderingRegistry.getNextAvailableRenderId();
    }

    public RenderJSONBase(int modelID) {
        this.modelID = modelID;
    }


    @Override
    public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
        final Tessellator tessellator = Tessellator.instance;
        if (block.getRenderBlockPass() == 1) {
            OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            OpenGLHelper.enableBlend();
        }
        OpenGLHelper.translate(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        doInventoryRender(getInventoryModel(block, meta), block, meta, modelID, renderer);
        tessellator.draw();

        OpenGLHelper.translate(0.5F, 0.5F, 0.5F);
        OpenGLHelper.disableBlend();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        doWorldRender(getWorldModel(world, x, y, z, meta), world, x, y, z, block, modelId, renderer, meta);
        return true;
    }

    /// Override this to add more render steps, and call the super again passing another {@link QuadProvider}, if you want to render more than one model at once.
    /// The model param is the one passed in by {@link #renderWorldBlock(IBlockAccess, int, int, int, Block, int, RenderBlocks)}
    protected void doWorldRender(QuadProvider model, @NotNull IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer, int meta) {
        Random random = world instanceof World fullWorld ? fullWorld.rand : modelRand;
        Tessellator tesselator = Tessellator.instance;
        // I forget if JSON models are dynamic, you can hardcode this since they're all the same.
        // You only need the check for a general renderer that doesn't know what kind of QuadProvider it's getting
        final Supplier<QuadView> sq = model.isDynamic() ? quadPool.get()::getInstance : null;

        int color = -1;

        // ALL_DIRECTIONS comes from NHLib too - caches .values() to avoid allocating a bunch
        for (ForgeDirection dir : DirectionUtil.ALL_DIRECTIONS) {
            // Saves a little performance if you cull faces ASAP, although you'd have to write this yourself
            if (dir != ForgeDirection.UNKNOWN && this.isCulled(world, x, y, z, block, meta, dir)) continue;

            // iterates over the quads and dumps em into the tesselator, nothing special
            for (final QuadView quad : model.getQuads(world, x, y, z, block, meta, dir, random, color, sq)) {
                tesselator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

                if(quad.getColorIndex() != -1) {
                    color = block.getRenderColor(meta);
                }

                final int r = color & 255;
                final int g = color >> 8 & 255;
                final int b = color >> 16 & 255;

                if(Minecraft.isAmbientOcclusionEnabled() && (!(model instanceof JsonModel jsonModel) || jsonModel.isUseAO())) {
                    computeUnknownFaceNormal(worldNormVec.get(), quad);

                    AOHelper aoHelper = lightingHelper.get();
                    if(!quad.isShade()) {
                        aoHelper.setLightnessOverride(1);
                    }
                    aoHelper.setRenderBlocks(renderer);
                    ForgeDirection renderDir = getClosestDir(worldNormVec.get(), quad, false);
                    if(dir == ForgeDirection.UNKNOWN) {
                        renderer.setRenderBounds(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
                    } else {
                        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
                    }
                    aoHelper.setupLighting(block,
                        MathHelper.floor_double(x),
                        MathHelper.floor_double(y),
                        MathHelper.floor_double(z), renderDir).setupColor(renderDir, color);
                    aoHelper.clearLightnessOverride();

                    renderQuadAO(model, quad, x, y, z, tesselator, renderer, renderDir, null);
                } else {
                    if (quad.isShade()) {
                        ForgeDirection renderDir = dir;

                        if (dir == ForgeDirection.UNKNOWN) {
                            /// We don't have to do special dir shading for world rendering; modern always simply gets the shading for the closest dir.
                            computeUnknownFaceNormal(worldNormVec.get(), quad);
                            renderDir = getClosestDir(worldNormVec.get(), quad, false);
                        }

                        float dirShading = AOHelper.getDirShading(renderDir);
                        tesselator.setColorOpaque((int) (r * dirShading), (int) (g * dirShading), (int) (b * dirShading));
                    } else {
                        tesselator.setColorOpaque(r, g, b);
                    }
                    renderQuad(model, quad, x, y, z, tesselator, null);
                }
            }
        }
    }


    protected final void renderQuadAO(QuadProvider model, QuadView quad, float x, float y, float z, Tessellator tessellator,
                                          RenderBlocks renderer, ForgeDirection quadFacing, @Nullable IIcon overrideIcon) {
        // DOWN, SOUTH = dir + 0;
        // EAST = dir + 1
        // UP = dir + 2
        // NORTH, WEST = dir + 3
        int offsetAO = switch(quadFacing) {
            case DOWN, SOUTH, UNKNOWN -> 0;
            case EAST -> 3;
            case UP -> 2;
            case NORTH, WEST -> 1;
        };
        for (int i = 0; i < 4; ++i) {
            switch ((i + offsetAO) & 3) {
                case 0 -> topLeftAOSetup(tessellator, renderer);
                case 1 -> bottomLeftAOSetup(tessellator, renderer);
                case 2 -> bottomRightAOSetup(tessellator, renderer);
                case 3 -> topRightAOSetup(tessellator, renderer);
            }
            tessellator.addVertexWithUV(
                getRenderX(x, quad, model, i),
                getRenderY(y, quad, model, i),
                getRenderZ(z, quad, model, i),
                getU(quad, overrideIcon, i), getV(quad, overrideIcon, i));
        }
    }

    protected void topLeftAOSetup(Tessellator tessellator, RenderBlocks renderer) {
        tessellator.setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
        tessellator.setBrightness(renderer.brightnessTopLeft);
    }

    protected void bottomLeftAOSetup(Tessellator tessellator, RenderBlocks renderer) {
        tessellator.setColorOpaque_F(renderer.colorRedBottomLeft, renderer.colorGreenBottomLeft, renderer.colorBlueBottomLeft);
        tessellator.setBrightness(renderer.brightnessBottomLeft);
    }

    protected void topRightAOSetup(Tessellator tessellator, RenderBlocks renderer) {
        tessellator.setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
        tessellator.setBrightness(renderer.brightnessTopRight);
    }

    protected void bottomRightAOSetup(Tessellator tessellator, RenderBlocks renderer) {
        tessellator.setColorOpaque_F(renderer.colorRedBottomRight, renderer.colorGreenBottomRight, renderer.colorBlueBottomRight);
        tessellator.setBrightness(renderer.brightnessBottomRight);
    }


    protected void renderQuad(QuadProvider model, QuadView quad, float x, float y, float z, Tessellator tessellator,
                            @Nullable IIcon overrideIcon) {
        for (int i = 0; i < 4; ++i) {
            tessellator.addVertexWithUV(
                getRenderX(x, quad, model, i),
                getRenderY(y, quad, model, i),
                getRenderZ(z, quad, model, i),
                getU(quad, overrideIcon, i), getV(quad, overrideIcon, i));
        }
    }

    /// Override this to add more render steps, and call the super again passing another {@link QuadProvider}, if you want to render more than one model at once.
    /// The model param is the one passed in by {@link #renderInventoryBlock}
    protected void doInventoryRender(QuadProvider model, Block block, int meta, int modelID, RenderBlocks renderer) {
        Random random = modelRand;
        Tessellator tesselator = Tessellator.instance;
        // I forget if JSON models are dynamic, you can hardcode this since they're all the same.
        // You only need the check for a general renderer that doesn't know what kind of QuadProvider it's getting
        final Supplier<QuadView> sq = model.isDynamic() ? quadPool.get()::getInstance : null;

        int color = -1;

        for (ForgeDirection dir : DirectionUtil.ALL_DIRECTIONS) {
            // iterates over the quads and dumps em into the tesselator, nothing special
            for (final QuadView quad : model.getQuads(null, 0, 0, 0, block, meta, dir, random, color, sq)) {

                ForgeDirection renderDir = dir;

                if(dir == ForgeDirection.UNKNOWN) {
                    computeUnknownFaceNormal(inventoryNormVec.get(), quad);
                    renderDir = getClosestDir(inventoryNormVec.get(), quad, true);
                }

                if(renderDir == ForgeDirection.UNKNOWN) {
                    // If still unknown, we'll use the vec3f to determine the normals
                    // We already did the normal calc earlier.
                    Vector3f normVec = inventoryNormVec.get();
                    tesselator.setNormal(normVec.x, normVec.y, normVec.z);
                } else {
                    tesselator.setNormal(renderDir.offsetX, renderDir.offsetY, renderDir.offsetZ);
                }

                if(quad.getColorIndex() != -1) {
                    color = block.getRenderColor(meta);
                }

                final int r = color & 255;
                final int g = color >> 8 & 255;
                final int b = color >> 16 & 255;

                tesselator.setColorOpaque(r, g, b);

                renderQuad(model, quad, 0, 0, 0, tesselator, renderer.overrideBlockTexture);
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

    /// TODO: Override icons aren't mapped properly right now; we need to do that.
    protected float getU(QuadView quad, IIcon icon, int idx) {
        if (icon != null) {
            float relative = quad.getTexU(0) - quad.getTexU(2);
//            relative *= (icon.getMaxU() - icon.getMinU());
            return idx == 0 || idx == 1 ? relative + icon.getMinU() : icon.getMaxU() - relative;
        }

        return quad.getTexU(idx);
    }

    /// TODO: Override icons aren't mapped properly right now; we need to do that.
    protected float getV(QuadView quad, IIcon icon, int idx) {
        if (icon != null) {
            float relative = quad.getTexV(0) - quad.getTexV(2);
//            relative *= (icon.getMaxV() - icon.getMinV());
            return idx == 1 || idx == 2 ? relative + icon.getMinV() : icon.getMaxV() - relative;
        }

        return quad.getTexV(idx);
    }

    protected void doTransformations() {

    }

    protected void computeUnknownFaceNormal(Vector3f saveVec, QuadView q) {
        ForgeDirection nominalFace = q.getFace();
        if (nominalFace != ForgeDirection.UNKNOWN) {
            Vector3i vec = DirectionUtil.STEP[nominalFace.ordinal()];
            saveVec.set(vec.x, vec.y, vec.z);
        } else {
            float x0 = q.getX(0);
            float y0 = q.getY(0);
            float z0 = q.getZ(0);
            float x1 = q.getX(1);
            float y1 = q.getY(1);
            float z1 = q.getZ(1);
            float x2 = q.getX(2);
            float y2 = q.getY(2);
            float z2 = q.getZ(2);
            float x3 = q.getX(3);
            float y3 = q.getY(3);
            float z3 = q.getZ(3);
            float dx0 = x2 - x0;
            float dy0 = y2 - y0;
            float dz0 = z2 - z0;
            float dx1 = x3 - x1;
            float dy1 = y3 - y1;
            float dz1 = z3 - z1;
            float normX = dy0 * dz1 - dz0 * dy1;
            float normY = dz0 * dx1 - dx0 * dz1;
            float normZ = dx0 * dy1 - dy0 * dx1;
            float l = (float) Math.sqrt(normX * normX + normY * normY + normZ * normZ);
            if (l != 0.0F) {
                normX /= l;
                normY /= l;
                normZ /= l;
            }

            saveVec.set(normX, normY, normZ);
        }
    }

    protected ForgeDirection getClosestDir(Vector3f check, QuadView q, boolean strict) {
        float normX = check.x;
        float normY = check.y;
        float normZ = check.z;

        // Logic for finding the closest facing dir.
        // Prioritizes returning the X coord dirs first, then Z, then Y
        // This is so if any faces happen to have matching coordinates on more than one plane, there's a predictable pattern.

        if (strict) {
            // If it lines up with a cardinal direction (one axis is 0 while the others are 1) then return the direction.
            if (MathUtil.fuzzy_eq(Math.abs(normX), 1) && MathUtil.fuzzy_eq(normY, 0) && MathUtil.fuzzy_eq(normZ, 0)) {
                return normX < 0 ? ForgeDirection.WEST : ForgeDirection.EAST;
            }
            if (MathUtil.fuzzy_eq(Math.abs(normZ), 1) && MathUtil.fuzzy_eq(normY, 0) && MathUtil.fuzzy_eq(normX, 0)) {
                return normZ < 0 ? ForgeDirection.NORTH : ForgeDirection.SOUTH;
            }
            if (MathUtil.fuzzy_eq(Math.abs(normY), 1) && MathUtil.fuzzy_eq(normX, 0) && MathUtil.fuzzy_eq(normZ, 0)) {
                return normY < 0 ? ForgeDirection.DOWN : ForgeDirection.UP;
            }
            // If no cardinal direction was matched, return UNKNOWN still if we don't want to find the closest face.
            return ForgeDirection.UNKNOWN;
        } else {
            // Else always returns a valid dir, it gets the vec3f from the normal, and returns the closest facing.
            float xAbs = Math.abs(normX);
            float yAbs = Math.abs(normY);
            float zAbs = Math.abs(normZ);

            float largest = MathUtils.max(xAbs, yAbs, zAbs);

            if (largest == xAbs) {
                return normX < 0 ? ForgeDirection.WEST : ForgeDirection.EAST;
            } else if (largest == zAbs) {
                return normZ < 0 ? ForgeDirection.NORTH : ForgeDirection.SOUTH;
            } else {
                return normY < 0 ? ForgeDirection.DOWN : ForgeDirection.UP;
            }
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    protected boolean isCulled(IBlockAccess world, int x, int y, int z, Block block, int meta, ForgeDirection dir) {
        return !block.shouldSideBeRendered(world, x, y, z, dir.ordinal());
    }

    @Override
    public int getRenderId() {
        return modelID;
    }
}
