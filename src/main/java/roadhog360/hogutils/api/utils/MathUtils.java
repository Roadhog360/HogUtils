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

    /// Why? Idk I was bored
    public static boolean isPrime(long n) {
        if(n < 2) return false;
        if(n == 2 || n == 3) return true;
        if(n%2 == 0 || n%3 == 0) return false;
        long sqrtN = (long)Math.sqrt(n)+1;
        for(long i = 6L; i <= sqrtN; i += 6) {
            if(n%(i-1) == 0 || n%(i+1) == 0) return false;
        }
        return true;
    }
}
