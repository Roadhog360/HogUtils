package roadhog360.hogutils.proxy;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.common.MinecraftForge;
import roadhog360.hogutils.handlers.event.ClientEventHandler;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.


    @Override
    public void onConstructing(FMLConstructionEvent event) {
        super.onConstructing(event);
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
    }
}
