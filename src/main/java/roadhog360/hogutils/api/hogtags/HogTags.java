package roadhog360.hogutils.api.hogtags;

import cpw.mods.fml.common.Loader;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.HogUtils;
import roadhog360.hogutils.api.hogtags.interfaces.ITaggable;
import roadhog360.hogutils.api.utils.SetPair;

import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public final class HogTags {
    private HogTags() {}

    public static void checkTagsSpec(String... tags) {
        IntStream.range(0, tags.length).forEach(i -> tags[i] = checkTagSpec(tags[i]));
    }

    public static String checkTagSpec(String tag) {
        if (tag == null || tag.isEmpty() || tag.equals("#")) {
            throw new RuntimeException("Cannot pass in empty tag (or just \"#\") to the tags registry!");
        }
        //Sanity checks passed, let's do some filtering

        if (tag.startsWith("#")) {
            tag = tag.substring(1);
        }
        if (!tag.contains(":")) {
            String domain;
            try {
                domain = Loader.instance().activeModContainer().getModId();
            } catch (Exception e) {
                domain = "minecraft";
            }
            HogUtils.LOG.warn("Adding tag " + tag + " with no domain! Assuming " + domain + ":" + tag);
            tag = domain + ":" + tag;
        }
        return tag;
    }

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

    public static void checkInheritorRecursion(Set<String> inherited, String... tags) {
        for(String tagToCheck : tags) {
            Set<String> visited = new ObjectOpenHashSet<>();
            Set<String> stack = new ObjectOpenHashSet<>();

            // Check downward recursion (new tag's inheritors)
            if (detectCycle(tagToCheck, inherited, visited, stack)) {
                /// Crash immediately to prevent stack overflows and confusion later
                throw new UnsupportedOperationException("Recursion detected when adding inheritance for: " + tagToCheck);
            }

//            // Check upward recursion (does this tag lead back to itself through parents?)
//            Commented: This code may be redundant
//            for (String parent : inherited) { // Get parents of `tag`
//                if (detectCycle(parent, inherited, visited, stack)) {
//                    throw new UnsupportedOperationException("Recursion detected through upper hierarchy when adding: " + tagToCheck);
//                }
//            }
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

    public static void addInheritors(String inheritor, Map<String, SetPair<String>> inheritorTable,  String... tagsToInherit) {
        HogTags.checkTagsSpec(tagsToInherit);
        inheritor = HogTags.checkTagSpec(inheritor);
        for(String inheriting : tagsToInherit) {
            inheritorTable.computeIfAbsent(inheritor, o -> new SetPair<>(new ObjectAVLTreeSet<>())).getUnlocked().add(inheriting);
            HogTags.checkInheritorRecursion(getInherited(inheriting, inheritorTable), tagsToInherit);
        }
    }

    public static void removeInheritors(String inheritor, Map<String, SetPair<String>> inheritorTable, String... tagsToInherit) {
        HogTags.checkTagsSpec(tagsToInherit);
        inheritor = HogTags.checkTagSpec(inheritor);

        inheritorTable.get(inheritor).getUnlocked().removeIf(s -> ArrayUtils.contains(tagsToInherit, s));
    }

    public static Set<String> getInherited(String inheritor, Map<String, SetPair<String>> inheritorTable) {
        return inheritorTable.getOrDefault(inheritor, SetPair.getEmpty()).getLocked();
    }

    public static void clearAllCaches(Iterable<ITaggable<?>> iterable) {
        iterable.forEach(ITaggable::clearCaches);
    }
}
