package roadhog360.hogutils.proxy;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;

public class ClientProxy extends CommonProxy {

    @Override
    public void onConstructing(FMLConstructionEvent event) {
        super.onConstructing(event);

        BlockRenderers.RegisterRenderers();
    }
}
