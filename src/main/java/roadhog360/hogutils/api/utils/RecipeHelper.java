package roadhog360.hogutils.api.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.api.blocksanditems.ISubtypesBase;

import java.util.function.Predicate;

public final class RecipeHelper {

    private RecipeHelper() {}

    public static void init() {
        RecipeSorter.register(Tags.MOD_ID + ":shapedHiPriority",
            HighPriorityShapedRecipe.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped");
        RecipeSorter.register(Tags.MOD_ID + ":shapelessHiPriority",
            HighPriorityShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shapeless");

        RecipeSorter.register(Tags.MOD_ID + ":shapedLoPriority",
            LowPriorityShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register(Tags.MOD_ID + ":shapelessLoPriority",
            LowPriorityShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    public static void registerOre(String oreName, ItemStack ore) {
        if (validateItems(ore)) {
            OreDictionary.registerOre(oreName, ore);
        }
    }

    public static void registerOre(String oreName, Item ore) {
        if (validateItems(ore)) {
            OreDictionary.registerOre(oreName, ore);
        }
    }

    public static void registerOre(String oreName, Block ore) {
        if (validateItems(ore)) {
            OreDictionary.registerOre(oreName, ore);
        }
    }

    public static void addSmelting(Item input, ItemStack output, float exp) {
        if (validateItems(input) && validateItems(output)) {
            GameRegistry.addSmelting(input, output, exp);
        }
    }

    public static void addSmelting(Block input, ItemStack output, float exp) {
        if (validateItems(input) && validateItems(output)) {
            GameRegistry.addSmelting(input, output, exp);
        }
    }

    public static void addSmelting(ItemStack input, ItemStack output, float exp) {
        if (validateItems(input) && validateItems(output)) {
            GameRegistry.addSmelting(input, output, exp);
        }
    }
    /// These get registered BEFORE other shaped recipes in vanilla and most mods, taking priority over them if they overlap.
    public static void addHighPriorityShapedRecipe(ItemStack output, Object... objects) {
        if (validateItems(output) && validateItems(objects)) {
            GameRegistry.addRecipe(new HighPriorityShapedRecipe(output, objects));
        }
    }

    /// These get registered BEFORE other shapeless recipes in vanilla and most mods, taking priority over them if they overlap.
    public static void addHighPriorityShapelessRecipe(ItemStack output, Object... objects) {
        if (validateItems(output) && validateItems(objects)) {
            GameRegistry.addRecipe(new HighPriorityShapelessRecipe(output, objects));
        }
    }

    /// These get registered AFTER other shaped recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
    public static void addLowPriorityShapedRecipe(ItemStack output, Object... objects) {
        if (validateItems(output) && validateItems(objects)) {
            GameRegistry.addRecipe(new LowPriorityShapedRecipe(output, objects));
        }
    }

    /// These get registered AFTER other shapeless recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
    public static void addLowPriorityShapelessRecipe(ItemStack output, Object... objects) {
        if (validateItems(output) && validateItems(objects)) {
            GameRegistry.addRecipe(new LowPriorityShapelessRecipe(output, objects));
        }
    }

    /// Does not evaluate NBT of passed in stacks
    public static void removeAllRecipesWithOutput(ItemStack find) {
        removeAllRecipesWithOutput(find, true);
    }

    /// Does not evaluate NBT of passed in stacks
    public static void removeAllRecipesWithOutput(ItemStack find, boolean matchWildcards) {
        removeAllRecipesWithOutput(find.getItem(), find.getItemDamage(), matchWildcards);
    }

    public static void removeAllRecipesWithOutput(Block block, int meta, boolean matchWildcards) {
        removeAllRecipesWithOutput(Item.getItemFromBlock(block), meta, matchWildcards);
    }

    public static void removeAllRecipesWithOutput(Item item, int meta, boolean matchWildcards) {
        removeAllMatchingRecipes(iRecipe -> {
            ItemStack stack = iRecipe.getRecipeOutput();
            return stack != null
                && stack.getItem() == item
                && ((matchWildcards && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) || meta == stack.getItemDamage());
        });
    }

    /// Removes any recipe that tests true for the specific predicate.
    public static void removeAllMatchingRecipes(Predicate<IRecipe> removeCondition) {
        CraftingManager.getInstance().getRecipeList().removeIf(removeCondition);
    }

    /// Does not evaluate NBT of passed in stacks
    public static void removeFirstRecipeWithOutput(ItemStack find) {
        removeFirstRecipeWithOutput(find, true);
    }

    /// Does not evaluate NBT of passed in stacks
    public static void removeFirstRecipeWithOutput(ItemStack find, boolean matchWildcards) {
        removeFirstRecipeWithOutput(find.getItem(), find.getItemDamage(), matchWildcards);
    }

    public static void removeFirstRecipeWithOutput(Block block, int meta, boolean matchWildcards) {
        removeFirstRecipeWithOutput(Item.getItemFromBlock(block), meta, matchWildcards);
    }

    public static void removeFirstRecipeWithOutput(Item item, int meta, boolean matchWildcards) {
        removeFirstMatchingRecipe(iRecipe -> {
            ItemStack stack = iRecipe.getRecipeOutput();
            return stack != null
                && stack.getItem() == item
                && ((matchWildcards && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) || meta == stack.getItemDamage());
        });
    }

    /// Removes any recipe that tests true for the specific predicate.
    public static void removeFirstMatchingRecipe(Predicate<IRecipe> removeCondition) {
        for(IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            if(removeCondition.test(recipe)) {
                CraftingManager.getInstance().getRecipeList().remove(recipe);
                return;
            }
        }
    }

    /// Checks if the objects actually exist in the item/block registry, returns false if any of them are not registered.
    /// Useful for registering recipes and stuff, but will only really work after preInit.
    /// This is because we can't expect items and blocks to be finished rendering before that.
    public static boolean validateItems(Object... objects) {
        for (Object object : objects) {
            if (object == null || object == Blocks.air) return false;
            if (object instanceof String) continue;

            if (object instanceof ItemStack stack) {
                if (stack.getItem() == null || Item.itemRegistry.getNameForObject(stack.getItem()) == null) {
                    ISubtypesBase base = null;
                    if(stack.getItem() instanceof ISubtypesBase item) {
                        base = item;
                    } else if (Block.getBlockFromItem(stack.getItem()) instanceof ISubtypesBase block) {
                        base = block;
                    }
                    return base != null && base.isMetadataEnabled(stack.getItemDamage());
                }
            }
            if (object instanceof Item) {
                if (Item.itemRegistry.getNameForObject(object) == null) {
                    return false;
                }
            }
            if (object instanceof Block) {
                if (Block.blockRegistry.getNameForObject(object) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    // Recipes need a different class to be sorted differently in the recipe sorter

    /// These get registered AFTER other shaped recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
    public static class LowPriorityShapedRecipe extends ShapedOreRecipe {
        public LowPriorityShapedRecipe(Block result, Object... recipe) {
            super(result, recipe);
        }

        public LowPriorityShapedRecipe(Item result, Object... recipe) {
            super(result, recipe);
        }

        public LowPriorityShapedRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    /// These get registered BEFORE other shaped recipes in vanilla and most mods, taking priority over them if they overlap.
    public static class HighPriorityShapedRecipe extends ShapedOreRecipe {
        public HighPriorityShapedRecipe(Block result, Object... recipe) {
            super(result, recipe);
        }

        public HighPriorityShapedRecipe(Item result, Object... recipe) {
            super(result, recipe);
        }

        public HighPriorityShapedRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    /// These get registered AFTER other shapeless recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
    public static class LowPriorityShapelessRecipe extends ShapelessOreRecipe {
        public LowPriorityShapelessRecipe(Block result, Object... recipe) {
            super(result, recipe);
        }

        public LowPriorityShapelessRecipe(Item result, Object... recipe) {
            super(result, recipe);
        }

        public LowPriorityShapelessRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    /// These get registered BEFORE other shapeless recipes in vanilla and most mods, taking priority over them if they overlap.
    public static class HighPriorityShapelessRecipe extends ShapelessOreRecipe {
        public HighPriorityShapelessRecipe(Block result, Object... recipe) {
            super(result, recipe);
        }
        public HighPriorityShapelessRecipe(Item result, Object... recipe) {
            super(result, recipe);
        }
        public HighPriorityShapelessRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    public static enum RecipePriority {
        HIGH,
        NORMAL,
        LOW
    }
}
