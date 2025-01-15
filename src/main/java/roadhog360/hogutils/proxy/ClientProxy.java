package roadhog360.hogutils.proxy;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.common.MinecraftForge;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;
import roadhog360.hogutils.handlers.event.HogTagsDisplayEventHandler;
import roadhog360.hogutils.handlers.event.MultiBlockSoundEventHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public void onConstructing(FMLConstructionEvent event) {
        super.onConstructing(event);

        MinecraftForge.EVENT_BUS.register(HogTagsDisplayEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MultiBlockSoundEventHandler.INSTANCE);

        BlockRenderers.registerRenderers();
    }
}
