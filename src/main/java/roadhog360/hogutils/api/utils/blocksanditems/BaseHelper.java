package roadhog360.hogutils.api.utils.blocksanditems;

import javax.annotation.Nullable;

public final class BaseHelper {
    private BaseHelper() {}

    public static String getTextureName(String name, @Nullable String domain, @Nullable String subfolder) {
        String finalName = "";

        if(domain != null) {
            finalName = domain + ":";
        }

        if(subfolder != null) {
            finalName = subfolder + "/" + finalName;
        }

        if(name.contains(":") && domain != null) {
            finalName += name.substring(0, name.indexOf(":"));
        } else {
            finalName += name;
        }

        return finalName;
    }

    public static String getUnlocalizedName(String name, @Nullable String domain) {
        return getTextureName(name, domain, null);
    }
}
