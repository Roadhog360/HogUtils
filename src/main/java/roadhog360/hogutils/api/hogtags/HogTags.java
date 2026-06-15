package roadhog360.hogutils.api.hogtags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.hogtags.handlers.ITagHandler;
import roadhog360.hogutils.api.hogtags.handlers.impl.BiomeTags;
import roadhog360.hogutils.api.hogtags.handlers.impl.TagHandlerMeta;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.Collection;
import java.util.Map;

public class HogTags {
    private static final Map<String, ITagHandler<?, ?>> TAG_HANDLERS = new Object2ObjectOpenHashMap<>();

    public static final BiomeTags BIOMES = new BiomeTags();
    public static final TagHandlerMeta.BlockTags BLOCKS = new TagHandlerMeta.BlockTags();
    public static final TagHandlerMeta.ItemTags ITEMS = new TagHandlerMeta.ItemTags();

    static {
        registerTagHandler(BIOMES);
        registerTagHandler(BLOCKS);
        registerTagHandler(ITEMS);
    }

    public static void registerTagHandler(ITagHandler<?, ?> handler) {
        TAG_HANDLERS.put(handler.getHandlerName(), handler);
    }

    @Nullable
    public static <T, S> ITagHandler<T, S> getTagHandler(String name) {
        return (ITagHandler<T, S>) TAG_HANDLERS.get(name);
    }

    public static Collection<ITagHandler<?, ?>> getHandlers() {
        return TAG_HANDLERS.values();
    }

    private static final char[] ALLOWED_CHARS = new char[]{':', '/'};
    /// Ensures the spec of tags is enforced, and checks the passed in tag for compliance.
    /// - Must have a properly namespaced ID. For example, `examplemod:example` is correct, but `example`, `:example` aren't.
    ///   - Something like `example:block:test` is also valid, the text before the first colon is assumed to be the domain.
    /// - `#` is purely for display purposes and tags in the registry do not have it, thus a tag passed through here shouldn't be prefixed with it.
    /// - Must not contain any characters disallowed by the Windows filesystem. (except for `:`, and `/`)
    /// If any of these violations are found, the game will throw an {@link IllegalArgumentException}.
    @Contract("null -> fail")
    public static void enforceTagSpec(String tag) {
        if (tag == null || tag.isEmpty()) {
            throw new IllegalArgumentException("Cannot pass in empty tag to the tags registry!");
        }
        if (tag.startsWith("#")) {
            throw new IllegalArgumentException("Tag should not start with #; the # is for display purposes only and doesn't \"exist\". Received [" + tag + "]");
        }
        if (!tag.contains(":") || tag.startsWith(":")) {
            throw new IllegalArgumentException("Tag does not adhere to the namespace ID conventions! Received [" + tag + "]");
        }
        if (!GenericUtils.verifyFilenameIntegrity(tag, ALLOWED_CHARS)) {
            throw new IllegalArgumentException("Tag must not contain chars not allowed in Windows file names! Received [" + tag + "]");
        }
    }
}
