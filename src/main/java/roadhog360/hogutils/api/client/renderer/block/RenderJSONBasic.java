package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.client.model.ModelLoader;
import com.gtnewhorizon.gtnhlib.client.model.Variant;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.Quad;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizon.gtnhlib.util.Callback;
import com.gtnewhorizon.gtnhlib.util.ObjectPooler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.utils.FastRandom;

public class RenderJSONBasic extends RenderJSONBase {

    protected final Variant var;
    protected QuadProvider model;
    protected final ObjectPooler<Quad> quadPool = new ObjectPooler<>(Quad::new);
    protected static final FastRandom rand = new FastRandom();

    public RenderJSONBasic(String domain, String loc) {
        this.var = new Variant(getResLoc(domain, loc), 0, 0, false);

        ModelLoader.registerModels(() -> model = ModelLoader.getModel(var), var);

    }

    public RenderJSONBasic(String loc) {
        this(null, loc);
    }

    public QuadProvider getInventoryModel(Block block, int meta) {
        return model;
    }

    public QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z) {
        return model;
    }
}
