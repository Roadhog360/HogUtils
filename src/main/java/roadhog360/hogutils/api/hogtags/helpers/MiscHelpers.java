package roadhog360.hogutils.api.hogtags.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.utils.GenericUtils;

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

    /// Ensures the spec of tags is enforced, and checks the passed in tags for compliance.
    /// Spec is the following:
    /// - Must have a namespace ID within.
    /// - `#` is purely for display purposes and tags in the registry do not have it.
    /// If these conditions are not met, the game will throw an {@link IllegalArgumentException}.
    public static void removeInheritorsFromItemAndBlock(String tag, String... inherits) {
        BlockTags.removeInheritors(tag, inherits);
        ItemTags.removeInheritors(tag, inherits);
    }

    /// Ensures the spec of tags is enforced, and checks the passed in tags for compliance.
    /// If the following spec is not met for any passed in tag, the game will throw an {@link IllegalArgumentException}.
    /// - Must have a properly namespaced ID. For example, `examplemod:example` is correct, but `examplemod` isn't.
    /// - `#` is purely for display purposes and tags in the registry do not have it.
    public static void enforceTagsSpec(String... tags) {
        IntStream.range(0, tags.length).forEach(i -> enforceTagSpec(tags[i]));
    }

    private static final char[] ALLOWED_CHARS = new char[]{':', '/'};

    /// Ensures the spec of tags is enforced, and checks the passed in tags for compliance.
    /// If the following spec is not met, the game will throw an {@link IllegalArgumentException}.
    /// - Must have a properly namespaced ID. For example, `examplemod:example` is correct, but `examplemod` isn't.
    /// - `#` is purely for display purposes and tags in the registry do not have it.
    /// If these conditions are not met, the game will throw an {@link IllegalArgumentException}.
    public static void enforceTagSpec(String tag) {
        if (tag == null || tag.isEmpty() || tag.equals("#") || tag.equals(":") || tag.equals("#:")) {
            throw new IllegalArgumentException("Cannot pass in empty tag (or just \"#\") to the tags registry!");
        }
        if (!GenericUtils.verifyFilenameIntegrity(tag, ALLOWED_CHARS)) {
            throw new IllegalArgumentException("Cannot instantiate tag with disallowed characters from Windows filesystem! Received [" + tag + "]");
        }
        if (tag.startsWith("#")) {
            throw new IllegalArgumentException("Tag should not start with #; the # is for display purposes only and doesn't \"exist\". Received [" + tag + "]");
        }
        if (!tag.contains(":") || tag.startsWith(":")) {
            throw new IllegalArgumentException("Tag does not adhere to the namespace ID conventions! Received [" + tag + "]");
        }
        if (!GenericUtils.verifyFilenameIntegrity(tag, ALLOWED_CHARS)) {
            throw new IllegalArgumentException("Tag must not contain chars not allowed in Windows file names! Received [" + tag + "]");
        }
    }
}
