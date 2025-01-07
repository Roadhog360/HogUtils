package roadhog360.hogutils.api.hogtags;

import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class HogTagsHelper {

    private HogTagsHelper() {}

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

        /// Removes the following tags to both the block and its item, if one exists.
        /// Probably doesn't work for pre-init so don't put this in your block's constructor.
        ///
        /// NOTE: Things like signs, beds, and skulls use a SEPARATE ITEM for their block.
        /// Which means this function will not tag things that do that. Make sure when using this on a block it actually has an ItemBlock!!!
        public static void removeTagsFromBlockAndItem(Block object, String... tags) {
            removeTagsFromBlockAndItem(object, OreDictionary.WILDCARD_VALUE, tags);
        }

        public static void addInheritorsToItemAndBlock(String tag, String... inherits) {
            HogTags.addInheritorsToTag(BlockTags.CONTAINER_ID, tag, inherits);
            HogTags.addInheritorsToTag(ItemTags.CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritorsFromItemAndBlock(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(BlockTags.CONTAINER_ID, tag, inherits);
            HogTags.removeInheritorsFromTag(ItemTags.CONTAINER_ID, tag, inherits);
        }
    }

    public static class ItemTags {
        public static final String CONTAINER_ID = "minecraft:items";

        /// Adds the following tags to the specified item.
        public static void addTags(Item item, int meta, String... tags) {
            if(!RecipeHelper.validateItems(item)) return;
            HogTags.addTagsToObject(CONTAINER_ID, RegistryMapping.of(item, meta), tags);
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
            HogTags.removeTagsFromObject(CONTAINER_ID, RegistryMapping.of(item, meta), tags);
        }

        public static void removeTags(Item item, String... tags) {
            removeTags(item, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Get the tags for the passed in item. You can pass in a Block's ItemBlock, too.
        /// (Typically obtained through {@link Item#getItemFromBlock(Block)})
        public static Set<String> getTags(Item item, int meta) {
            return HogTags.getTagsFromObject(CONTAINER_ID, RegistryMapping.of(item, meta));
        }

        /// Get the Items for the passed in tag via {@link RegistryMapping<Item>} objects.
        ///
        /// {@link RegistryMapping<Item>#getObject()} gets the {@link Item} object, whilst {@link RegistryMapping<Item>#getMeta()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
        /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
        public static Set<RegistryMapping<Item>> getInTag(String tag) {
            return HogTags.getObjectsForTag(CONTAINER_ID, tag);
        }

        /// Get the Items for the passed in tag. Returns them as {@link Pair}<{@link Item}, {@link Integer}> objects,
        /// which is the underlying type for a {@link RegistryMapping} extends, which {@link RegistryMapping<Item>} uses.
        ///
        /// Useful if you're accessing the registry via reflection, as a {@link Pair} would be easier to work with in that context.
        /// {@link Pair#getLeft()} gets the {@link Item} object, whilst {@link Pair#getRight()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
        @SuppressWarnings("unchecked")
        public static Set<Pair<Item, Integer>> getPairsInTag(String tag) {
            return (Set<Pair<Item, Integer>>) (Object) getInTag(tag);
        }

        /// Get the items for the passed in tag. Returns them as {@link ItemStack} objects of stack size 1.
        /// Useful if you're accessing the registry via reflection, as an {@link ItemStack} would be easier to work with.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static List<ItemStack> getItemStacksInTag(String tag) {
            return HogTags.<RegistryMapping<Item>>getObjectsForTag(CONTAINER_ID, tag)
                .stream().map(RegistryMapping::newItemStack).collect(Collectors.toCollection(ObjectArrayList::new));
        }

        /// Returns true if the passed in item has any of the listed tags.
        public static boolean hasAnyTag(Item item, int meta, String... tags) {
            if(item == null) return false;
            return getTags(item, meta).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }

        public static void addInheritors(String tag, String... inherits) {
            HogTags.addInheritorsToTag(CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritors(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(CONTAINER_ID, tag, inherits);
        }

        public static Set<String> getInheritors(String tag) {
            return HogTags.getInheritors(CONTAINER_ID, tag);
        }
    }

    public static class BlockTags {
        public static final String CONTAINER_ID = "minecraft:blocks";

        /// Adds the following tags to the specified block.
        public static void addTags(Block block, int meta, String... tags) {
            if(!RecipeHelper.validateItems(block)) return;
            HogTags.addTagsToObject(CONTAINER_ID, RegistryMapping.of(block, meta), tags);
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
            HogTags.removeTagsFromObject(CONTAINER_ID, RegistryMapping.of(block, meta), tags);
        }

        public static void removeTags(Block block, String... tags) {
            removeTags(block, OreDictionary.WILDCARD_VALUE, tags);
        }

        /// Get the tags for the passed in block.
        public static Set<String> getTags(Block block, int meta) {
            return HogTags.getTagsFromObject(CONTAINER_ID, RegistryMapping.of(block, meta));
        }

        /// Get the blocks for the passed in tag via {@link RegistryMapping<Block>} objects.
        ///
        /// {@link RegistryMapping<Block>#getObject()} gets the {@link Block} object, whilst {@link RegistryMapping<Block>#getMeta()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
        /// The underlying object is a {@link Pair}, so {@link Pair#getLeft()} and {@link Pair#getRight()} respectively may also be used.
        public static Set<RegistryMapping<Block>> getInTag(String tag) {
            return HogTags.getObjectsForTag(CONTAINER_ID, tag);
        }

        /// Get the blocks for the passed in tag. Returns them as {@link Pair}<{@link Block}, {@link Integer}> objects,
        /// which is the underlying type for a {@link RegistryMapping} extends, which {@link RegistryMapping<Block>} uses.
        ///
        /// Useful if you're accessing the registry via reflection, as a {@link Pair} would be easier to work with in that context.
        /// {@link Pair#getLeft()} gets the {@link Block} object, whilst {@link Pair#getRight()} retrieves its metadata.
        /// This metadata can be {@link OreDictionary#WILDCARD_VALUE}, which means the tag is added to every metadata list of that item.
        @SuppressWarnings("unchecked")
        public static Set<Pair<Block, Integer>> getPairsInTag(String tag) {
            return (Set<Pair<Block, Integer>>) (Object) getInTag(tag);
        }

        /// Get the blocks for the passed in tag. Returns them as {@link ItemStack} objects of stack size 1.
        /// Useful if you're accessing the registry via reflection, as an {@link ItemStack} would be easier to work with.
        /// Note that any returned blocks which do not have an {@link ItemBlock} will not appear in this list.
        ///
        /// Returns a new list independent of the ones in the registry, meaning it
        /// may be mutated freely without making your own list to put them in for mutation.
        public static List<ItemStack> getItemStacksInTag(String tag) {
            return HogTags.<RegistryMapping<Block>>getObjectsForTag(CONTAINER_ID, tag)
                .stream().map(RegistryMapping::newItemStack).collect(Collectors.toCollection(ObjectArrayList::new));
        }

        /// Returns true if the passed in block has any of the listed tags.
        public static boolean hasAnyTag(Block block, int meta, String... tags) {
            if(block == null) return false;
            return getTags(block, meta).stream().anyMatch(tag -> ArrayUtils.contains(tags, tag));
        }

        public static void addInheritors(String tag, String... inherits) {
            HogTags.addInheritorsToTag(CONTAINER_ID, tag, inherits);
        }

        public static void removeInheritors(String tag, String... inherits) {
            HogTags.removeInheritorsFromTag(CONTAINER_ID, tag, inherits);
        }

        public static Set<String> getInheritors(String tag) {
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
        public static Set<String> getTags(BiomeGenBase biome) {
            return HogTags.getTagsFromObject(CONTAINER_ID, biome);
        }

        /// Get the tags for the passed in biome via its ID.
        public static Set<String> getTags(int id) {
            return getTags(getBiomeFromID(id));
        }

        /// Get the biomes for the passed in tag via RegistryMapping
        public static Set<BiomeGenBase> getInTag(String tag) {
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

        public static void addInheritors(String tag, String... inheritors) {
            HogTags.addInheritorsToTag(CONTAINER_ID, tag, inheritors);
        }

        public static void removeInheritors(String tag, String... inheritors) {
            HogTags.removeInheritorsFromTag(CONTAINER_ID, tag, inheritors);
        }

        public static Set<String> getInheritors(String tag) {
            return HogTags.getInheritors(CONTAINER_ID, tag);
        }
    }
}
