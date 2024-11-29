package roadhog360.hogutils.handlers.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import roadhog360.hogutils.HogUtils;
import roadhog360.hogutils.api.event.BlockItemIterateEvent;

public class RegistryIterateEventHandler {
    private RegistryIterateEventHandler() {}
    public static final Object INSTANCE = new RegistryIterateEventHandler();

    @SubscribeEvent
    public void iterateBlockRegistry(BlockItemIterateEvent.BlockRegister.Init event) {
        HogUtils.registerTagDynamicBlock(event.objToRegister);
    }

    @SubscribeEvent
    public void iterateBlockRegistry(BlockItemIterateEvent.ItemRegister.Init event) {
        HogUtils.registerTagDynamicItem(event.objToRegister);
    }
}
