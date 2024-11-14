package roadhog360.hogutils.api.hogtags.event;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Set;

/// Used when the game is registering OreDictionary values, to return a corresponding tag.
/// If there's a custom OreDictionary tag you have a modern tag equivalent for you can hook into this event.
/// This way, you can make it so anything that registers your OreDictionary string automatically puts a tag on the object as well!
/// See HogTags$Utils#convertOreDictTag and for more details on when this is called.
/// This event is NOT {@link Cancelable}.
public class OreDictionaryToTagStringEvent extends Event {
    public final String oreDictTag;
    public final Set<String> convertedTagsList;
    public final Set<String> tagsToAdd;

    public OreDictionaryToTagStringEvent(String oreDictTag, Set<String> convertedTagsList, Set<String> tagsToAdd) {
        this.oreDictTag = oreDictTag;
        this.convertedTagsList = Collections.unmodifiableSet(convertedTagsList);
        this.tagsToAdd = tagsToAdd;
    }

}
