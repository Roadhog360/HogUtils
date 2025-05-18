package roadhog360.hogutils.api.hogtags.containers;

import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.hogtags.helpers.MiscHelpers;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class InheritorContainer<Type> {

    protected final Map<String, SetPair<Type>> revLookupTable;
    protected final Map<String, SetPair<String>> inheritorTable = new Object2ObjectOpenHashMap<>();
    protected final Object2ObjectFunction<String, Set<Type>> revLookupSupplier;

    public InheritorContainer(@NonNull Map<String, SetPair<Type>> revLookupTable, Object2ObjectFunction<String, Set<Type>> revLookupSupplier) {
        this.revLookupTable = revLookupTable;
        this.revLookupSupplier = revLookupSupplier;
    }

    protected boolean detectCycle(String tag, Set<String> inherited, Set<String> visited, Set<String> stack) {
        if (!stack.add(tag)) return true; // Cycle detected
        if (visited.add(tag)) {
            for (String inheritor : inherited) {
                if (detectCycle(inheritor, inherited, visited, stack)) return true;
            }
        }
        stack.remove(tag);
        return false;
    }

    protected void checkInheritorRecursion(Set<String> inheritors, String... toInherit) {
        for(String tagToCheck : toInherit) {
            Set<String> visited = new ObjectOpenHashSet<>();
            Set<String> stack = new ObjectOpenHashSet<>();

            // Check downward recursion (new tag's inheritors)
            if (detectCycle(tagToCheck, inheritors, visited, stack)) {
                /// Crash immediately to prevent stack overflows and confusion later
                throw new UnsupportedOperationException("Recursion detected when adding inheritance for: " + tagToCheck);
            }
        }
    }

    /// Gets the tags that this tag inherits, as well as any tags those inherit, and so on, then adds it to the specified list.
    /// Returns the specified list for ease of use.
    public Set<String> addInheritedRecursive(String inheritor, @Nullable Set<String> addToSet) {
        Set<String> inheritorsSet = addToSet == null ? new ObjectOpenHashSet<>() : addToSet;
        Set<String> inheritedSet = inheritorTable.getOrDefault(inheritor, SetPair.getEmpty()).getUnlocked();
        if(!inheritedSet.isEmpty()) {
            inheritorsSet.addAll(inheritedSet);
            for (String tagToInherit : inheritedSet) {
                inheritorsSet.addAll(addInheritedRecursive(tagToInherit, inheritorsSet));
            }
        }
        return inheritorsSet;
    }

    public void addInheritors(@NonNull String inheritor, @NonNull String... toInherit) {
        MiscHelpers.enforceTagsSpec(toInherit);
        MiscHelpers.enforceTagSpec(inheritor);

        Set<Type> parentObjects = revLookupTable.computeIfAbsent(inheritor, o -> new SetPair<>(new ObjectOpenHashSet<>())).getLocked();
        if (parentObjects != null) {
            for (String inheriting : toInherit) {
                for (Type object : parentObjects) {
                    revLookupTable.computeIfAbsent(inheriting, o -> new SetPair<>(new ObjectOpenHashSet<>())).getUnlocked()
                        .add(object);
                }
            }
        }

        for(String inheriting : toInherit) {
            inheritorTable.computeIfAbsent(inheritor, o -> new SetPair<>(new ObjectOpenHashSet<>())).getUnlocked().add(inheriting);
            checkInheritorRecursion(getInherited(inheriting), toInherit);
        }

        clearAllCaches(inheritor);
        clearAllCaches(toInherit);
    }

    public void removeInheritors(@NonNull String inheritor, @NonNull String... toRemove) {
        MiscHelpers.enforceTagsSpec(toRemove);
        MiscHelpers.enforceTagSpec(inheritor);

        clearAllCaches(inheritor);
        clearAllCaches(toRemove);

        Set<?> parentObjects = revLookupTable.get(inheritor).getLocked();
        if (parentObjects != null) {
            for (String inheriting : toRemove) {
                for (Object object : parentObjects) {
                    SetPair<?> tagSet = revLookupTable.get(inheriting);
                    tagSet.getUnlocked().remove(object);
                    if (!tagSet.getUnlocked().isEmpty()) {
                        revLookupTable.remove(inheriting);
                    }
                }
            }
        }

        inheritorTable.get(inheritor).getUnlocked().removeIf(s -> ArrayUtils.contains(toRemove, s));
    }

    public Set<String> getInherited(String tag) {
        return inheritorTable.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }

    /// Clears all caches for objects that are a member of these tags
    protected void clearAllCaches(String... tags) {
        for(String tag : tags) {
            for (Type biome : getInTag(tag)) {
                ((ITaggable) biome).clearCaches();
            }
        }
    }

    protected Set<Type> getInTag(String tag) {
        return revLookupSupplier.get(tag);
    }
}
