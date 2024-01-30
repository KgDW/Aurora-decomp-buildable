package kassuk.addon.aurora.utils;

public class MathUtils
{
    public static int randomNum(final int min, final int max) {
        return min + (int)(Math.random() * (max - min + 1));
    }
    
    public static float random(final float min, final float max) {
        return (float)(Math.random() * (max - min) + min);
    }
    
    public static double clamp(final double num, final double min, final double max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static float clamp(final float num, final float min, final float max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static double interpolate(final double oldValue, final double newValue, final double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }
}
