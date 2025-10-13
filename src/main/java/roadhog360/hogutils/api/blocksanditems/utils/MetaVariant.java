package roadhog360.hogutils.api.blocksanditems.utils;

import net.minecraft.util.IIcon;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public class MetaVariant {
    public static int NO_OVERRIDE = -2;

    @Nullable
    protected String displayName;
    @Nullable
    protected String iconName;
    @Nullable
    protected IIcon icon;

    public MetaVariant(@Nullable String name) {
        this(name, name);
    }

    public MetaVariant(@Nullable String displayName, @Nullable String iconName) {
        this.displayName = displayName;
        this.iconName = iconName;
    }

    public @Nullable String getDisplayName() {
        return displayName;
    }

    public @Nullable String getIconName() {
        return iconName;
    }

    public @Nullable IIcon getIcon() {
        return icon;
    }

    public void setIcon(IIcon icon) {
        this.icon = icon;
    }
}
