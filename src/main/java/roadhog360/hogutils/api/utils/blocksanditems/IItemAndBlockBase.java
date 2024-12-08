package roadhog360.hogutils.api.utils.blocksanditems;

import javax.annotation.Nullable;

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
