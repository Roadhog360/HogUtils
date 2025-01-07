package roadhog360.hogutils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roadhog360.hogutils.proxy.CommonProxy;

import java.util.Map;

@Mod(modid = Tags.MOD_ID, version = Tags.VERSION, name = Tags.MOD_NAME, acceptedMinecraftVersions = "[1.7.10]")
public class HogUtils {

    public static final Logger LOG = LogManager.getLogger(Tags.MOD_ID);

    @SidedProxy(clientSide = Tags.MOD_GROUP + ".proxy.ClientProxy", serverSide = Tags.MOD_GROUP + ".proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value = Tags.MOD_ID)
    public static HogUtils instance;

    /// Used to detect registry replacement and transfer tags over.
    private final Map<BiomeGenBase, Integer> vanillaBiomes = new Object2IntOpenHashMap<>();

    @Mod.EventHandler
    public void onConstructing(FMLConstructionEvent event) {
        proxy.onConstructing(event);
        // Set up registry replacement detection to transfer the tags to the replacement biome
//        for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
//            if(biome.getClass().getName().startsWith("net.minecraft.world.Biome")) {
//                vanillaBiomes.put(biome, biome.biomeID);
//            }
//        }
    }

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        registerTags();
    }

    @Mod.EventHandler
    public void doIMC(FMLInterModComms.IMCEvent event) {
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        proxy.onLoadComplete(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
        detectBiomeRegistryReplacement();
    }

    //TODO: Register more tags via these functions

    public void registerTags() {
//        HogTagsHelper.BiomeTags.addInheritors("c:is_dry", "c:is_dry/nether", "c:is_dry/end", "c:is_dry/overworld");
//
//        for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
//            BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
//            if(ArrayUtils.contains(types, NETHER)) {
//                HogTagsHelper.BiomeTags.addTags(biome, "c:is_nether");
//                continue;
//            }
//            if(ArrayUtils.contains(types, END)) {
//                HogTagsHelper.BiomeTags.addTags(biome, "c:is_end");
//                continue;
//            }
//        }
    }

    /// Finds biomes that have been registry replaced by a mod and transfer all of the tags to the new one
    private void detectBiomeRegistryReplacement() {
//        for(Map.Entry<BiomeGenBase, Integer> biome : vanillaBiomes.entrySet()) {
//            if(BiomeGenBase.getBiomeGenArray()[biome.getValue()] != biome.getKey()) {
//                LOG.info("A mod has registry replaced the biome " + biome.getKey().biomeName + ", transferring tags over...");
//                String[] tags = HogTagsHelper.BiomeTags.getTags(biome.getKey()).toArray(new String[]{});
//                HogTagsHelper.BiomeTags.addTags(BiomeGenBase.getBiomeGenArray()[biome.getValue()], tags);
//            }
//        }
    }

    public static void registerTagDynamicBlock(Block block) {

    }

    public static void registerTagDynamicItem(Item item) {

    }
}
