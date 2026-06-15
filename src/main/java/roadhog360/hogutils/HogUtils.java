package roadhog360.hogutils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.proxy.CommonProxy;

import java.util.Map;

@Mod(modid = Tags.MOD_ID, version = Tags.VERSION, name = Tags.MOD_NAME, acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gtnhlib@[0.11.3,);")
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
    }

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc., and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // Debug code to stress test tags with memory. So far this code shows very low memory impact.
        // Accommodations to the test code will not be made for expanded IDs.
        // It's very unlikely tags will become anywhere near this heavily used even in large packs.
        // Even if a pack is large enough to go well beyond this test scope, it would be at the point where more RAM is expected anyway.
//        for(int i = 0; i < 3000; i ++) {
//            Block dummyBlock = new BaseBlock(Material.rock) {
//                @Override
//                public @Nullable String getTextureDomain(String textureName) {
//                    return "";
//                }
//
//                @Override
//                public @Nullable String getNameDomain(String unlocalizedName) {
//                    return "";
//                }
//            };
//            GameRegistry.registerBlock(dummyBlock, "test"+i);
//            int tags = 15;
//            for(int j = 0; j <= tags; j++) {
//                String tag = "minecraft:test_tag" + j;
//                BlockTags.addTags(dummyBlock, tag);
//                ItemTags.addTags(Item.getItemFromBlock(dummyBlock), tag);
//            }
//        }
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        registerBiomeTags();
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
    }

    //TODO: Register more tags via these functions

    public void registerBiomeTags() {
        HogTags.BLOCKS.put(Blocks.iron_ore, OreDictionary.WILDCARD_VALUE, "minecraft:test1");
        HogTags.BLOCKS.put(Blocks.netherrack, OreDictionary.WILDCARD_VALUE, "minecraft:test2");

        HogTags.BIOMES.putInheritor("c:is_dry/end", "c:is_dry");
        HogTags.BIOMES.putInheritor("c:is_dry/nether", "c:is_dry");
        HogTags.BIOMES.putInheritor("c:is_dry/overworld", "c:is_dry");

        for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
            if(biome != null) {
                BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
                if (ArrayUtils.contains(types, BiomeDictionary.Type.NETHER)) {
                    HogTags.BIOMES.put(biome, "c:is_nether");
                    continue;
                }
                if (ArrayUtils.contains(types, BiomeDictionary.Type.END)) {
                    HogTags.BIOMES.put(biome, "c:is_end");
                    continue;
                }
            }
        }
    }

    private void listenForRegistryReplacement() {
//         Set up registry replacement detection to transfer the tags to the replacement biome
        for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
            if(biome != null && biome.getClass().getName().startsWith("net.minecraft.world.Biome")) {
                vanillaBiomes.put(biome, biome.biomeID);
            }
        }
    }

    public static void registerTagDynamicBlock(Block block) {

    }

    public static void registerTagDynamicItem(Item item) {

    }
}
