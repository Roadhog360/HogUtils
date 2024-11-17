package roadhog360.hogutils.proxy;

import cpw.mods.fml.common.event.*;
import net.minecraft.init.Blocks;
import roadhog360.hogutils.HogUtils;
import roadhog360.hogutils.api.hogtags.HogTags;

public class CommonProxy {

    public void onConstructing(FMLConstructionEvent event) {}

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {}

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    public void onLoadComplete(FMLLoadCompleteEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {finalizeTags();}

    private void finalizeTags() { //For new stuff that isn't auto-tagged by the OreDictionary auto-tagger.
        HogTags.BlockTags.addTagsToLog(Blocks.log, 0, "minecraft:oak_logs");
        HogTags.BlockTags.addTagsToLog(Blocks.log, 1, "minecraft:spruce_logs");
        HogTags.BlockTags.addTagsToLog(Blocks.log, 2, "minecraft:birch_logs");
        HogTags.BlockTags.addTagsToLog(Blocks.log, 3, "minecraft:jungle_logs");
        HogTags.BlockTags.addTagsToLog(Blocks.log2, 0, "minecraft:acacia_logs");
        HogTags.BlockTags.addTagsToLog(Blocks.log2, 1, "minecraft:dark_oak_logs");
    }
}
