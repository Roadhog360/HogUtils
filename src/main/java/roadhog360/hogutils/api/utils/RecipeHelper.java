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

    /// Registers recipes with the specified priority, check the enum comments for more information.
    /// This function checks the recipe against {@link RecipeHelper#validateItems(Object...)} to ensure everything inside it exists.
    /// This also means your recipe must be registered AFTER pre-init, where all blocks would be registered by then.
    public static void addShapelessRecipe(Priority priority, Item output, Object... objects) {
        addShapelessRecipe(priority, new ItemStack(output), objects);
    }

    /// Registers recipes with the specified priority, check the enum comments for more information.
    /// This function checks the recipe against {@link RecipeHelper#validateItems(Object...)} to ensure everything inside it exists.
    /// This also means your recipe must be registered AFTER pre-init, where all blocks would be registered by then.
    public static void addShapelessRecipe(Priority priority, Block output, Object... objects) {
        addShapelessRecipe(priority, new ItemStack(output), objects);
    }

    /// Registers recipes with the specified priority, check the enum comments for more information.
    /// This function checks the recipe against {@link RecipeHelper#validateItems(Object...)} to ensure everything inside it exists.
    /// This also means your recipe must be registered AFTER pre-init, where all blocks would be registered by then.
    public static void addShapelessRecipe(Priority priority, ItemStack output, Object... objects) {
        priority.addShapelessRecipe(output, objects);
    }

    /// Registers recipes with the specified priority, check the enum comments for more information.
    /// This function checks the recipe against {@link RecipeHelper#validateItems(Object...)} to ensure everything inside it exists.
    /// This also means your recipe must be registered AFTER pre-init, where all blocks would be registered by then.
    public static void addShapedRecipe(Priority priority, Item output, Object... objects) {
        addShapedRecipe(priority, new ItemStack(output), objects);
    }

    /// Registers recipes with the specified priority, check the enum comments for more information.
    /// This function checks the recipe against {@link RecipeHelper#validateItems(Object...)} to ensure everything inside it exists.
    /// This also means your recipe must be registered AFTER pre-init, where all blocks would be registered by then.
    public static void addShapedRecipe(Priority priority, Block output, Object... objects) {
        addShapedRecipe(priority, new ItemStack(output), objects);
    }

    /// Registers recipes with the specified priority, check the enum comments for more information.
    /// This function checks the recipe against {@link RecipeHelper#validateItems(Object...)} to ensure everything inside it exists.
    /// This also means your recipe must be registered AFTER pre-init, where all blocks would be registered by then.
    public static void addShapedRecipe(Priority priority, ItemStack output, Object... objects) {
        priority.addShapedRecipe(output, objects);
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
        if(item != null) {
            removeAllMatchingRecipes(iRecipe -> {
                ItemStack stack = iRecipe.getRecipeOutput();
                return stack != null
                    && stack.getItem() == item
                    && ((matchWildcards && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) || meta == stack.getItemDamage());
            });
        }
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
        if(item != null) {
            removeFirstMatchingRecipe(iRecipe -> {
                ItemStack stack = iRecipe.getRecipeOutput();
                return stack != null
                    && stack.getItem() == item
                    && ((matchWildcards && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) || meta == stack.getItemDamage());
            });
        }
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
    /// This is because we can't expect items and blocks to be finished registering before that.
    public static boolean validateItems(Object... objects) {
        for (Object object : objects) {
            if (object == null || object == Blocks.air) return false; //Object is null or air, assuming it is invalid, invalidate this recipe
            if (object instanceof String) continue; //Object is string, ignore this and go to the next contents

            if (object instanceof ItemStack stack) { //Object is ItemStack, let's unpack the ItemStack to figure out what to do next
                if (stack.getItem() != null && stack.getItem().delegate.name() != null) { //Check if the item inside is not null, then checks if it is registered (delegate name is not null)
                    ISubtypesBase base;
                    if(stack.getItem() instanceof ISubtypesBase item) { //Check for subtype item
                        base = item;
                    } else if (Block.getBlockFromItem(stack.getItem()) instanceof ISubtypesBase block) { //Check for subtype block
                        base = block;
                    } else { //Does not use the subtypes system, but passed other checks. We're good.
                        return true;
                    }
                    //It uses the subtypes system, the variant there will tell us if it should be enabled. If not, invalidate the recipe.
                    return base.isMetadataEnabled(stack.getItemDamage());
                } else {
                    return false; // The containing item was null, or it was not registered.
                }
            }
            if (object instanceof Item item) {
                if (item.delegate.name() == null) { //Checks if it is registered (delegate name is not null)
                    return false; //Not registered, so this recipe is not valid even though the item is not null.
                }
            }
            if (object instanceof Block block) {
                if (block.delegate.name() == null) { //Checks if it is registered (delegate name is not null)
                    return false; //Not registered, so this recipe is not valid even though the block is not null.
                }
            }
        }
        return true;
    }

    public static class Templates {
        /// Registers a recipe to compress an ore block to compress to 9 (or 4), then back to 9 (or 4)
        /// @param small If the block is "small" (compresses to/from 4)
        public void addOreBlockRecipe(Priority priority, Block oreBlock, int oreBlockMeta, Item material, int materialMeta, boolean small) {
            if(small) {
                add2by2Recipe(priority, new ItemStack(oreBlock, 1, oreBlockMeta), new ItemStack(material, 1, materialMeta));
            } else {
                add3by3Recipe(priority, new ItemStack(oreBlock, 1, oreBlockMeta), new ItemStack(material, 1, materialMeta));
            }
            addShapedRecipe(priority, new ItemStack(material, small ? 4 : 9, materialMeta),
                "x", 'x', new ItemStack(oreBlock, 1, oreBlockMeta));
        }

        /// Registers a shaped recipe, that returns the input item in a 2x2 shape, to the output stack.
        public void add2by2Recipe(Priority priority, ItemStack output, Object input) {
            addShapedRecipe(priority, output, "xx", "xx", 'x', input);
        }

        /// Registers a shaped recipe, that returns the input item in a 3x3 shape, to the output stack.
        public void add3by3Recipe(Priority priority, ItemStack output, Object input) {
            addShapedRecipe(priority, output, "xxx", "xxx", "xxx", 'x', input);
        }

        /// Registers a recipe that takes 3 of the input object across the bottom of a 3x3 crafting grid, and outputs 6 of the output block
        public void addSlabRecipe(Priority priority, Block output, int outputMeta, Object input) {
            addShapedRecipe(priority, new ItemStack(output, 6, outputMeta), "xxx", 'x', input);
        }

        /// Registers a recipe that takes 6 of the input object, in a staircase shape on a 3x3 crafting grid, and outputs 4 of the output block.
        public void addStairRecipe(Priority priority, Block output, int outputMeta, Object input) {
            addShapedRecipe(priority, new ItemStack(output, 4, outputMeta), "x  ", "xx ", "xxx", 'x', input);
        }
    }

    // Recipes need a different class to be sorted differently in the recipe sorter

    /// These get registered AFTER other shaped recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
    public static class LowPriorityShapedRecipe extends ShapedOreRecipe {
        public LowPriorityShapedRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    /// These get registered BEFORE other shaped recipes in vanilla and most mods, taking priority over them if they overlap.
    public static class HighPriorityShapedRecipe extends ShapedOreRecipe {
        public HighPriorityShapedRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    /// These get registered AFTER other shapeless recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
    public static class LowPriorityShapelessRecipe extends ShapelessOreRecipe {
        public LowPriorityShapelessRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    /// These get registered BEFORE other shapeless recipes in vanilla and most mods, taking priority over them if they overlap.
    public static class HighPriorityShapelessRecipe extends ShapelessOreRecipe {
        public HighPriorityShapelessRecipe(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
    }

    public enum Priority {
        /// Registers recipes BEFORE other shaped recipes in vanilla and most mods, taking priority over them if they overlap.
        HIGH,
        /// Registers recipes without a priority, being thrown with other recipes in no particular order.
        /// Mods that ask to have high or lower priority will be placed below and above this recipe, respectively.
        NORMAL,
        /// These get registered AFTER other shaped recipes in vanilla and most mods, allowing those recipes to take priority if they overlap.
        LOW;

        private void addShapelessRecipe(ItemStack output, Object... objects) {
            if (validateItems(output) && validateItems(objects)) {
                switch (this) {
                    case LOW -> GameRegistry.addRecipe(new LowPriorityShapelessRecipe(output, objects));
                    case NORMAL -> GameRegistry.addShapelessRecipe(output, objects);
                    case HIGH -> GameRegistry.addRecipe(new HighPriorityShapelessRecipe(output, objects));
                }
            }
        }

        private void addShapedRecipe(ItemStack output, Object... objects) {
            if (validateItems(output) && validateItems(objects)) {
                switch (this) {
                    case LOW -> GameRegistry.addRecipe(new LowPriorityShapedRecipe(output, objects));
                    case NORMAL -> GameRegistry.addShapedRecipe(output, objects);
                    case HIGH -> GameRegistry.addRecipe(new HighPriorityShapedRecipe(output, objects));
                }
            }
        }
    }
}
