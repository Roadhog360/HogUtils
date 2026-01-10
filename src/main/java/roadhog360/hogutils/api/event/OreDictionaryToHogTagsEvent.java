package roadhog360.hogutils.api.event;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

import java.util.List;

/// Fired when the game is registering OreDictionary values, so other mods can detect what HogUtils has tagged an item with.
/// If there's a custom OreDictionary tag you have a modern tag equivalent for you can hook into this event.
/// This event is {@link Cancelable}.
@Cancelable
public class OreDictionaryToHogTagsEvent extends Event {
    private final String oreDictTag;
    private final ItemStack stack;
    public final List<String> convertedTagsList;

    public OreDictionaryToHogTagsEvent(String oreDictTag, ItemStack stack, List<String> autoHogTags) {
        this.oreDictTag = oreDictTag;
        this.stack = stack;
        this.convertedTagsList = ImmutableList.copyOf(autoHogTags);
    }
}
