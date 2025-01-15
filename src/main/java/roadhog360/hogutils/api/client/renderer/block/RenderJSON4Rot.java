package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.ModelLoader;
import com.gtnewhorizon.gtnhlib.client.model.ModelVariant;
import com.gtnewhorizon.gtnhlib.client.model.template.Model4Rot;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

@ThreadSafeISBRH(perThread = false)
public class RenderJSON4Rot extends RenderJSONBase {

    private final int metaOffset;
    private QuadProvider itemModel;
    private final Model4Rot model;

    public RenderJSON4Rot(@Nullable String itemLoc, String blockLoc, int metaOffset) {
        if(itemLoc != null) {
            ModelVariant var = new ModelVariant(new ResourceLocation(itemLoc + ".json"), 0, 0, false);
            ModelLoader.registerModels(() -> itemModel = ModelLoader.getModel(var), var);
        }
        model = new Model4Rot(new ResourceLocation(blockLoc + ".json"));
        this.metaOffset = metaOffset;
    }

    public RenderJSON4Rot(@Nullable String itemLoc, String blockLoc) {
        this(itemLoc, blockLoc, 0);
    }

    @Override
    public QuadProvider getInventoryModel(Block block, int meta) {
        return itemModel;
    }

    @Override
    public QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z, int meta) {
        return model.models[meta + metaOffset % 4];
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return itemModel != null;
    }
}
