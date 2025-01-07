package roadhog360.hogutils.api.client.renderer.block;

import cpw.mods.fml.client.registry.RenderingRegistry;

public final class BlockRenderers {
    private BlockRenderers() {}

    public static final RenderBetterDoor DOOR = new RenderBetterDoor();
    public static final RenderBetterTrapdoor TRAPDOOR = new RenderBetterTrapdoor();

    public static void RegisterRenderers() {
//        RenderingRegistry.registerBlockHandler(DOOR); //Not needed; it overrides the regular door renderer.
        RenderingRegistry.registerBlockHandler(TRAPDOOR);
    }
}
