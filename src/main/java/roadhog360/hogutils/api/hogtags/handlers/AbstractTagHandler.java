package roadhog360.hogutils.api.hogtags.handlers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraftforge.common.MinecraftForge;
import roadhog360.hogutils.api.event.HogTagsInheritorEvent;
import roadhog360.hogutils.api.event.HogTagsObjectEvent;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.utils.CachedSupplier;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/// Contains the majority of the tagging logic, suitable for most use cases.
public abstract class AbstractTagHandler<Member, MemberSet extends Set<Member>> implements ITagHandler<Member, MemberSet> {

    protected final String helpText;
    protected final String handlerID;
    protected final Map<Member, ObjectSet<String>> obj2TagsBase;
    protected final Map<String, ObjectSet<String>> inheritorTable = new Object2ObjectOpenHashMap<>();

    protected final Map<Member, CachedSupplier<ObjectSet<String>>> member2TagsCache;
    protected final Map<String, CachedSupplier<MemberSet>> tag2MembersCache;

    protected final Function<MemberSet, MemberSet> memberSetFactory;
    protected Function<Member, ObjectSet<String>> extraMembers = key -> ObjectSets.emptySet();

    public AbstractTagHandler(String helpText, String handlerID,
                              Map<Member, ObjectSet<String>> obj2TagsBase,
                              Map<Member, CachedSupplier<ObjectSet<String>>> member2TagsCache,
                              Map<String, CachedSupplier<MemberSet>> tag2MembersCache,
                              Function<MemberSet, MemberSet> memberSetFactory) {
        this.helpText = helpText;
        this.handlerID = handlerID;
        this.obj2TagsBase = obj2TagsBase;

        this.member2TagsCache = member2TagsCache;
        this.tag2MembersCache = tag2MembersCache;
        this.memberSetFactory = memberSetFactory;
    }

    @Override
    public void put(Member member, String tag) {
        HogTags.enforceTagSpec(tag);

        Set<String> tagSet = obj2TagsBase.get(member);
        if ((tagSet == null || !tagSet.contains(tag)) && !MinecraftForge.EVENT_BUS.post(new HogTagsObjectEvent.Register(this, member, tag))) {
            obj2TagsBase.computeIfAbsent(member, o -> new ObjectOpenHashSet<>(new String[]{tag}));
            clearCaches();
        }
    }

    @Override
    public void remove(Member member, String tag) {
        Set<String> tagSet = obj2TagsBase.get(member);
        if (tagSet != null && tagSet.contains(tag) && !MinecraftForge.EVENT_BUS.post(new HogTagsObjectEvent.Remove(this, member, tag))) {
            tagSet.remove(tag);
            clearCaches();
        }
    }

    @Override
    public boolean isIn(Member member, String tag) {
        return getMembers(tag).get().contains(member);
    }

    @Override
    public CachedSupplier<ObjectSet<String>> getTags(Member member) {
        return member2TagsCache.computeIfAbsent(member, (o) -> new CachedSupplier<>(() -> {
            ObjectSet<String> result = new ObjectOpenHashSet<>(obj2TagsBase.getOrDefault(o, ObjectSets.emptySet()));
            result.addAll(extraMembers.apply(member));
            if (obj2TagsBase.isEmpty()) {
                return ObjectSets.emptySet();
            }

            for (String tag : result) {
                ObjectSet<String> inheriting = getInheritors(tag);
                result.addAll(inheriting);
            }
            return ObjectSets.unmodifiable(result);
        }));
    }

    @Override
    public CachedSupplier<MemberSet> getMembers(String tag) {
        return tag2MembersCache.computeIfAbsent(tag, (t) -> new CachedSupplier<>(() -> {
            Set<String> traverse = new ObjectOpenHashSet<>(new String[]{t});
            if(inheritorTable.containsKey(t)) {
                traverse.addAll(getInheritors(t));
            }

            MemberSet result = memberSetFactory.apply(null);
            for(Map.Entry<Member, ObjectSet<String>> obj : obj2TagsBase.entrySet()) {
                Set<String> memberTags = obj.getValue();
                if(memberTags.stream().anyMatch(traverse::contains)) {
                    result.add(obj.getKey());
                }
            }
            return memberSetFactory.apply(result);
        }));
    }

    @Override
    public String getHandlerName() {
        return handlerID;
    }

    @Override
    public String getHelpSyntax() {
        return helpText;
    }

    @Override
    public void dump(boolean flat) {
        // TODO: This
    }

    public void putInheritor(String inheritor, String inheriting) {
        HogTags.enforceTagSpec(inheriting);
        HogTags.enforceTagSpec(inheritor);

        // TODO: Check inheritor recursion, to crash now before producing a StackOverflowError later
        Set<String> result = inheritorTable.get(inheritor);
        if((result == null || !result.contains(inheriting)) && !MinecraftForge.EVENT_BUS.post(new HogTagsInheritorEvent.Register(this, inheriting, inheritor))) {
            inheritorTable.computeIfAbsent(inheritor, o -> new ObjectOpenHashSet<>(new String[]{inheriting}));
            clearCaches();
        }
    }

    public void removeInheritor(String inheritor, String inheriting) {
        Set<String> result = inheritorTable.get(inheritor);
        if(result != null && result.contains(inheriting) && !MinecraftForge.EVENT_BUS.post(new HogTagsInheritorEvent.Remove(this, inheriting, inheritor))) {
            result.remove(inheriting);
            clearCaches();
        }
    }

    public ObjectSet<String> getInheritors(String inheritor) {
        ObjectSet<String> inheritorSet = new ObjectOpenHashSet<>(inheritorTable.getOrDefault(inheritor, ObjectSets.emptySet()));
        for(String s : inheritorSet) {
            inheritorSet.addAll(getInheritors(s));
        }
        return ObjectSets.unmodifiable(inheritorSet);
    }

    @Override
    public void clearCaches() {
        member2TagsCache.values().forEach(CachedSupplier::clear);
        tag2MembersCache.values().forEach(CachedSupplier::clear);
    }
}
