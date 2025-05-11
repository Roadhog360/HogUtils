package roadhog360.hogutils.api.hogtags.helpers;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.HogUtils;

import java.util.stream.IntStream;

@SuppressWarnings({"unused"})
public final class MiscHelpers {
    /// Adds the following tags to both the block and its item too.
    /// Probably doesn't work for pre-init so don't put this in your block's constructor.
    ///
    /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
    /// Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
    public static void addTagsToBlockAndItem(Block object, int meta, String... tags) {
        Item item = Item.getItemFromBlock(object);
        if (item != null) {
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
        if (item != null) {
            ItemTags.removeTags(item, meta, tags);
        }
        BlockTags.removeTags(object, item != null && item.getHasSubtypes() ? meta : OreDictionary.WILDCARD_VALUE, tags);
    }

    /// Removes the following tags to both the block and its item, if one exists.
    /// Probably doesn't work for pre-init so don't put this in your block's constructor.
    ///
    /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
    /// Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
    public static void removeTagsFromBlockAndItem(Block object, String... tags) {
        removeTagsFromBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
    }

    public static void addInheritorsToItemAndBlock(String tag, String... inherits) {
        BlockTags.addInheritors(tag, inherits);
        ItemTags.addInheritors(tag, inherits);
    }

    public static void removeInheritorsFromItemAndBlock(String tag, String... inherits) {
        BlockTags.removeInheritors(tag, inherits);
        ItemTags.removeInheritors(tag, inherits);
    }

    public static void checkTagsSpec(String... tags) {
        IntStream.range(0, tags.length).forEach(i -> tags[i] = checkTagSpec(tags[i]));
    }

    public static String checkTagSpec(String tag) {
        if (tag == null || tag.isEmpty() || tag.equals("#")) {
            throw new RuntimeException("Cannot pass in empty tag (or just \"#\") to the tags registry!");
        }
        //Sanity checks passed, let's do some filtering

        if (tag.startsWith("#")) {
            tag = tag.substring(1);
        }
        if (!tag.contains(":")) {
            String domain;
            try {
                domain = Loader.instance().activeModContainer().getModId();
            } catch (Exception e) {
                domain = "minecraft";
            }
            HogUtils.LOG.warn("Adding tag " + tag + " with no domain! Assuming " + domain + ":" + tag);
            tag = domain + ":" + tag;
        }
        return tag;
    }
}
