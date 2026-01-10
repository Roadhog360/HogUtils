package roadhog360.hogutils.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

/// {@link Cancelable} version of the Ore Dictionary register event; for the purpose of preventing OreDictionary tags from being registered.
/// Will also cancel auto HogTag logic for this OreDictionary tag.
/// Do not register new OreDictionary values here, you are very likely to trigger infinite recursion!
@Cancelable
public class OreRegisterEventPre extends Event {
    public final String name;
    public final ItemStack stack;

    public OreRegisterEventPre(String name, ItemStack ore)
    {
        this.name = name;
        this.stack = ore;
    }
}
