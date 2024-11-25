package roadhog360.hogutils.api.hogtags.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/// {@link Cancelable} version of the Ore Dictionary register event; for the purpose of preventing OreDictionary tags from being registered.
/// Will also cancel auto-tagging logic for this OreDictionary tag.
/// Do not register new OreDictionary values here, you are very likely to trigger infinite recursion!
public class OreRegisterEventPre extends OreDictionary.OreRegisterEvent {
    public OreRegisterEventPre(String name, ItemStack ore) {
        super(name, ore);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
