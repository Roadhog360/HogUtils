package roadhog360.hogutils.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import lombok.NonNull;
import net.minecraftforge.common.MinecraftForge;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.hogtags.handlers.ITagHandler;

/// Fires whenever a new HogTag is registered or unregistered to a handler.
/// Adding tags within this event is not advised; remember tags can have inheritors, and it's recommended to use that system instead.
/// If you need to add tags via this event and for some reason inheriting doesn't work for you,
/// take care to check for recursion, as those additions will also fire this event.
///
/// The same goes for removals.
///
/// This event fires after validation checks are passed, meaning that the tag passes [HogTags#enforceTagSpec(String)].
/// This event also only fires if the tag is not already in the set. For removals, the event is only fired if the tag is present and can be removed.
///
/// These event is fired on the [MinecraftForge#EVENT_BUS] and is [cpw.mods.fml.common.eventhandler.Cancelable].
public class HogTagsObjectEvent extends Event {
    @NonNull
    public final ITagHandler<?, ?> handler;
    @NonNull
    public final Object registered;
    @NonNull
    public final String tag;

    protected HogTagsObjectEvent(@NonNull ITagHandler<?, ?> handler, @NonNull Object member, @NonNull String tag) {
        this.handler = handler;
        this.registered = member;
        this.tag = tag;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static final class Register extends HogTagsObjectEvent {
        public Register(@NonNull ITagHandler<?, ?> handler, @NonNull Object member, @NonNull String tag) {
            super(handler, member, tag);
        }
    }

    public static final class Remove extends HogTagsObjectEvent {
        public Remove(@NonNull ITagHandler<?, ?> handler, @NonNull Object member, @NonNull String tag) {
            super(handler, member, tag);
        }
    }
}
