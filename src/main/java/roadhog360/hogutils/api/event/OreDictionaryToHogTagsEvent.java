package roadhog360.hogutils.api.event;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

import java.util.List;

/// Fired when HogUtils automatically applies HogTags to an item when it is registered through OreDictionary.
/// The list is what HogTags will be added. It is immutable.
/// This event is {@link Cancelable}.
@Cancelable
public class OreDictionaryToHogTagsEvent extends Event {
    public final String oreDictTag;
    public final ItemStack stack;
    public final List<String> autoHogTags;

    public OreDictionaryToHogTagsEvent(String oreDictTag, ItemStack stack, List<String> autoHogTags) {
        this.oreDictTag = oreDictTag;
        this.stack = stack;
        this.autoHogTags = ImmutableList.copyOf(autoHogTags);
    }
}
