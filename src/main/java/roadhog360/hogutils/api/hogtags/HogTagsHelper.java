package roadhog360.hogutils.api.hogtags;

import com.google.common.base.CaseFormat;
import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.hogtags.event.OreDictionaryToTagStringEvent;
import roadhog360.hogutils.api.hogtags.mappings.BlockTagMapping;
import roadhog360.hogutils.api.hogtags.mappings.ItemTagMapping;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/// Modern-esque tag system.
/// Uses [Fabric common tags standard](https://fabricmc.net/wiki/community:common_tags) as a standard rather than the vanilla or Forge standard tags.
/// New tags added by HogUtils have the "hogutils" domain.
public final class HogTagsHelper {

    private HogTagsHelper() {}


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
            }
            BlockTags.addTags(object, item != null && item.getHasSubtypes() ? meta : OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Adds the following tags to both the block and its item too.
        /// Probably doesn't work for pre-init so don't put this in your block's constructor.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things like that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void addTagsToBlockAndItem(Block object, String... tags) {
            addTagsToBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to both the block and its item too. If the item is not found it'll be removed from the item during init.
        /// Probably doesn't work for pre-init so don't put this in your block's constructor.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things like that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void removeTagsFromBlockAndItem(Block object, int meta, String... tags) {
            Item item = Item.getItemFromBlock(object);
            if(item != null) {
                ItemTags.removeTags(item, meta, tags);
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
        // TODO: Should we support N/EIDs or should modders using those IDs be expected to add the extra data themselves?

        /// Add tags to this block, with meta presets designed for stuff where the variant wraps every 4 meta, like logs or leaves
        public static void addTagsTo4ths(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.addTags(log, meta + 4, tags);
            BlockTags.addTags(log, meta + 8, tags);
            BlockTags.addTags(log, meta + 12, tags);
        }

        /// Remove tags to this block, with meta presets designed for stuff where the variant wraps every 4 meta, like logs or leaves
        public static void removeTagsFrom4ths(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.removeTags(log, meta + 4, tags);
            BlockTags.removeTags(log, meta + 8, tags);
            BlockTags.removeTags(log, meta + 12, tags);
        }

        /// Add tags to this block, with meta presets designed for stuff where the variant wraps every 8 meta values, like slabs
        public static void addTagsTo8ths(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.addTags(log, meta + 8, tags);
        }

        /// Remove tags to this block, with meta presets designed for stuff where the variant wraps every 8 meta values, like slabs
        public static void removeTagsFrom8ths(Block log, int meta, String... tags) {
            addTagsToBlockAndItem(log, meta, tags);
            BlockTags.removeTags(log, meta + 8, tags);
        }

        public static void addInheritorsToItemAndBlock(String tag, String... inherits) {
            HogTags.addInheritorsToTag(BlockTags.CONTAINER_ID, tag, inherits);
            HogTags.addInheritorsToTag(ItemTags.CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritorsFromItemAndBlock(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(BlockTags.CONTAINER_ID, tag, inherits);
            HogTags.removeInheritorsFromTag(ItemTags.CONTAINER_ID, tag, inherits);
        }

        public static List<String> getInheritorsForItemAndBlock(String tag) {
            List<String> set = new ObjectArrayList<>();
            set.addAll(HogTags.getInheritors(ItemTags.CONTAINER_ID, tag));
            set.addAll(HogTags.getInheritors(BlockTags.CONTAINER_ID, tag));
            return new ObjectImmutableList<>(set);
        }
    }

    public static class ItemTags {
        public static final String CONTAINER_ID = "minecraft:item";

        /// Adds the following tags to the specified item.
        public static void addTags(Item item, int meta, String... tags) {
            if(!RecipeHelper.validateItems(item)) return;
            HogTags.addTagsToObject(CONTAINER_ID, ItemTagMapping.of(item, meta), tags);
        }

        public static void addTags(Item item, String... tags) {
            addTags(item, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to the specified item.
        /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
        /// It may also be present in multiple lists in the inheritance tree.
        ///
        /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:items`.
        public static void removeTags(Item item, int meta, String... tags) {
            if(!RecipeHelper.validateItems(item)) return;
            HogTags.removeTagsFromObject(CONTAINER_ID, ItemTagMapping.of(item, meta), tags);
        }

        public static void removeTags(Item item, String... tags) {
            removeTags(item, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Get the tags for the passed in item. You can pass in a Block's ItemBlock, too.
        /// (Typically obtained through Item#getItemFromBlock)
        public static List<String> getTags(Item item, int meta) {
            return HogTags.getTagsFromObject(CONTAINER_ID, ItemTagMapping.of(item, meta));
        }

        /// Get the Items for the passed in tag via {@link ItemTagMapping} objects.
        ///
        /// {@link ItemTagMapping#getObject()} gets the {@link Item} object, whilst {@link ItemTagMapping#getMeta()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
        /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
        public static List<ItemTagMapping> getInTag(String tag) {
            return HogTags.getObjectsForTag(CONTAINER_ID, tag);
        }

        /// Get the Items for the passed in tag. Returns them as {@link Pair}<{@link Item}, {@link Integer}> objects,
        /// which is the underlying type for a {@link RegistryMapping} extends, which {@link ItemTagMapping} uses.
        ///
        /// Useful if you're accessing the registry via reflection, as a {@link Pair} would be easier to work with in that context.
        /// {@link Pair#getLeft()} gets the {@link Item} object, whilst {@link Pair#getRight()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
//        @SuppressWarnings("unchecked")
//        public static List<Pair<Item, Integer>> getPairsInTag(String tag) {
//            return (List<Pair<Item, Integer>>) (Object) getInTag(tag);
//        } //TODO: Commented due to not having made a RegistryMapping into a pair (yet?)
        public static List<Pair<Item, Integer>> getPairsInTag(String tag) {
            return HogTags.<ItemTagMapping>getObjectsForTag(CONTAINER_ID, tag)
                .stream().map(mapping -> Pair.of(mapping.getObject(), mapping.getMeta()))
                .collect(Collectors.toCollection(ObjectArrayList::new));
        }

        /// Get the items for the passed in tag. Returns them as {@link ItemStack} objects of stack size 1.
        /// Useful if you're accessing the registry via reflection, as an {@link ItemStack} would be easier to work with.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static List<ItemStack> getItemStacksInTag(String tag) {
            return HogTags.<ItemTagMapping>getObjectsForTag(CONTAINER_ID, tag)
                .stream().map(mapping -> new ItemStack(mapping.getObject(), 1, mapping.getMeta()))
                .collect(Collectors.toCollection(ObjectArrayList::new));
        }

        /// Returns true if the passed in item has any of the listed tags.
        public static boolean hasAnyTag(Item block, int meta, String... tags) {
            return getTags(block, meta).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }

        public static void addInheritors(String tag, String... inherits) {
            HogTags.addInheritorsToTag(CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritors(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(CONTAINER_ID, tag, inherits);
        }

        public static List<String> getInheritors(String tag) {
            return HogTags.getInheritors(CONTAINER_ID, tag);
        }
    }

    public static class BlockTags {
        public static final String CONTAINER_ID = "minecraft:block";

        /// Adds the following tags to the specified block.
        public static void addTags(Block block, int meta, String... tags) {
            if(!RecipeHelper.validateItems(block)) return;
            HogTags.addTagsToObject(CONTAINER_ID, BlockTagMapping.of(block, meta), tags);
        }

        public static void addTags(Block block, String... tags) {
            addTags(block, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Removes the following tags to the specified block.
        /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
        /// It may also be present in multiple lists in the inheritance tree.
        ///
        /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:blocks`.
        public static void removeTags(Block block, int meta, String... tags) {
            if(!RecipeHelper.validateItems(block)) return;
            HogTags.removeTagsFromObject(CONTAINER_ID, BlockTagMapping.of(block, meta), tags);
        }

        public static void removeTags(Block block, String... tags) {
            removeTags(block, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Get the tags for the passed in block.
        public static List<String> getTags(Block block, int meta) {
            return HogTags.getTagsFromObject(CONTAINER_ID, BlockTagMapping.of(block, meta));
        }

        /// Get the blocks for the passed in tag via {@link BlockTagMapping} objects.
        ///
        /// {@link BlockTagMapping#getObject()} gets the {@link Block} object, whilst {@link BlockTagMapping#getMeta()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
        /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
        public static List<BlockTagMapping> getInTag(String tag) {
            return HogTags.getObjectsForTag(CONTAINER_ID, tag);
        }

        /// Get the blocks for the passed in tag. Returns them as {@link Pair}<{@link Block}, {@link Integer}> objects,
        /// which is the underlying type for a {@link RegistryMapping} extends, which {@link BlockTagMapping} uses.
        ///
        /// Useful if you're accessing the registry via reflection, as a {@link Pair} would be easier to work with in that context.
        /// {@link Pair#getLeft()} gets the {@link Block} object, whilst {@link Pair#getRight()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
//        @SuppressWarnings("unchecked")
//        public static List<Pair<Block, Integer>> getPairsInTag(String tag) {
//            return (List<Pair<Block, Integer>>) (Object) getInTag(tag);
//        } //TODO: Commented due to not having made a RegistryMapping into a pair (yet?)
        public static List<Pair<Block, Integer>> getPairsInTag(String tag) {
            return HogTags.<BlockTagMapping>getObjectsForTag(CONTAINER_ID, tag)
                .stream().map(mapping -> Pair.of(mapping.getObject(), mapping.getMeta()))
                .collect(Collectors.toCollection(ObjectArrayList::new));
        }

        /// Get the blocks for the passed in tag. Returns them as {@link ItemStack} objects of stack size 1.
        /// Useful if you're accessing the registry via reflection, as an {@link ItemStack} would be easier to work with.
        /// Note that any returned blocks which do not have an {@link ItemBlock} will not appear in this list.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static List<ItemStack> getItemStacksInTag(String tag) {
            return HogTags.<BlockTagMapping>getObjectsForTag(CONTAINER_ID, tag)
                .stream().map(mapping -> new ItemStack(mapping.getObject(), 1, mapping.getMeta()))
                .collect(Collectors.toCollection(ObjectArrayList::new));
        }

        /// Returns true if the passed in block has any of the listed tags.
        public static boolean hasAnyTag(Block block, int meta, String... tags) {
            return getTags(block, meta).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }

        public static void addInheritors(String tag, String... inherits) {
            HogTags.addInheritorsToTag(CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritors(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(CONTAINER_ID, tag, inherits);
        }

        public static List<String> getInheritors(String tag) {
            return HogTags.getInheritors(CONTAINER_ID, tag);
        }
    }

    public static class BiomeTags {
        public static final String CONTAINER_ID = "minecraft:worldgen/biome";

        private static BiomeGenBase getBiomeFromID(int id) {
            if (id >= 0 && id < BiomeGenBase.getBiomeGenArray().length && BiomeGenBase.getBiomeGenArray()[id] != null) {
                return BiomeGenBase.getBiomeGenArray()[id];
            }
            throw new IllegalArgumentException(id + " is not a valid Biome ID!");
        }

        /// Adds the following tags to the specified biome.
        public static void addTags(BiomeGenBase biome, String... tags) {
            HogTags.addTagsToObject(CONTAINER_ID, biome, tags);
        }

        /// Adds the following tags to the specified biome via its ID.
        public static void addTags(int id, String... tags) {
            addTags(getBiomeFromID(id));
        }

        /// Removes the following tags to the specified biome.
        /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
        /// It may also be present in multiple lists in the inheritance tree.
        ///
        /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:worldgen/biome`.
        public static void removeTags(BiomeGenBase biome, String... tags) {
            HogTags.removeTagsFromObject(CONTAINER_ID, biome, tags);
        }

        /// Removes the following tags to the specified biome via its ID
        /// If the removal doesn't work, it may be part of the wildcard tags instead, or part of an inherited tags list.
        /// It may also be present in multiple lists in the inheritance tree.
        ///
        /// You can always use `/tags dump` to get a full dump of any tags registry, this one's id is `minecraft:worldgen/biome`.
        public static void removeTags(int id, String... tags) {
            removeTags(getBiomeFromID(id), tags);
        }

        /// Get the tags for the passed in biome.
        public static List<String> getTags(BiomeGenBase biome) {
            return HogTags.getTagsFromObject(CONTAINER_ID, biome);
        }

        /// Get the tags for the passed in biome via its ID.
        public static List<String> getTags(int id) {
            return getTags(getBiomeFromID(id));
        }

        /// Get the biomes for the passed in tag via RegistryMapping
        public static List<BiomeGenBase> getInTag(String tag) {
            return HogTags.getObjectsForTag(CONTAINER_ID, tag);
        }

        /// Returns true if the passed in biome has any of the listed tags.
        public static boolean hasAnyTag(BiomeGenBase biome, String... tags) {
            return getTags(biome).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }

        /// Returns true if the passed in biome via its id, has any of the listed tags.
        public static boolean hasAnyTag(int id, String... tags) {
            return hasAnyTag(getBiomeFromID(id));
        }

        public static void addInheritors(String tag, String... inherits) {
            HogTags.addInheritorsToTag(CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritors(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(CONTAINER_ID, tag, inherits);
        }

        public static List<String> getInheritors(String tag) {
            return HogTags.getInheritors(CONTAINER_ID, tag);
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
                } catch (Exception e) {
                    //This could also happen if there's an error in the OreDictionary auto-tagging system, since that'd return an invalid mod container.
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
        public static final Map<String, Pair<String, Boolean>> PREFIX_BASED_TAGS = new Object2ObjectArrayMap<>();
        static {
            PREFIX_BASED_TAGS.put("ore", Pair.of("c:ores", true));
            PREFIX_BASED_TAGS.put("ingot", Pair.of("c:ingots", true));
            PREFIX_BASED_TAGS.put("gem", Pair.of("c:gems", true));
            PREFIX_BASED_TAGS.put("block", Pair.of("c:storage_blocks", true));
            PREFIX_BASED_TAGS.put("raw", Pair.of("c:raw_materials", true));
        }

        /// OreDict tags that are registered but have this at the beginning of their name, will not be hit by the prefix maps.
        /// So since `oreQuartz` has no equivalent commons tag (like `c:storage_blocks/quartz`, because it isn't a storage block.
        public static final Set<String> PREFIX_SUFFIX_TAG_EXEMPTIONS = new ObjectArraySet<>();
        static {
            PREFIX_SUFFIX_TAG_EXEMPTIONS.add("blockQuartz");
            PREFIX_SUFFIX_TAG_EXEMPTIONS.add("blockGlass");
            PREFIX_SUFFIX_TAG_EXEMPTIONS.add("paneGlass");
        }

        /// Simply put, oreDict tags that register a tag if the tag is an exact match.
        /// One of the examples in the below map is `logWood` to `minecraft:logs`.
        /// So if the tag is EXACTLY `logWood`, then `minecraft:logs` is registered.
        public static final Map<String, String[]> FULL_SWAPS = new Object2ObjectArrayMap<>();
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

        /// Converts an OreDictionary string to the [Fabric common tag standard](https://fabricmc.net/wiki/community:common_tags). Examples:
        /// `oreIron` converts to `c:ores/iron`, `ingotCopper` becomes `c:ingots/copper`
        /// Sometimes something may return MULTIPLE tags, hence the list, like `paneGlassPurple` will return a list containing both `c:purple_glass_panes` AND `c:dyed/purple`
        /// Pass in the last boolean as `TRUE` if you want it to return a generic tag in place, for example `someRandomTag` would become `ore_dictionary:some_random_tag` instead of returning an empty list.
        /// If the boolean is `FALSE`, tags not eligible to convert will not be added to the list, meaning the list would become empty.
        /// See the maps this uses for more info on how they're being used. Instead of using the event, you may also add your own dynamic filters to the static maps, if you wish.
        public static List<String> convertOreDictToTags(String oreDict, boolean returnsGenericTag) {
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

            List<String> eventTags = new ObjectArrayList<>();
            MinecraftForge.EVENT_BUS.post(new OreDictionaryToTagStringEvent(oreDict, tags, eventTags));
            tags.addAll(eventTags);

            if(returnsGenericTag && !tags.isEmpty()) {
                tags.add("ore_dictionary:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDict));
            }

            return tags;
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
}
