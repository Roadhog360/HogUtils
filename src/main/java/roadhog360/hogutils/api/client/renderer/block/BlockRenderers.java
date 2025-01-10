package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.template.Model4Rot;
import cpw.mods.fml.client.registry.RenderingRegistry;

public final class BlockRenderers {
    private BlockRenderers() {}

    public static final RenderBetterDoor DOOR = new RenderBetterDoor();
    public static final RenderBetterTrapdoor TRAPDOOR = new RenderBetterTrapdoor();
    public static final RenderJSONBase LECTERN_TEST = new RenderJSONBasic("lectern");

    public static void RegisterRenderers() {
//        RenderingRegistry.registerBlockHandler(DOOR); //Not needed; it overrides the regular door renderer.
        RenderingRegistry.registerBlockHandler(TRAPDOOR);
        RenderingRegistry.registerBlockHandler(LECTERN_TEST);
    }
}
