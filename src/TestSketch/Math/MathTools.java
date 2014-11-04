package TestSketch.Math;

public class MathTools {
    public static float[] product(float[] a, float b) {
        float[] ret = new float[ a.length ];
        for( int i = 0; i < ret.length; ++i )
            ret[i] = a[i] * b;
        return ret;
    }
    public static float[] product(float[] a, float[] b) {
        float[] ret = new float[ Math.min(a.length, b.length) ];
        for( int i = 0; i < ret.length; ++i )
            ret[i] = a[i] * b[i];
        return ret;
    }
    public static float dotProduct(float[] a, float[] b) {
        int size = Math.min(a.length, b.length);
        float ret = 0;
        for( int i = 0; i < size; ++i )
            ret += a[i] * b[i];
        return ret;
    }
    // Modification of the normal dotProduct to support multiple components simultaneously.
    public static float[] dotProduct(float[][] a, float[] b) {
        int size = Math.min(a.length, b.length), components = a[0].length;
        if( size == 0 || components == 0 )
            return null;
        float[] ret = new float[components];
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < components; ++j )
                ret[j] += a[i][j] * b[i];
        return ret;
    }
    // The previous method applies works on a per row entry, this works on a per column entry
    public static float[] dotProduct(float[] b, float[][] a) {
        if( a.length == 0 || a[0].length == 0 )
            return null;
        float[] ret = new float[a.length];
        for( int i = 0; i < a.length; ++i )
            for( int j = 0; j < a[i].length; ++j )
                ret[i] += a[i][j] * b[j];
        return ret;
    }
    public static float length(float[] data) {
        float sum = 0;
        for( int i = 0; i < data.length; ++i )
            sum += data[i] * data[i];
        return (float)Math.sqrt(sum);
    }
    public static float[] average(float[][] in) {
        float[] ret = new float[in.length];
        for( int i = 0; i < in.length; ++i )
            ret[i] = average(in[i]);
        return ret;
    }
    public static float average(float[] in) {
        float sum = 0;
        for( int i = 0; i < in.length; ++i ) {
            sum += in[i];
        }
        return sum / ((float) in.length > 0 ? in.length : 1 );
    }
    public static float[] multiply(float scale, float[] in) {
        for( int i = 0; i < in.length; ++i )
            in[i] *= scale;
        return in;
    }
    public static float[][] multiply(float scale, float[][] in) {
        for( int i = 0; i < in.length; ++i )
            in[i] = multiply(scale, in[i]);
        return in;
    }
    public static int minMax(int val, int min, int max) {
        return ( val > max ) ? max : ( ( val < min ) ? min : val );
    }
    public static float minMax(float val, float min, float max) {
        return ( val > max ) ? max : ( ( val < min ) ? min : val );
    }
    public static float[] minMax(float[] val, float min, float max) {
        float[] ret = new float[val.length];
        for( int i = 0; i < val.length; ++i )
            ret[i] = minMax(val[i],min, max);
        return ret;
    }
    public static float cyclicMaxMin(float val, float min, float max) {
        float range = max - min;
        while( val < min )
            val += range;
        while( val > max )
            val -= range;
        return val;
    }
    public static float[] cyclicMaxMin(float[] val, float min, float max) {
        float range = max - min;
        for( int i = 0; i < val.length; ++i ) {
            while( val[i] < min )
                val[i] += range;
            while( val[i] > max )
                val[i] -= range;
        }
        return val;
    }
    public static float normalize(float val, float oldMin, float oldMax, float newMin, float newMax) {
        return normalize(val, oldMin, oldMax) * ( newMax - newMin ) + newMin;
    }
    public static float normalize(float val, float min, float max) {
        return (val-min) / (max-min);
    }
    public static float normalizeMinMax(float val, float oldMin, float oldMax, float newMin, float newMax) {
        return minMax( normalize(val, oldMin, oldMax, newMin, newMax), newMin, newMax);
    }
    public static float normalizeMinMax(float val, float oldMin, float oldMax) {
        return minMax( normalize(val, oldMin, oldMax), 0, 1);
    }
}
