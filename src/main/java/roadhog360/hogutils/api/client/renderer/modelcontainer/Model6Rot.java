package roadhog360.hogutils.api.client.renderer.modelcontainer;

import com.gtnewhorizon.gtnhlib.client.model.ModelLoader;
import com.gtnewhorizon.gtnhlib.client.model.Variant;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import net.minecraft.util.ResourceLocation;

public class Model6Rot {

    /**
     * Use this to create JSON model rotatable in 6 directions - NSWE
     */

    public final QuadProvider[] models = new QuadProvider[6];
    private final Variant[] modelIds;

    public Model6Rot(ResourceLocation modelLoc) {

        this.modelIds = new Variant[] {
            new Variant(modelLoc, 0, 0, false),
            new Variant(modelLoc, 0, 180, false),
            new Variant(modelLoc, 0, 90, false),
            new Variant(modelLoc, 180, 90, false),
            new Variant(modelLoc, 90, 90, false),
            new Variant(modelLoc, 270, 90, false) };

        ModelLoader.registerModels(() -> loadModels(this), this.modelIds);
    }

    public static void loadModels(Model6Rot model) {
        for (int i = 0; i < 6; ++i) model.models[i] = ModelLoader.getModel(model.modelIds[i]);
    }
}
