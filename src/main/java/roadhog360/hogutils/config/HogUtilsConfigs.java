package roadhog360.hogutils.config;

import roadhog360.hogutils.Tags;
import roadhog360.hogutils.repackaged.fplib.config.Config;

public class HogUtilsConfigs {

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
    }
}
