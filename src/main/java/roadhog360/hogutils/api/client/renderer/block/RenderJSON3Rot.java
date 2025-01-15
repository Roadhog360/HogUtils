package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.ModelLoader;
import com.gtnewhorizon.gtnhlib.client.model.ModelVariant;
import com.gtnewhorizon.gtnhlib.client.model.template.Column3Rot;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class RenderJSON3Rot extends RenderJSONBase {

    private final int metaOffset;
    private QuadProvider itemModel;
    private final Column3Rot model;

    public RenderJSON3Rot(@Nullable String itemLoc, String blockLoc, int metaOffset) {
        if(itemLoc != null) {
            ModelVariant var = new ModelVariant(new ResourceLocation(itemLoc + ".json"), 0, 0, false);
            ModelLoader.registerModels(() -> itemModel = ModelLoader.getModel(var), var);
        }
        model = new Column3Rot(new ResourceLocation(blockLoc + ".json"));
        this.metaOffset = metaOffset;
    }

    public RenderJSON3Rot(@Nullable String itemLoc, String blockLoc) {
        this(itemLoc, blockLoc, 0);
    }

    @Override
    public QuadProvider getInventoryModel(Block block, int meta) {
        return itemModel;
    }

    @Override
    public QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z, int meta) {
        return model.models[meta + metaOffset % 3];
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return itemModel != null;
    }
}
