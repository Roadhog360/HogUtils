package roadhog360.hogutils.api.hogtags.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.Collections;
import java.util.List;

/// Used when the game is registering OreDictionary values, to return a corresponding tag.
/// If there's a custom OreDictionary tag you have a modern tag equivalent for you can hook into this event.
/// This way, you can make it so anything that registers your OreDictionary string automatically puts a tag on the object as well!
/// See HogTags$Utils#convertOreDictTag and for more details on when this is called.
/// This event is NOT {@link Cancelable}.
public class OreDictionaryToTagStringEvent extends Event {
    public final String oreDictTag;
    public final List<String> convertedTagsList;
    public final List<String> tagsToAdd;

    public OreDictionaryToTagStringEvent(String oreDictTag, List<String> convertedTagsList, List<String> tagsToAdd) {
        this.oreDictTag = oreDictTag;
        this.convertedTagsList = Collections.unmodifiableList(convertedTagsList);
        this.tagsToAdd = tagsToAdd;
    }

}
