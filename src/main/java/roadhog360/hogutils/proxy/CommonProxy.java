package roadhog360.hogutils.proxy;

import cpw.mods.fml.common.event.*;
import net.minecraft.init.Blocks;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;
import roadhog360.hogutils.api.utils.RecipeHelper;

public class CommonProxy {

    public void onConstructing(FMLConstructionEvent event) {
    }

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        RecipeHelper.init();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {

    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        finalizeTags();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
    }

    private void finalizeTags() { //For new stuff that isn't auto-tagged by the OreDictionary auto-tagger.
        HogTagsHelper.MiscHelpers.addTagsTo4ths(Blocks.log, 0, "minecraft:oak_logs");
        HogTagsHelper.MiscHelpers.addTagsTo4ths(Blocks.log, 1, "minecraft:spruce_logs");
        HogTagsHelper.MiscHelpers.addTagsTo4ths(Blocks.log, 2, "minecraft:birch_logs");
        HogTagsHelper.MiscHelpers.addTagsTo4ths(Blocks.log, 3, "minecraft:jungle_logs");
        HogTagsHelper.MiscHelpers.addTagsTo4ths(Blocks.log2, 0, "minecraft:acacia_logs");
        HogTagsHelper.MiscHelpers.addTagsTo4ths(Blocks.log2, 1, "minecraft:dark_oak_logs");
    }
}
