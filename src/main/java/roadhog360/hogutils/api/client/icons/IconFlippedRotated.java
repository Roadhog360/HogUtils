package roadhog360.hogutils.api.client.icons;

import net.minecraft.util.IIcon;
import roadhog360.hogutils.api.utils.Rotation;

/// Unfinished; doesn't even work
public class IconFlippedRotated implements IIcon {

    private final IIcon baseIcon;
    private final boolean flipU;
    private final boolean flipV;
    private final Rotation rotation;

    public IconFlippedRotated(IIcon baseIcon, boolean flipU, boolean flipV) {
        this(baseIcon, flipU, flipV, Rotation.NONE);
    }

    public IconFlippedRotated(IIcon baseIcon, boolean flipU, boolean flipV, Rotation rotation) {
        this.baseIcon = baseIcon;
        this.flipU = flipU;
        this.flipV = flipV;
        this.rotation = rotation;
    }

    public int getIconWidth()
    {
        return this.baseIcon.getIconWidth();
    }

    public int getIconHeight()
    {
        return this.baseIcon.getIconHeight();
    }

    public float getMinU() {
        return this.flipU ? getMaxURotated() : getMinURotated();
    }

    public float getMaxU() {
        return this.flipU ? getMinURotated() : getMaxURotated();
    }

    public float getMinV() {
        return this.flipV ? getMaxVRotated() : getMinVRotated();
    }

    public float getMaxV() {
        return this.flipV ? getMinVRotated() : getMaxVRotated();
    }


    public float getMinURotated() {
        return switch (rotation) {
            case CLOCKWISE_90 -> baseIcon.getMaxU();
            case CLOCKWISE_180 -> baseIcon.getMinV();
            case COUNTERCLOCKWISE_90 -> baseIcon.getMaxV();
            default -> baseIcon.getMinU();
        };
    }

    public float getMaxURotated() {
        return switch (rotation) {
            case CLOCKWISE_90 -> baseIcon.getMinV();
            case CLOCKWISE_180 -> baseIcon.getMaxV();
            case COUNTERCLOCKWISE_90 -> baseIcon.getMinU();
            default -> baseIcon.getMaxU();
        };
    }

    public float getMinVRotated() {
        return switch (rotation) {
            case CLOCKWISE_90 -> baseIcon.getMaxV();
            case CLOCKWISE_180 -> baseIcon.getMinU();
            case COUNTERCLOCKWISE_90 -> baseIcon.getMaxU();
            default -> baseIcon.getMinV();
        };
    }

    public float getMaxVRotated() {
        return switch (rotation) {
            case CLOCKWISE_90 -> baseIcon.getMinU();
            case CLOCKWISE_180 -> baseIcon.getMaxU();
            case COUNTERCLOCKWISE_90 -> baseIcon.getMinV();
            default -> baseIcon.getMaxV();
        };
    }

    public float getInterpolatedU(double p_94214_1_) {
        float f = this.getMaxU() - this.getMinU();
        return this.getMinU() + f * ((float) p_94214_1_ / 16.0F);
    }

    public float getInterpolatedV(double p_94207_1_) {
        float f = this.getMaxV() - this.getMinV();
        return this.getMinV() + f * ((float) p_94207_1_ / 16.0F);
    }

    public String getIconName() {
        return this.baseIcon.getIconName();
    }
}
