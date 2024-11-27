package roadhog360.hogutils.api.hogtags;

import java.util.List;

/// Used by recipes to reference a HogTag
public class HogTagReference<T> {
    private final String container;
    private final String tag;

    public HogTagReference(String container, String tag) {
        this.container = container;
        this.tag = tag;
    }

    public List<T> reference() {
        return HogTags.getObjectsForTag(container, tag);
    }
}
