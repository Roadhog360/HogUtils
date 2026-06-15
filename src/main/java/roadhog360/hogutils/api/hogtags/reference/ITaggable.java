package roadhog360.hogutils.api.hogtags.reference;

import java.util.Set;

public interface ITaggable {
    void putTag(String tag);
    void removeTag(String tag);
    boolean isIn(String tag);
    Set<String> getTags();
}
