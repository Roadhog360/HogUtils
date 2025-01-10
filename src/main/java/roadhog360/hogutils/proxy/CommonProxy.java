package roadhog360.hogutils.proxy;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import me.mrnavastar.r.R;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.block.BaseBlock;
import roadhog360.hogutils.api.client.renderer.block.BlockRenderers;
import roadhog360.hogutils.api.event.BlockItemIterateEvent;
import roadhog360.hogutils.api.utils.RecipeHelper;
import roadhog360.hogutils.handlers.event.RegistryIterateEventHandler;

import java.util.Map;

public class CommonProxy {

    @SuppressWarnings("unchecked")
    Map<String, Block> mapBlocks = (Map<String, Block>) R.of(Block.blockRegistry).get("registryObjects", Map.class);
    @SuppressWarnings("unchecked")
    Map<String, Item> mapItems = (Map<String, Item>) R.of(Item.itemRegistry).get("registryObjects", Map.class);

    public void onConstructing(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(RegistryIterateEventHandler.INSTANCE);
    }

    // preInit "Run before anything else. Read your config, create blocks, items, etc., and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        RecipeHelper.init();
        GameRegistry.registerBlock(new BaseBlock(Material.cloth, "stone") {
            @Override
            public @Nullable String getTextureDomain(String textureName) {
                return null;
            }

            @Override
            public @Nullable String getNameDomain(String unlocalizedName) {
                return null;
            }

            @Override
            public int getRenderType() {
                return BlockRenderers.LECTERN_TEST.getRenderId();
            }

            @Override
            public void registerBlockIcons(IIconRegister reg) {
                super.registerBlockIcons(reg);
                reg.registerIcon("lectern_base");
            }
        }, "test_block");
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        mapBlocks.entrySet().stream()
            .map(entry -> new BlockItemIterateEvent.BlockRegister.Init(entry.getValue(), entry.getKey())).forEach(MinecraftForge.EVENT_BUS::post);
        mapItems.entrySet().stream()
            .map(entry -> new BlockItemIterateEvent.ItemRegister.Init(entry.getValue(), entry.getKey())).forEach(MinecraftForge.EVENT_BUS::post);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        mapBlocks.entrySet().stream()
            .map(entry -> new BlockItemIterateEvent.BlockRegister.PostInit(entry.getValue(), entry.getKey())).forEach(MinecraftForge.EVENT_BUS::post);
        mapItems.entrySet().stream()
            .map(entry -> new BlockItemIterateEvent.ItemRegister.PostInit(entry.getValue(), entry.getKey())).forEach(MinecraftForge.EVENT_BUS::post);
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        mapBlocks.entrySet().stream()
            .map(entry -> new BlockItemIterateEvent.BlockRegister.LoadComplete(entry.getValue(), entry.getKey())).forEach(MinecraftForge.EVENT_BUS::post);
        mapItems.entrySet().stream()
            .map(entry -> new BlockItemIterateEvent.ItemRegister.LoadComplete(entry.getValue(), entry.getKey())).forEach(MinecraftForge.EVENT_BUS::post);
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
    }
}
