package roadhog360.hogutils.api.hogtags.handlers;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.utils.CachedSupplier;

/// Tag handler interface, to be registered in [HogTags]. You probably want to extend [AbstractTagHandler].
@ApiStatus.Experimental
public interface ITagHandler<Member, MemberSet> {
    // operations

    /// Adds the following object as a member of this tag.
    /// This method should generally call [this#clearCaches()] if it succeeds.
    void put(Member member, String tag);
    /// Removes a member from a tag.
    /// This method should generally call [this#clearCaches()] if it succeeds.
    void remove(Member member, String tag);
    /// Checks if this object is a member of this tag.
    boolean isIn(Member member, String tag);
    /// Clears the caches for this tag container.
    void clearCaches();

    // suppliers

    /// Gets all tags this object is a member of.
    /// The CachedSupplier a specific member returns should never be replaced once created, only updated.
    CachedSupplier<ObjectSet<String>> getTags(Member member);
    /// Gets all members of this tag.
    /// The CachedSupplier a specific tag returns should never be replaced once created, only updated.
    CachedSupplier<MemberSet> getMembers(String tag);


    // commands

    String getHandlerName();
    String getHelpSyntax();
    String getNameFromObject(Member member);
    Member getObjectFromName(String string);
    void dump(boolean flat);

    // inheritors

    /// Makes the second tag inherited by the first.
    /// When [this#getMembers(String)], or [this#isIn(Object, String)] is called, the inheriting tag will also be in the list.
    /// This method should generally call [this#clearCaches()] if it succeeds.
    void putInheritor(String inheritor, String inheriting);
    /// This method should generally call [this#clearCaches()] if it succeeds.
    void removeInheritor(String inheritor, String inheriting);
    /// Returns the tags this inheritor is currently inheriting.
    /// This list does not need to be persistent.
    ObjectSet<String> getInheritors(String inheritor);
}
