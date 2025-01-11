package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.ModelLoader;
import com.gtnewhorizon.gtnhlib.client.model.Variant;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.Quad;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizon.gtnhlib.util.ObjectPooler;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import roadhog360.hogutils.api.utils.FastRandom;

import javax.annotation.Nullable;

public class RenderJSONBasic extends RenderJSONBase {

    protected QuadProvider modelBlock;
    protected QuadProvider modelItem;

    public RenderJSONBasic(@Nullable String itemLoc, String blockLoc) {
        if(itemLoc != null) {
            Variant var = new Variant(new ResourceLocation(itemLoc + ".json"), 0, 0, false);
            ModelLoader.registerModels(() -> modelItem = ModelLoader.getModel(var), var);
        }
        Variant var = new Variant(new ResourceLocation(blockLoc + ".json"), 0, 0, false);
        ModelLoader.registerModels(() -> modelBlock = ModelLoader.getModel(var), var);

    }

    public RenderJSONBasic(String loc) {
        this(null, loc);
    }

    public QuadProvider getInventoryModel(Block block, int meta) {
        return modelItem;
    }

    public QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z, int meta) {
        return modelBlock;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return modelItem != null;
    }
}
