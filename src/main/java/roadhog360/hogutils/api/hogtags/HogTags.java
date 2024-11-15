package roadhog360.hogutils.api.hogtags;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.GenericUtils;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.hogtags.event.OreDictionaryToTagStringEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Modern-esque tag system.
 * Uses https://fabricmc.net/wiki/community:common_tags as a standard rather than the vanilla or Forge standard tags.
 * New tags added by HogUtils have the "hogutils" domain.
 */
public final class HogTags {

    private HogTags() {}

    /**
     * Only use this if you've modified/mixin'd the registry to add your own custom taggable thing (unsupported)
     */
    public static <E> void addTagsToObject(E objToTag, String... tags) {
        HogTagsRegistry.addTagsToObject(objToTag, tags);
    }

    /**
     * Only use this if you've modified/mixin'd the registry to add your own custom taggable thing (unsupported)
     */
    public static <E> void removeTagsFromObject(E objToTag, String... tags) {
        HogTagsRegistry.removeTagsFromObject(objToTag, tags);
    }

    /**
     * Adds the following tags to the specified block or item.
     */
    public static <E> void addTagsToBlockOrItem(E object, int meta, String... tags) {
        addTagsToObject(new RegistryMapping<>(object, meta, false), tags);
    }

    public static <E> void addTagsToBlockOrItem(E object, String... tags) {
        addTagsToBlockOrItem(object, OreDictionary.WILDCARD_VALUE, tags);
    }

    /**
     * Removes the following tags to the specified block or item.
     */
    public static <E> void removeTagsFromBlockOrItem(E object, int meta, String... tags) {
        removeTagsFromObject(RegistryMapping.getKeyFor(object, meta, false), tags);
    }

    public static <E> void removeTagsFromBlockOrItem(E object, String... tags) {
        removeTagsFromBlockOrItem(object, OreDictionary.WILDCARD_VALUE, tags);
    }

    /**
     * Adds the following tags to both the block and its item too.
     * Probably doesn't work for pre-init so don't put this in your block's constructor.
     * </p>
     * NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
     * Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
     */
    public static void addTagsToBlockAndItem(Block object, int meta, String... tags) {
        Item item = Item.getItemFromBlock(object);
        if(item != null) {
            addTagsToBlockOrItem(item, meta, tags);
        }
        addTagsToBlockOrItem(object, item != null && item.getHasSubtypes() ? meta : OreDictionary.WILDCARD_VALUE, tags);
    }


    /**
     * Adds the following tags to both the block and its item too.
     * Probably doesn't work for pre-init so don't put this in your block's constructor.
     * </p>
     * NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
     * Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
     */
    public static void addTagsToBlockAndItem(Block object, String... tags) {
        addTagsToBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
    }

    public static void addTagsForLog(Block log, int meta, String... tags) {
        Item logItem = Item.getItemFromBlock(log);
        if(logItem != null) {
            addTagsToBlockOrItem(logItem, meta, tags);
        }
        addTagsToBlockOrItem(log, meta, tags);
        addTagsToBlockOrItem(log, meta + 4, tags);
        addTagsToBlockOrItem(log, meta + 8, tags);
        addTagsToBlockOrItem(log, meta + 12, tags);
    }

    /**
     * Removes the following tags to both the block and its item too.
     * Probably doesn't work for pre-init so don't put this in your block's constructor.
     * </p>
     * NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
     * Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
     */
    public static void removeTagsFromBlockAndItem(Block object, int meta, String... tags) {
        Item item = Item.getItemFromBlock(object);
        if(item != null) {
            removeTagsFromBlockOrItem(item, meta, tags);
        }
        removeTagsFromBlockOrItem(object, item != null && item.getHasSubtypes() ? meta : OreDictionary.WILDCARD_VALUE, tags);
    }


    /// Removes the following tags to both the block and its item too.
    /// Probably doesn't work for pre-init so don't put this in your block's constructor.
    ///
    /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
    /// Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
    public static void removeTagsFromBlockAndItem(Block object, String... tags) {
        removeTagsFromBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
    }

    public static void removeTagsFromLog(Block log, int meta, String... tags) {
        Item logItem = Item.getItemFromBlock(log);
        if(logItem != null) {
            removeTagsFromBlockOrItem(logItem, meta, tags);
        }
        removeTagsFromBlockOrItem(log, meta, tags);
        removeTagsFromBlockOrItem(log, meta + 4, tags);
        removeTagsFromBlockOrItem(log, meta + 8, tags);
        removeTagsFromBlockOrItem(log, meta + 12, tags);
    }

    /// Get the tags for the passed in block or item. You can pass in a Block's ItemBlock, too.
    /// (Typically obtained through Item#getItemFromBlock)
    ///
    /// Returns a new list independent of the ones in the registry,
    /// the objects returned may be mutated freely without making your own list to put them in for mutation.
    public static <E> Set<String> getTagsForBlockOrItem(E block, int meta) {
        Set<String> set = Sets.newLinkedHashSet();
        if(meta != OreDictionary.WILDCARD_VALUE) {
            set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.getKeyFor(block, OreDictionary.WILDCARD_VALUE, false)));
        }
        set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.getKeyFor(block, meta, false)));
        return set;
    }

    public static Set<Block> getBlocksInTag(String tag) {
        return Collections.unmodifiableSet(HogTagsRegistry.getBlocksInTag(tag));
    }

    public static Set<Item> getItemsInTag(String tag) {
        return Collections.unmodifiableSet(HogTagsRegistry.getItemsInTag(tag));
    }

    public static class Utils {

        public static String applyFiltersToTag(String tag) {
            if(tag == null || tag.isEmpty() || tag.equals("#")) {
                throw new RuntimeException("Cannot add empty tag (or just \"#\") to the tags registry!");
            }

            //Sanity checks passed, let's do some filtering

            if(tag.startsWith("#")) {
                tag = tag.substring(1);
            }
            if(!tag.contains(":")) {
                try {
                    tag = Loader.instance().activeModContainer().getModId() + ":" + tag;
                } catch (Exception e) { //This could also happen if there's an error in the OreDictionary auto-tagging system, since that'd return an invalid mod container.
                    throw new RuntimeException("Could not determine mod id for unprefixed tag " + tag + "!" +
                        "\nThis could be for several reasons, sometimes Forge's mod container fetcher just doesn't work, your code could be called from mixin'd vanilla code, etc..." +
                        "\nIt's good practice to just add a mod prefix to your tags. Do that please...");
                }
            }

            //TODO Maybe make this force common standard? Would require a rather large map, sounds annoying...

            return tag;
        }

        /// Map for prefix-based replacements. E.G. oreIron becomes `c:ores/iron`
        /// Takes the part after the prefix provided and converts it to lower camel case.
        /// So because `ore` to `c:ores/` is an entry in this map, if you passed in `oreMyMaterial` it'd detect the `ore` part due to the capital letter after it, and do the following:
        ///  - Truncate the `ore` prefix
        ///  - Converts the rest of the string to lower_snake_case.
        ///  - Then finally, it adds `c:ores/` to the beginning of the string,
        ///
        /// The right hand assignment is a boolean, determining if the "blank" tag should be added alongside the regular one.
        /// So the ores entry in this map has this as `TRUE` so in addition to the above, `c:ores` will also be added as a tag.
        /// If `FALSE`, a blank OreDict tag needs to be registered for a tag that is just the prefix to be registered. Example: blockGlass
        public static final Map<String, Pair<String, Boolean>> PREFIX_BASED_TAGS = Maps.newHashMap();
        static {
            PREFIX_BASED_TAGS.put("ore", Pair.of("c:ores", true));
            PREFIX_BASED_TAGS.put("ingot", Pair.of("c:ingots", true));
            PREFIX_BASED_TAGS.put("gem", Pair.of("c:gems", true));
            PREFIX_BASED_TAGS.put("block", Pair.of("c:storage_blocks", true));
            PREFIX_BASED_TAGS.put("raw", Pair.of("c:raw_materials", true));

            PREFIX_BASED_TAGS.put("blockGlass", Pair.of("c:glass_blocks", false));
            PREFIX_BASED_TAGS.put("paneGlass", Pair.of("c:glass_panes", false));
        }

        /// FULL OreDict tags that should not be hit by the prefix maps.
        /// So since `oreQuartz` has no equivalent commons tag (like `c:storage_blocks/quartz`, because it isn't a storage
        public static final Set<String> PREFIX_TAG_EXEMPTIONS = Sets.newHashSet();
        static {
            PREFIX_TAG_EXEMPTIONS.add("blockQuartz");
        }

        /// Simply put, oreDict tags that register a tag if the tag is an exact match.
        /// One of the examples in the below map is `logWood` to `minecraft:logs`.
        /// So if the tag is EXACTLY `logWood`, then `minecraft:logs` is registered.
        public static final Map<String, String> FULL_SWAPS = Maps.newHashMap();
        static {
            FULL_SWAPS.put("logWood", "minecraft:logs");
        }

        /// Converts an OreDictionary string to the [Fabric common tag standard](https://fabricmc.net/wiki/community:common_tags). Examples:
        /// `oreIron` converts to `c:ores/iron`, `ingotCopper` becomes `c:ingots/copper`
        /// Sometimes something may return MULTIPLE tags, hence the list, like `paneGlassPurple` will return a list containing both `c:purple_glass_panes` AND `c:dyed/purple`
        /// Pass in the last boolean as `TRUE` if you want it to return a generic tag in place, for example `someRandomTag` would become `ore_dictionary:some_random_tag` instead of returning an empty list.
        /// If the boolean is `FALSE`, tags not eligible to convert will not be added to the list, meaning the list would become empty.
        /// See the maps this uses for more info on how they're being used. Instead of using the event, you may also add your own dynamic filters to the static maps, if you wish.
        public static Set<String> convertOreDictTag(String oreDict, boolean returnsGenericTag) {
            Set<String> tags = Sets.newLinkedHashSet();

            //The below implementations originally used the indexes of the first/last capital letters to determine where to truncate the string
            //The logic for this ended up being really messy so I sacrificed map lookup speed for cleaner code.

            if (oreDict.equals("dye")) {
                tags.add("c:dyes");
            } else if (oreDict.startsWith("dye")) {
                //De-capitalize first letter to use in contains check
                String dye = String.valueOf(oreDict.charAt(0)).toLowerCase() + oreDict.substring(1);
                int dyeID = ArrayUtils.indexOf(GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE, dye);
                if(dyeID > -1) {
                    tags.add("c:dyes/" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, GenericUtils.Constants.MODERN_COLORS_SNAKE_CASE[dyeID]));
                }
            }

            String oreDictPrefix = null;
            for(String checkPrefix : PREFIX_BASED_TAGS.keySet()) {
                if(oreDict.startsWith(checkPrefix)) {
                    oreDictPrefix = checkPrefix;
                    break;
                }
            }

            // The map has a key containing the first part of this OreDict tag
            if(oreDictPrefix != null) {
                Pair<String, Boolean> data = PREFIX_BASED_TAGS.get(oreDictPrefix);
                if (data != null) {
                    String tagPrefix = data.getLeft();

                    //The other half of the oreDict tag
                    String oreDictSuffix = oreDict.substring(oreDictPrefix.length());

                    if (!oreDictSuffix.isEmpty() && ArrayUtils.contains(GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE,
                        String.valueOf(oreDictSuffix.charAt(0)).toLowerCase() + oreDictSuffix.substring(1))) {
                        tags.add("c:dyed");
                        tags.add("c:dyed/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDictSuffix));
                    }


                    if(!PREFIX_TAG_EXEMPTIONS.contains(oreDict)) {
                        if (data.getRight() || oreDictSuffix.isEmpty()) { // Adds the "plain" tag if this one should get it. Example: c:ores as well as c:ores/iron
                            tags.add(tagPrefix);
                        }
                        if(!oreDictSuffix.isEmpty()) {
                            tags.add(tagPrefix + "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDictSuffix));
                        }
                    }
                }
            }

            if(FULL_SWAPS.containsKey(oreDict)) {
                tags.add(FULL_SWAPS.get(oreDict));
            }

            Set<String> eventTags = Sets.newLinkedHashSet();
            MinecraftForge.EVENT_BUS.post(new OreDictionaryToTagStringEvent(oreDict, tags, eventTags));
            tags.addAll(eventTags);

            if(returnsGenericTag && !tags.isEmpty()) {
                tags.add("ore_dictionary:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDict));
            }

            return tags;
        }
    }
}
