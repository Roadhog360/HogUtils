package roadhog360.hogutils.api.utils;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

public enum Rotation {
    NONE("rotate_0"),//North facing
    CLOCKWISE_90("rotate_90"),//East
    CLOCKWISE_180("rotate_180"),//South
    COUNTERCLOCKWISE_90("rotate_270");//West

    private final String name;
    public static final Rotation[] VALUES = values();
    private static final String[] ROTATION_NAMES = new String[VALUES.length];

    static {
        int i = 0;
        for (Rotation rotation : values()) {
            ROTATION_NAMES[i++] = rotation.name;
        }
    }

    Rotation(String nameIn) {
        this.name = nameIn;
    }

    public Rotation add(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> switch (this) {
                case NONE -> CLOCKWISE_180;
                case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_180 -> NONE;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
            };
            case COUNTERCLOCKWISE_90 -> switch (this) {
                case NONE -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_90 -> NONE;
                case CLOCKWISE_180 -> CLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
            };
            case CLOCKWISE_90 -> switch (this) {
                case NONE -> CLOCKWISE_90;
                case CLOCKWISE_90 -> CLOCKWISE_180;
                case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> NONE;
            };
            default -> this;
        };
    }

    public EnumFacing rotate(EnumFacing facing) {
        if (facing.getFrontOffsetY() != 0) {
            return facing;
        }
        return switch (this) {
            case CLOCKWISE_90 -> rotateY(facing);
            case CLOCKWISE_180 ->
                GenericUtils.Constants.ENUM_FACING_VALUES[ForgeDirection.VALID_DIRECTIONS[facing.ordinal()].getOpposite().ordinal()];
            case COUNTERCLOCKWISE_90 -> rotateYCCW(facing);
            default -> facing;
        };
    }

    public EnumFacing rotateY(EnumFacing facing) {
        return switch (facing) {
            case NORTH -> EnumFacing.EAST;
            case EAST -> EnumFacing.SOUTH;
            case SOUTH -> EnumFacing.WEST;
            case WEST -> EnumFacing.NORTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    public EnumFacing rotateYCCW(EnumFacing facing) {
        return switch (facing) {
            case NORTH -> EnumFacing.WEST;
            case EAST -> EnumFacing.NORTH;
            case SOUTH -> EnumFacing.EAST;
            case WEST -> EnumFacing.SOUTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    public int rotate(int p_185833_1_, int p_185833_2_) {
        return switch (this) {
            case CLOCKWISE_90 -> (p_185833_1_ + p_185833_2_ / 4) % p_185833_2_;
            case CLOCKWISE_180 -> ((p_185833_1_ + p_185833_2_) / 2) % p_185833_2_;
            case COUNTERCLOCKWISE_90 -> (p_185833_1_ + p_185833_2_ * 3 / 4) % p_185833_2_;
            default -> p_185833_1_;
        };
    }
}
