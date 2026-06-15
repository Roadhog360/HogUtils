package roadhog360.hogutils.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import lombok.NonNull;
import net.minecraftforge.common.MinecraftForge;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.hogtags.handlers.ITagHandler;

/// Fires whenever a new HogTag inheritor is registered or unregistered to a handler.
/// Adding tags within this event is not advised as the inheritor system is designed for such cases.
/// If for some reason you need to use this event, take into account that adding inheritors through this event will fire it again.
///
/// The same goes for removals.
///
/// This event fires after validation checks are passed, meaning that the tag passes [HogTags#enforceTagSpec(String)] and that the addition of this inheritor does not cause recursion.
/// This event also only fires if the tag is not already in the set. For removals, the event is only fired if the tag is present and can be removed.
///
/// These event is fired on the [MinecraftForge#EVENT_BUS] and is [cpw.mods.fml.common.eventhandler.Cancelable].
public class HogTagsInheritorEvent extends Event {
    @NonNull
    public final ITagHandler<?, ?> handler;
    @NonNull
    /// The tag that will be added under the [HogTagsInheritorEvent#inheritor] tag.
    public final String inheriting;
    @NonNull
    /// The tag that will be inheriting the [HogTagsInheritorEvent#inheriting] tag.
    public final String inheritor;

    protected HogTagsInheritorEvent(@NonNull ITagHandler<?, ?> handler, @NonNull String inheriting, @NonNull String inheritor) {
        this.handler = handler;
        this.inheriting = inheriting;
        this.inheritor = inheritor;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static final class Register extends HogTagsObjectEvent {
        public Register(@NonNull ITagHandler<?, ?> handler, @NonNull Object inheriting, @NonNull String tag) {
            super(handler, inheriting, tag);
        }
    }

    public static final class Remove extends HogTagsObjectEvent {
        public Remove(@NonNull ITagHandler<?, ?> handler, @NonNull Object inheriting, @NonNull String tag) {
            super(handler, inheriting, tag);
        }
    }
}
