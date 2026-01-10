package roadhog360.hogutils.handlers.event;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import roadhog360.hogutils.HogUtils;
import roadhog360.hogutils.api.event.BlockItemRegisterEvent;

@EventBusSubscriber
public class RegistryIterateEventHandler {

    @SubscribeEvent
    public static void iterateBlockRegistry(BlockItemRegisterEvent.BlockRegister.Init event) {
        HogUtils.registerTagDynamicBlock(event.objToRegister);
    }

    @SubscribeEvent
    public static void iterateBlockRegistry(BlockItemRegisterEvent.ItemRegister.Init event) {
        HogUtils.registerTagDynamicItem(event.objToRegister);
    }
}
