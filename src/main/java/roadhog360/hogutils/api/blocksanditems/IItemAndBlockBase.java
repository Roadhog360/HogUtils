package roadhog360.hogutils.api.blocksanditems;

import org.jetbrains.annotations.Nullable;

public interface IItemAndBlockBase {
    @Nullable
    String getTextureDomain(String textureName);

    @Nullable
    default String getTextureSubfolder(String textureName) {
        return null;
    }

    @Nullable
    String getNameDomain(String unlocalizedName);
}
