package roadhog360.hogutils.api.hogtags.helpers;

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class InheritorHelper {
    private InheritorHelper() {}

    public static boolean detectCycle(String tag, Set<String> inherited, Set<String> visited, Set<String> stack) {
        if (!stack.add(tag)) return true; // Cycle detected
        if (visited.add(tag)) {
            for (String inheritor : inherited) {
                if (detectCycle(inheritor, inherited, visited, stack)) return true;
            }
        }
        stack.remove(tag);
        return false;
    }

    public static void checkInheritorRecursion(Set<String> inheritors, String... toInherit) {
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
    public static Set<String> addInheritedRecursive(String inheritor, @Nullable Set<String> addToSet, @NonNull Map<String, SetPair<String>> inheritorTable) {
        Set<String> inheritorsSet = addToSet == null ? new ObjectOpenHashSet<>() : addToSet;
        Set<String> inheritedSet = inheritorTable.getOrDefault(inheritor, SetPair.getEmpty()).getUnlocked();
        if(!inheritedSet.isEmpty()) {
            inheritorsSet.addAll(inheritedSet);
            for (String tagToInherit : inheritedSet) {
                inheritorsSet.addAll(addInheritedRecursive(tagToInherit, inheritorsSet, inheritorTable));
            }
        }
        return inheritorsSet;
    }

    public static <Type> void addInheritors(@Nullable Map<String, SetPair<Type>> revLookupTable,
                                            @NonNull Map<String, SetPair<String>> inheritorTable,
                                            @NonNull String inheritor, @NonNull String... toInherit) {
        MiscHelpers.enforceTagsSpec(toInherit);
        MiscHelpers.enforceTagSpec(inheritor);

        if(revLookupTable != null) { // In case a taggable object needs its own reverse lookup logic, it can pass in null.
            Set<Type> parentObjects = revLookupTable.computeIfAbsent(inheritor, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getLocked();
            if (parentObjects != null) {
                for (String inheriting : toInherit) {
                    for (Type object : parentObjects) {
                        revLookupTable.computeIfAbsent(inheriting, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked()
                            .add(object);
                    }
                }
            }
        }

        for(String inheriting : toInherit) {
            inheritorTable.computeIfAbsent(inheritor, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked().add(inheriting);
            InheritorHelper.checkInheritorRecursion(getInherited(inheriting, inheritorTable), toInherit);
        }
    }

    public static <Type> void removeInheritors(@Nullable Map<String, SetPair<Type>> revLookupTable,
                                               @NonNull Map<String, SetPair<String>> inheritorTable,
                                               @NonNull String inheritor, @NonNull String... toRemove) {
        MiscHelpers.enforceTagsSpec(toRemove);
        MiscHelpers.enforceTagSpec(inheritor);
        if(revLookupTable != null) { // In case a taggable object needs its own reverse lookup logic, it can pass in null.
            Set<Type> parentObjects = revLookupTable.get(inheritor).getLocked();
            if (parentObjects != null) {
                for (String inheriting : toRemove) {
                    for (Type object : parentObjects) {
                        SetPair<Type> tagSet = revLookupTable.get(inheriting);
                        tagSet.getUnlocked().remove(object);
                        if (!tagSet.getUnlocked().isEmpty()) {
                            revLookupTable.remove(inheriting);
                        }
                    }
                }
            }
        }

        inheritorTable.get(inheritor).getUnlocked().removeIf(s -> ArrayUtils.contains(toRemove, s));
    }

    public static Set<String> getInherited(String tag, Map<String, SetPair<String>> inheritorTable) {
        return inheritorTable.getOrDefault(tag, SetPair.getEmpty()).getLocked();
    }
}
