package roadhog360.hogutils.api.utils;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ComparableVersion;

/// Utility class to aid in managing loaded mods. Recommended to store them in a static list of some sort.
/// The loaded state of a mod is checked lazily, to prevent accidentally "ruining" your instances should the static class be "touched" too early.
/// Comes with tools to check the mod name, get the container, compare versions and so on.
public final class ModReference {
    private final String modID;
    private ModContainer modContainer;
    private Boolean isLoaded;
    private String version;

    public ModReference(String modID) {
        this.modID = modID;
    }

    private ModContainer getModContainer() {
        if(modContainer == null) {
            ModContainer container = Loader.instance().getIndexedModList().get(modID);
            if(container != null) {
                modContainer = container;
            } else {
                throw new RuntimeException("Illegal action performed for mod that is not loaded!");
            }
        }
        return modContainer;
    }

    public boolean isLoaded() {
        if (isLoaded == null) {
            isLoaded = Loader.isModLoaded(modID);
        }
        return isLoaded;
    }

    public String getModID() {
        return modID;
    }

    public String getModName() {
        return getModContainer().getName();
    }

    public String getVersion() {
        if (version == null) {
            version = getModContainer().getProcessedVersion().getVersionString().replaceAll("§.", "");/*Remove color code values*/
        }
        return version;
    }

    public int compareVersion(String compareTo) {
        return new ComparableVersion(getVersion()).compareTo(new ComparableVersion(compareTo.replaceAll("§.", "") /*Remove color code values*/));
    }

    public boolean isVersionNewer(String compareTo) {
        return compareVersion(compareTo) > 0;
    }

    public boolean isVersionNewerOrEqual(String compareTo) {
        return compareVersion(compareTo) >= 0;
    }

    public boolean isVersionEqual(String compareTo) {
        return compareVersion(compareTo) == 0;
    }

    public boolean isVersionOlderOrEqual(String compareTo) {
        return compareVersion(compareTo) <= 0;
    }

    public boolean isVersionOlder(String compareTo) {
        return compareVersion(compareTo) < 0;
    }
}
