package roadhog360.hogutils.api.hogtags;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// Marked as experimental deliberately because this thing is a fucking mess.
/// Will eventually be rewritten entirely. Seriously, what was I thinking?
///
/// I wanted to use a map since I had a lot of conditions I wanted to register tags based on, and doing so by hand would've been a chore.
/// BUT... this just ISN'T IT. Some of the comments are also weirdly, or nonsensically worded.
@EventBusSubscriber
@ApiStatus.Experimental
public class HogTagsOreDictionaryHelper {

    /// Map for prefix-based {@link OreDictionary} registration. Example: oreIron becomes `c:ores/iron`
    /// Takes the part after the prefix provided and converts it to lower camel case.
    /// So because `ore` to `c:ores` is an entry in this map, if you passed in `oreMyMaterial` it'd detect the `ore` part due to the capital letter after it, and do the following:
    ///  - Truncate the `ore` prefix
    ///  - Converts the rest of the string to lower_snake_case.
    ///  - Then finally, it adds `c:ores` to the beginning of the string,
    ///  - The result is `c:ores/my_material`.
    ///
    /// The right hand assignment is a boolean, determining if the "blank" tag should be added alongside the regular one.
    ///
    /// So the ores entry in this map has this as `TRUE` so in addition to the above, `c:ores` will also be added as a tag.
    ///
    /// If `FALSE`, a {@link OreDictionary} tag without a suffix (anything beyond the specified prefix) will not register anything.
    public static final Map<String, Pair<String, Boolean>> PREFIX_BASED_TAGS = new Object2ObjectOpenHashMap<>();
    static {
        PREFIX_BASED_TAGS.put("ore", Pair.of("c:ores", true));
        PREFIX_BASED_TAGS.put("ingot", Pair.of("c:ingots", true));
        PREFIX_BASED_TAGS.put("gem", Pair.of("c:gems", true));
        PREFIX_BASED_TAGS.put("block", Pair.of("c:storage_blocks", true));
        PREFIX_BASED_TAGS.put("raw", Pair.of("c:raw_materials", true));
    }

    /// OreDict tags that are registered but have this at the beginning of their name, will not be hit by the prefix maps.
    /// So since `oreQuartz` has no equivalent commons tag (like `c:storage_blocks/quartz`, because it isn't a storage block.
    public static final Set<String> PREFIX_SUFFIX_TAG_EXEMPTIONS = new ObjectOpenHashSet<>();
    static {
        PREFIX_SUFFIX_TAG_EXEMPTIONS.add("blockQuartz");
        PREFIX_SUFFIX_TAG_EXEMPTIONS.add("blockGlass");
        PREFIX_SUFFIX_TAG_EXEMPTIONS.add("paneGlass");
    }

    /// Simply put, oreDict tags that register a tag if the tag is an exact match.
    /// One of the examples in the below map is `logWood` to `minecraft:logs`.
    /// So if the tag is EXACTLY `logWood`, then `minecraft:logs` is registered.
    public static final Map<String, String[]> FULL_SWAPS = new Object2ObjectOpenHashMap<>();
    static {
        FULL_SWAPS.put("logWood", new String[]{"minecraft:logs"});
        FULL_SWAPS.put("blockGlass", new String[]{"c:glass_blocks", "c:glass_blocks/cheap"});
        FULL_SWAPS.put("blockGlassColorless", new String[]{"c:glass_blocks/colorless"});
        FULL_SWAPS.put("paneGlass", new String[]{"c:glass_panes", "c:glass_panes/cheap"});
        FULL_SWAPS.put("paneGlassColorless", new String[]{"c:glass_panes/colorless"});
        String[] modernColorsCamelCase = GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE;
        for (int i = 0; i < modernColorsCamelCase.length; i++) {
            String color = modernColorsCamelCase[i];
            String upperCase = String.valueOf(color.charAt(0)).toUpperCase();
            FULL_SWAPS.put("blockGlass" + upperCase + color.substring(1)
                , new String[]{"c:" + GenericUtils.Constants.MODERN_COLORS_SNAKE_CASE[i] + "_glass_blocks"});
            FULL_SWAPS.put("paneGlass" + upperCase + color.substring(1)
                , new String[]{"c:" + GenericUtils.Constants.MODERN_COLORS_SNAKE_CASE[i] + "_glass_panes"});
        }
    }

    /// Converts an OreDictionary entry to the [Fabric common tag standard](https://fabricmc.net/wiki/community:common_tags).
    /// Examples: `oreIron` converts to `c:ores/iron`, `ingotCopper` becomes `c:ingots/copper`
    ///
    /// Sometimes something may return MULTIPLE tags, hence the list, like `paneGlassPurple` will return a list containing both `c:purple_glass_panes` AND `c:dyed/purple`
    ///
    ///  Pass in the last boolean as `TRUE` if you want it to return a generic tag in place, for example `someRandomTag` would become `ore_dictionary:some_random_tag` instead of returning an empty list.
    ///
    ///  If the boolean is `FALSE`, tags not eligible to convert will not be added to the list, meaning the list would become empty.
    ///
    ///  See the maps this uses for more info on how they're being used. Instead of using the event, you may also add your own dynamic filters to the static maps, if you wish.
    public static List<String> getHogTagsForOreDictionary(String oreDict, ItemStack stack, boolean returnsGenericTag) {
        List<String> tags = new ObjectArrayList<>();

        //The below implementations originally used the indexes of the first/last capital letters to determine where to truncate the string
        //The logic for this ended up being really messy so I sacrificed map lookup speed for cleaner code.

        if (oreDict.equals("dye")) {
            tags.add("c:dyes");
        } else if (oreDict.startsWith("dye")) {
            //De-capitalize first letter to use in contains check
            String dye = String.valueOf(oreDict.charAt(3)).toLowerCase() + oreDict.substring(4);
            int dyeID = ArrayUtils.indexOf(GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE, dye);
            if(dyeID > -1) {
                tags.add("c:dyes/" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, GenericUtils.Constants.MODERN_COLORS_SNAKE_CASE[dyeID]));
            }
        } else {
            tagDyed(oreDict, tags);
        }

        if(FULL_SWAPS.containsKey(oreDict)) {
            Collections.addAll(tags, FULL_SWAPS.get(oreDict));
        }

        if(PREFIX_SUFFIX_TAG_EXEMPTIONS.stream().noneMatch(oreDict::startsWith)){
            for (String checkPrefix : PREFIX_BASED_TAGS.keySet()) {
                if (oreDict.startsWith(checkPrefix)) {
                    doPrefixingLogic(oreDict, checkPrefix, tags);
                    break;
                }
            }
        }

        if (returnsGenericTag) {
            tags.add("ore_dictionary:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDict));
        }
        return ImmutableList.copyOf(tags);
    }

    private static void doPrefixingLogic(String oreDict, String prefix, List<String> tags) {
        Pair<String, Boolean> data = PREFIX_BASED_TAGS.get(prefix);

        //The other half of the oreDict tag
        String oreDictSuffix = oreDict.substring(prefix.length());

        if (data != null) {
            String tagPrefix = data.getLeft();
            if (data.getRight()) { // Adds the "plain" tag if this one should get it. Example: c:ores as well as c:ores/iron
                tags.add(tagPrefix);
            }
            if (!oreDictSuffix.isEmpty()) {
                tags.add(tagPrefix + "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDictSuffix));
            }
        }
    }

    private static void tagDyed(String oreDict, List<String> tags) {
        String[] modernColorsCamelCase = GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE;
        for (int i = 0; i < modernColorsCamelCase.length; i++) {
            String color = modernColorsCamelCase[i];
            if (oreDict.endsWith(String.valueOf(color.charAt(0)).toUpperCase() + color.substring(1))) {
                tags.add("c:dyed");
                tags.add("c:dyed/" + GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE[i]);
            }
        }
    }
}
