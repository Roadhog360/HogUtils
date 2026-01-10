package roadhog360.hogutils.api.event;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/// Called at various points during initialization, to prevent you from needing to create your own registry iterators.
/// Be sure to register this in your {@link FMLConstructionEvent} or you may miss the OnRegister event.
///
/// This event is NOT {@link Cancelable}
public class BlockItemRegisterEvent<T> extends Event {
    public final T objToRegister;
    public final String namespaceID;

    private BlockItemRegisterEvent(T obj, String name) {
        this.objToRegister = obj;
        this.namespaceID = name;
    }

    public static class BlockRegister {
        /// Called from an iterator in HogUtils sometime during {@link FMLInitializationEvent}
        public static class Init extends BlockItemRegisterEvent<Block> {
            public Init(Block obj, String name) {
                super(obj, name);
            }
        }

        /// Called from an iterator in HogUtils sometime during {@link FMLPostInitializationEvent}
        public static class PostInit extends BlockItemRegisterEvent<Block> {
            public PostInit(Block obj, String name) {
                super(obj, name);
            }
        }

        /// Called from an iterator in HogUtils sometime during {@link FMLLoadCompleteEvent}
        public static class LoadComplete extends BlockItemRegisterEvent<Block> {
            public LoadComplete(Block obj, String name) {
                super(obj, name);
            }
        }
    }

    public static class ItemRegister {
        /// Called from an iterator in HogUtils sometime during {@link FMLInitializationEvent}
        public static class Init extends BlockItemRegisterEvent<Item> {
            public Init(Item obj, String name) {
                super(obj, name);
            }
        }

        /// Called from an iterator in HogUtils sometime during {@link FMLPostInitializationEvent}
        public static class PostInit extends BlockItemRegisterEvent<Item> {
            public PostInit(Item obj, String name) {
                super(obj, name);
            }
        }

        /// Called from an iterator in HogUtils sometime during {@link FMLLoadCompleteEvent}
        public static class LoadComplete extends BlockItemRegisterEvent<Item> {
            public LoadComplete(Item obj, String name) {
                super(obj, name);
            }
        }
    }
}
