package roadhog360.hogutils.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;

/// Fires when a recipe is registered in [GameRegistry]. This event is [Cancelable], and is fired on the [MinecraftForge#EVENT_BUS]
/// You can also pass through a new recipe if you want. If it's null, the recipe will not be overridden.
public class RecipeRegisterEvent extends Event {
    private final IRecipe recipe;

    public RecipeRegisterEvent(IRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
