package roadhog360.hogutils.api.hogtags;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.include.com.google.common.collect.Lists;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.hogtags.event.OreDictionaryToTagStringEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Modern-esque tag system.
 * Uses https://fabricmc.net/wiki/community:common_tags as a standard rather than the vanilla or Forge standard tags.
 * New tags added by HogUtils have the "hogutils" domain.
 */
public final class HogTags {

    private HogTags() {}

    /// Only use this if you've modified the registry to add your own custom taggable thing
    public static <E> void addTagsToObject(E objToTag, String... tags) {
        HogTagsRegistry.addTagsToObject(objToTag, tags);
    }

    /// Only use this if you've modified the registry to add your own custom taggable thing
    public static <E> void removeTagsFromObject(E objToTag, String... tags) {
        HogTagsRegistry.removeTagsFromObject(objToTag, tags);
    }


    public static class MiscHelpers {
        /// Adds the following tags to both the block and its item too.
        /// Probably doesn't work for pre-init so don't put this in your block's constructor.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void addTagsToBlockAndItem(Block object, int meta, String... tags) {
            Item item = Item.getItemFromBlock(object);
            if(item != null) {
                ItemTags.addTags(item, meta, tags);
            } else {
                HogTagsRegistry.ITEMBLOCK_ADDITION_QUEUE.add(Pair.of(Pair.of(object, meta), tags));
            }
            BlockTags.addTags(object, item != null && item.getHasSubtypes() ? meta : OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Adds the following tags to both the block and its item too. If the item is not found it'll be added to the item during init.
        /// Keep this in mind if you put this in your block's constructor, as the respective item won't be tagged until AFTER preInit due to this.
        /// If your block is registered past preInit... don't do that. Blocks and Items should ALWAYS be registered in preInit.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things like that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void addTagsToBlockAndItem(Block object, String... tags) {
            addTagsToBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to both the block and its item too. If the item is not found it'll be removed from the item during init.
        /// Keep this in mind if you put this somewhere in preInit, as the respective item's tag won't be removed until AFTER preInit due to this.
        /// If you're targeting a block registered in preInit, you'll need to update your logic to work around this, instead of using this function,
        /// as blocks and items registered after preInit are unsupported by the queueing system.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things like that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void removeTagsFromBlockAndItem(Block object, int meta, String... tags) {
            Item item = Item.getItemFromBlock(object);
            if(item != null) {
                ItemTags.removeTags(item, meta, tags);
            } else {
                HogTagsRegistry.ITEMBLOCK_REMOVAL_QUEUE.add(Pair.of(Pair.of(object, meta), tags));
            }
            BlockTags.removeTags(object, item != null && item.getHasSubtypes() ? meta : OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to both the block and its item too.
        /// Probably doesn't work for pre-init so don't put this in your block's constructor.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void removeTagsFromBlockAndItem(Block object, String... tags) {
            removeTagsFromBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
        }

        // Utils and helpers for random blocks

        /// Add tags to this block, with meta presets designed for logs.
        public static void addTagsToLog(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.addTags(log, meta + 4, tags);
            BlockTags.addTags(log, meta + 8, tags);
            BlockTags.addTags(log, meta + 12, tags);
        }

        /// Remove tags to this block, with meta presets designed for logs.
        public static void removeTagsFromLog(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.removeTags(log, meta + 4, tags);
            BlockTags.removeTags(log, meta + 8, tags);
            BlockTags.removeTags(log, meta + 12, tags);
        }

        /// Add tags to this block, with meta presets designed for slabs.
        public static void addTagsToSlab(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.addTags(log, meta + 8, tags);
        }

        /// Remove tags to this block, with meta presets designed for slabs.
        public static void removeTagsFromSlab(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.removeTags(log, meta + 8, tags);
        }
    }

    public static class ItemTags {
        /// Adds the following tags to the specified item.
        public static void addTags(Item item, int meta, String... tags) {
            addTagsToObject(RegistryMapping.of(item, meta, false), tags);
        }

        public static void addTags(Item item, String... tags) {
            addTags(item, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to the specified item.
        public static void removeTags(Item item, int meta, String... tags) {
            removeTagsFromObject(RegistryMapping.of(item, meta, false), tags);
        }

        public static void removeTags(Item item, String... tags) {
            removeTags(item, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Get the tags for the passed in item. You can pass in a Block's ItemBlock, too.
        /// (Typically obtained through Item#getItemFromBlock)
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static Set<String> getTags(Item item, int meta) {
            Set<String> set = Sets.newLinkedHashSet();
            if(meta != OreDictionary.WILDCARD_VALUE) {
                set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.of(item, OreDictionary.WILDCARD_VALUE, false)));
            }
            set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.of(item, meta, false)));
            return set;
        }

        /// Get the items for the passed in tag.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static Set<RegistryMapping<Item>> getInTag(String tag) {
            return Sets.newLinkedHashSet(HogTagsRegistry.getItemsInTag(tag));
        }

        /// Get the items for the passed in tag. Returns them as {@link ItemStack} objects of stack size 1.
        /// Useful if you're accessing the registry via reflection, as an {@link ItemStack} would be easier to work with.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static Set<ItemStack> getItemStacksInTag(String tag) {
            return HogTagsRegistry.getBlocksInTag(tag)
                .stream().map(mapping -> new ItemStack(mapping.getObject(), 1, mapping.getMeta())).collect(Collectors.toCollection(Sets::newLinkedHashSet));
        }

        //Returns true if the passed in item has any of the listed tags.
        public static boolean hasTag(Item item, int meta, String... tags) {
            return getTags(item, meta).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }
    }

    public static class BlockTags {
        /// Adds the following tags to the specified block.
        public static void addTags(Block block, int meta, String... tags) {
            addTagsToObject(RegistryMapping.of(block, meta, false), tags);
        }

        public static void addTags(Block block, String... tags) {
            addTags(block, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to the specified block.
        public static void removeTags(Block block, int meta, String... tags) {
            removeTagsFromObject(RegistryMapping.of(block, meta, false), tags);
        }

        public static void removeTags(Block block, String... tags) {
            removeTags(block, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Get the tags for the passed in block.
        ///
        /// Returns a new list independent of the ones in the registry,
        /// thBlock objects returned may be mutated freely without making your own list to put them in for mutation.
        public static Set<String> getTags(Block block, int meta) {
            Set<String> set = Sets.newLinkedHashSet();
            if(meta != OreDictionary.WILDCARD_VALUE) {
                set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.of(block, OreDictionary.WILDCARD_VALUE, false)));
            }
            set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.of(block, meta, false)));
            return set;
        }

        /// Get the blocks for the passed in tag via RegistryMapping
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static Set<RegistryMapping<Block>> getInTag(String tag) {
            return Sets.newLinkedHashSet(HogTagsRegistry.getBlocksInTag(tag));
        }

        /// Get the blocks for the passed in tag. Returns them as {@link ItemStack} objects of stack size 1.
        /// Useful if you're accessing the registry via reflection, as an {@link ItemStack} would be easier to work with.
        /// Note that any returned blocks which do not have an {@link ItemBlock} will not appear in this list.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static Set<ItemStack> getItemStacksInTag(String tag) {
            return HogTagsRegistry.getBlocksInTag(tag)
                .stream().map(mapping -> new ItemStack(mapping.getObject(), 1, mapping.getMeta())).collect(Collectors.toCollection(Sets::newLinkedHashSet));
        }

        /// Get the blocks for the passed in tag. Returns them as {@link Pair}<{@link Block}, {@link Integer}> objects.
        /// Useful if you're accessing the registry via reflection, as a {@link Pair} would be easier to work with.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static Set<Pair<Block, Integer>> getBlockPairsInTag(String tag) {
            return HogTagsRegistry.getBlocksInTag(tag)
                .stream().map(mapping -> Pair.of(mapping.getObject(), mapping.getMeta())).collect(Collectors.toCollection(Sets::newLinkedHashSet));
        }

        //Returns true if the passed in item has any of the listed tags.
        public static boolean hasTag(Block block, int meta, String... tags) {
            return getTags(block, meta).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }
    }

    public static class Utils {
        public static void applyFiltersToTags(String... tags) {
            IntStream.range(0, tags.length).forEach(i -> tags[i] = applyFiltersToTag(tags[i]));
        }

        public static String applyFiltersToTag(String tag) {
            if (tag == null || tag.isEmpty() || tag.equals("#")) {
                throw new RuntimeException("Cannot pass in empty tag (or just \"#\") to the tags registry!");
            }
            //Sanity checks passed, let's do some filtering

            if (tag.startsWith("#")) {
                tag = tag.substring(1);
            }
            if (!tag.contains(":")) {
                try {
                    tag = Loader.instance().activeModContainer().getModId() + ":" + tag;
                } catch (
                    Exception e) { //This could also happen if there's an error in the OreDictionary auto-tagging system, since that'd return an invalid mod container.
                    throw new RuntimeException("Could not determine mod id for unprefixed tag " + tag + "!" +
                        "\nThis could be for several reasons, sometimes Forge's mod container fetcher just doesn't work, your code could be called from mixin'd vanilla code, etc..." +
                        "\nIt's good practice to just add a mod prefix to your tags. Do that please...");
                }
            }
            return tag;
        }

        /// Map for prefix-based {@link OreDictionary} registration. Example: oreIron becomes `c:ores/iron`
        /// Takes the part after the prefix provided and converts it to lower camel case.
        /// So because `ore` to `c:ores` is an entry in this map, if you passed in `oreMyMaterial` it'd detect the `ore` part due to the capital letter after it, and do the following:
        ///  - Truncate the `ore` prefix
        ///  - Converts the rest of the string to lower_snake_case.
        ///  - Then finally, it adds `c:ores` to the beginning of the string,
        ///
        /// The right hand assignment is a boolean, determining if the "blank" tag should be added alongside the regular one.
        ///
        /// So the ores entry in this map has this as `TRUE` so in addition to the above, `c:ores` will also be added as a tag.
        ///
        /// If `FALSE`, a {@link OreDictionary} tag without a suffix (anything beyond the specified prefix) will not register anything.
        public static final Map<String, Pair<String, Boolean>> PREFIX_BASED_TAGS = Maps.newHashMap();
        static {
            PREFIX_BASED_TAGS.put("ore", Pair.of("c:ores", true));
            PREFIX_BASED_TAGS.put("ingot", Pair.of("c:ingots", true));
            PREFIX_BASED_TAGS.put("gem", Pair.of("c:gems", true));
            PREFIX_BASED_TAGS.put("block", Pair.of("c:storage_blocks", true));
            PREFIX_BASED_TAGS.put("raw", Pair.of("c:raw_materials", true));
        }

        /// FULL OreDict tags that should not be hit by the prefix maps.
        /// So since `oreQuartz` has no equivalent commons tag (like `c:storage_blocks/quartz`, because it isn't a storage block.
        public static final Set<String> PREFIX_SUFFIX_TAG_EXEMPTIONS = Sets.newHashSet();
        static {
            PREFIX_SUFFIX_TAG_EXEMPTIONS.add("blockQuartz");
        }

        /// Simply put, oreDict tags that register a tag if the tag is an exact match.
        /// One of the examples in the below map is `logWood` to `minecraft:logs`.
        /// So if the tag is EXACTLY `logWood`, then `minecraft:logs` is registered.
        public static final Map<String, String[]> FULL_SWAPS = Maps.newHashMap();
        static {
            FULL_SWAPS.put("logWood", new String[]{"minecraft:logs"});
            FULL_SWAPS.put("blockGlass", new String[]{"c:glass_blocks", "c:glass_blocks/cheap"});
            FULL_SWAPS.put("blockGlassColorless", new String[]{"c:glass_blocks/colorless"});
            FULL_SWAPS.put("paneGlass", new String[]{"c:glass_panes", "c:glass_panes/cheap"});
            FULL_SWAPS.put("paneGlassColorless", new String[]{"c:glass_panes/colorless"});
        }

        /// Converts an OreDictionary string to the [Fabric common tag standard](https://fabricmc.net/wiki/community:common_tags). Examples:
        /// `oreIron` converts to `c:ores/iron`, `ingotCopper` becomes `c:ingots/copper`
        /// Sometimes something may return MULTIPLE tags, hence the list, like `paneGlassPurple` will return a list containing both `c:purple_glass_panes` AND `c:dyed/purple`
        /// Pass in the last boolean as `TRUE` if you want it to return a generic tag in place, for example `someRandomTag` would become `ore_dictionary:some_random_tag` instead of returning an empty list.
        /// If the boolean is `FALSE`, tags not eligible to convert will not be added to the list, meaning the list would become empty.
        /// See the maps this uses for more info on how they're being used. Instead of using the event, you may also add your own dynamic filters to the static maps, if you wish.
        public static Set<String> convertOreDictToTags(String oreDict, boolean returnsGenericTag) {
            Set<String> tags = Sets.newLinkedHashSet();

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
            }


            if(FULL_SWAPS.containsKey(oreDict)) {
                Collections.addAll(tags, FULL_SWAPS.get(oreDict));
            } else {
                String oreDictPrefix = null;
                for(String checkPrefix : PREFIX_BASED_TAGS.keySet()) {
                    if(oreDict.startsWith(checkPrefix)) {
                        oreDictPrefix = checkPrefix;
                        break;
                    }
                }
                if(oreDictPrefix != null) {
                    doPrefixingLogic(oreDict, oreDictPrefix, tags);
                }
            }


            Set<String> eventTags = Sets.newLinkedHashSet();
            MinecraftForge.EVENT_BUS.post(new OreDictionaryToTagStringEvent(oreDict, tags, eventTags));
            tags.addAll(eventTags);

            if(returnsGenericTag && !tags.isEmpty()) {
                tags.add("ore_dictionary:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDict));
            }

            return tags;
        }

        private static void doPrefixingLogic(String oreDict, String prefix, Set<String> tags) {
            Pair<String, Boolean> data = PREFIX_BASED_TAGS.get(prefix);

            //The other half of the oreDict tag
            String oreDictSuffix = oreDict.substring(prefix.length());

            if (!oreDictSuffix.isEmpty() && ArrayUtils.contains(GenericUtils.Constants.MODERN_COLORS_CAMEL_CASE,
                String.valueOf(oreDictSuffix.charAt(0)).toLowerCase() + oreDictSuffix.substring(1))) {
                tags.add("c:dyed");
                tags.add("c:dyed/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDictSuffix));
            }

            if (data != null) {
                String tagPrefix = data.getLeft();
                if(!PREFIX_SUFFIX_TAG_EXEMPTIONS.contains(oreDict)) { // This needs to be here so we can run the above logic
                    if (data.getRight()) { // Adds the "plain" tag if this one should get it. Example: c:ores as well as c:ores/iron
                        tags.add(tagPrefix);
                    }
                    if (!oreDictSuffix.isEmpty()) {
                        tags.add(tagPrefix + "/" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDictSuffix));
                    }
                }
            }
        }

        private static void tagGlassBlocks(String oreDict, Set<String> tags) {

        }
    }
}
