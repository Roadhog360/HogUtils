package roadhog360.hogutils.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import org.spongepowered.asm.mixin.MixinEnvironment;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.mixinplugin.HogUtilsEarlyMixins;

public class HogUtilsConfigs {

    public static void init() {
        try {
            if(HogUtilsEarlyMixins.SIDE == MixinEnvironment.Side.CLIENT) {
                ConfigurationManager.registerConfig(HogUtilsConfigs.Utils.F3AndTooltips.class);
            }
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Utils {
        @Config(modid = Tags.MOD_ID, category = "F3 and Tooltips", configSubDirectory = Tags.MOD_ID, filename = "utils")
        @Config.Comment("Options relating to the F3 menu and tooltip info. Item tooltips usually require F3+H mode to be enabled.")
        public static class F3AndTooltips {
            @Config.Comment("Displays HogTags info for biomes and blocks in the F3 menu.")
            @Config.DefaultBoolean(true)
            public static boolean hogTagsInF3;

            @Config.Comment("Displays HogTags info for items in their tooltips.")
            @Config.DefaultBoolean(true)
            public static boolean hogTagsInItemTooltip;
        }

        //Might just say EFR is the examples lol
//        public static class Examples {
//            @Config.Comment("Enables blocks and items and other API examples so developers can easily observe the results of the code in the example packages.")
//            @Config.DefaultBoolean(false)
//            public static boolean enableExamples;
//        }
    }
}
