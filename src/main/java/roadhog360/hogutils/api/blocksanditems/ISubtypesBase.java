package roadhog360.hogutils.api.blocksanditems;

import net.minecraft.util.IIcon;

import java.util.Map;

public interface ISubtypesBase extends IItemAndBlockBase {
    Map<Integer, IIcon> getIcons();

    Map<Integer, String> getTypes();

    default boolean usesMap() {
        return !getTypes().isEmpty();
    }

    /// Can be used to "disable" specific metadatas. It can't do anything about metas already placed in the world.
    /// But it will prevent them from being placed or shown in Creative.
    default boolean isMetadataEnabled(int meta) {
        return usesMap() ? getTypes().containsKey(meta) : meta == 0;
    }
}
