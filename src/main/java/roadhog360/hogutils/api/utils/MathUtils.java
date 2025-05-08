package roadhog360.hogutils.api.utils;

import net.minecraft.util.MathHelper;

public class MathUtils {
    public static final float SQRT_2 = MathHelper.sqrt_float(2.0F);

    public static double max(double... vals) {
        double prevMax = Double.MIN_VALUE;
        for(double val : vals) {
            prevMax = Math.max(val, prevMax);
        }
        return prevMax;
    }

    public static float max(float... vals) {
        float prevMax = Float.MIN_VALUE;
        for(float val : vals) {
            prevMax = Math.max(val, prevMax);
        }
        return prevMax;
    }

    public static int max(int... vals) {
        int prevMax = Integer.MIN_VALUE;
        for(int val : vals) {
            prevMax = Math.max(val, prevMax);
        }
        return prevMax;
    }

    public static double min(double... vals) {
        double prevMax = Double.MAX_VALUE;
        for(double val : vals) {
            prevMax = Math.min(val, prevMax);
        }
        return prevMax;
    }

    public static float min(float... vals) {
        float prevMax = Float.MAX_VALUE;
        for(float val : vals) {
            prevMax = Math.min(val, prevMax);
        }
        return prevMax;
    }

    public static int min(int... vals) {
        int prevMax = Integer.MAX_VALUE;
        for(int val : vals) {
            prevMax = Math.min(val, prevMax);
        }
        return prevMax;
    }
}
