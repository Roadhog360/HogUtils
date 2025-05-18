package roadhog360.hogutils.api.hogtags.containers;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.hogtags.helpers.MiscHelpers;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/// Used as a partner class for {@link ITaggable} objects since interfaces can't have non-static fields in them.
/// Since I can't put non-static fields in an interface, I'll also give every ITaggable object one of these containers so we don't have to copy the code to each one.
/// Constructor is protected since every taggable object should get their own class that extends this,
/// for the reason that is where the static helper functions will be going.
///
/// This class is not strictly necessary for a taggable object and if you need your own tagging code then you should only create
/// a class like this if you're mixing in taggability to something that you don't have direct access to.
/// This is so you can more easily "carry" code over to the helper class
public abstract class TagContainerBasic<Type> {

    protected final Map<String, SetPair<Type>> revLookupTable;
    protected final InheritorContainer<Type> inheritorContainer;

    protected final Type taggable;

    protected final Set<String> tags = new ObjectOpenHashSet<>();
    protected Set<String> lookupCache;

    /// Both of these should be static lists, that are passed into every TagContainer of this type.
    protected TagContainerBasic(@NonNull Map<String, SetPair<Type>> revLookupTable,
                                @NonNull InheritorContainer<Type> inheritorContainer,
                                @NonNull Type containerObject) {
        this.revLookupTable = revLookupTable;
        this.inheritorContainer = inheritorContainer;
        this.taggable = containerObject;
    }

    public synchronized void addTags(String... tags) {
        MiscHelpers.enforceTagsSpec(tags);
        Collections.addAll(this.tags, tags);

        // Maintain reverse lookup table
        for(String tag : tags) {
            revLookupTable.computeIfAbsent(tag, o -> new SetPair<>(new ObjectOpenHashSet<>())).getUnlocked().add(taggable);
        }

        clearCaches();
    }

    public synchronized void removeTags(String... tags) {
        MiscHelpers.enforceTagsSpec(tags);
        this.tags.removeIf(s -> ArrayUtils.contains(tags, s));

        for(String tag : tags) {
            SetPair<Type> tagSet = revLookupTable.get(tag);
            tagSet.getUnlocked().remove(taggable);
            if(tagSet.getUnlocked().isEmpty()) {
                revLookupTable.remove(tag);
                //TODO: I did this to prevent possible memory leaks but do I really need to be doing this?
            }
        }

        clearCaches();
    }

    public synchronized Set<String> getTags() {
        if(lookupCache != null) {
            return lookupCache;
        }

        if(!getBaseTags().isEmpty()) {
            lookupCache = new ObjectOpenHashSet<>(getBaseTags());

            for(String tag : lookupCache) {
                inheritorContainer.addInheritedRecursive(tag, lookupCache);
            }

            lookupCache = Collections.unmodifiableSet(lookupCache);
            return lookupCache;
        }

        return Collections.emptySet();
    }

    protected Set<String> getBaseTags() {
        return tags;
    }

    public synchronized void clearCaches() {
        lookupCache = null;
    }
}
