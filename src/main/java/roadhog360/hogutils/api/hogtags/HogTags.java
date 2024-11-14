package roadhog360.hogutils.api.hogtags;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
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

    public static ThreadLocal<Boolean> oreDictTagRegister = ThreadLocal.withInitial(() -> false);

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


    /**
     * Removes the following tags to both the block and its item too.
     * Probably doesn't work for pre-init so don't put this in your block's constructor.
     * </p>
     * NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
     * Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
     */
    public static void removeTagsFromBlockAndItem(Block object, String... tags) {
        removeTagsFromBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
    }

    //TODO The get functions below should have some caching to make them faster.

    /**
     * Get the tags for the passed in block or item. You can pass in a Block's ItemBlock, too.
     * (Typically obtained through Item#getItemFromBlock)
     * </p>
     * Returns a new list independent from the ones in the registry,
     * meaning the objects returned may be mutated freely without making your own list to put them in for mutation.
     */
    public static <E> Set<String> getTagsForBlockOrItem(E block, int meta) {
        Set<String> set = Sets.newLinkedHashSet();
        if(meta != OreDictionary.WILDCARD_VALUE) {
            set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.getKeyFor(block, OreDictionary.WILDCARD_VALUE, false)));
        }
        set.addAll(HogTagsRegistry.getTagsFromObject(RegistryMapping.getKeyFor(block, meta, false)));
        return Collections.unmodifiableSet(set);
    }

    public static Set<Block> getBlocksInTag(String tag) {
        return Collections.unmodifiableSet(HogTagsRegistry.getBlocksInTag(tag));
    }

    public static Set<Item> getItemsInTag(String tag) {
        return Collections.unmodifiableSet(HogTagsRegistry.getItemsInTag(tag));
    }

    public static class Utils {
        public static String applyFiltersToTag(String tag) {
            if(tag == null || tag.isEmpty()) {
                throw new RuntimeException("Cannot add empty tag to the tags registry!");
            }

            //Sanity checks passed, let's do some filtering

            if(tag.startsWith("#")) {
                tag = tag.substring(1);
            }
            if(!tag.contains(":")) {
                tag = Loader.instance().activeModContainer().getModId() + ":" + tag;
            }

            //TODO Maybe make this force common standard? Would require a rather large map, sounds annoying...

            return tag;
        }

        public static final Map<String, Set<String>> COLOR_TAGS = Maps.newHashMap();
        static {
            COLOR_TAGS.put("purple", Sets.newHashSet("paneGlassPurple"));
        }

        public static final Map<String, String> PREFIX_REPLACEMENTS = Maps.newHashMap();
        static {
            PREFIX_REPLACEMENTS.put("ore", "c:ores/");
            PREFIX_REPLACEMENTS.put("ingot", "c:ingots/");
            PREFIX_REPLACEMENTS.put("gem", "c:gems/");
            PREFIX_REPLACEMENTS.put("block", "c:storage_blocks/");
            PREFIX_REPLACEMENTS.put("raw", "c:raw_materials/");
        }

        /// Converts an OreDictionary string to the [Fabric common tag standard](https://fabricmc.net/wiki/community:common_tags). Examples:
        /// `oreIron` converts to `c:ores/iron`, `ingotCopper` becomes `c:ingots/copper`
        /// Sometimes something may return MULTIPLE tags, hence the list, like `paneGlassPurple` will return a list containing both `c:purple_glass_panes` AND `c:dyed/purple`
        /// Pass in the last boolean as `TRUE` if you want it to return a generic tag in place, for example `someRandomTag` would become `ore_dictionary:some_random_tag` instead of returning an empty list.
        /// If the boolean is `FALSE`, tags not eligible to convert will not be added to the list, meaning the list would become empty.
        public static Set<String> convertOreDictTag(String oreDict, boolean returnsGenericTag) {
            Set<String> tags = Sets.newLinkedHashSet();

            int truncIndex = GenericUtils.indexOfFirstCapitalLetter(oreDict);
            if(truncIndex > -1) {
                String tagPrefix = PREFIX_REPLACEMENTS.get(oreDict.substring(0, truncIndex));
                if (tagPrefix != null) {
                    tags.add(tagPrefix + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDict.substring(truncIndex)));
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
    }
}
