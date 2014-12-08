package TestSketch.Math;

public class MathTools {

    // multiplies every value in the value by the scalar, essentially vector scalar multiplication
    // TODO: this and the method multiply below do the same thing, refactor?
    public static float[] product(float[] a, float b) {
        float[] ret = new float[ a.length ];
        for( int i = 0; i < ret.length; ++i )
            ret[i] = a[i] * b;
        return ret;
    }

    // multiples each value in the first matrix with the value in the corresponding index of the second matrix
    public static float[] product(float[] a, float[] b) {
        float[] ret = new float[ Math.min(a.length, b.length) ];
        for( int i = 0; i < ret.length; ++i )
            ret[i] = a[i] * b[i];
        return ret;
    }
    
    public static Vector difference( Vector a, Vector b ) {
        int size = Math.min( a.getSize(), b.getSize() );
        float[] data = new float[ size ];
        for( int i = 0; i < size; ++i )
            data[i] = a.getComponent(i) - b.getComponent(i);
        return new Vector(data);
    }

    // dot product with arrays; if lengths differ, uses the shorter length
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

    // input vector (array), returns length from 0 vector in same dimension
    public static float length(float[] data) {
        float sum = 0;
        for( int i = 0; i < data.length; ++i )
            sum += data[i] * data[i];
        return (float)Math.sqrt(sum);
    }
    public static float[] averageComponents(float[][] in) {
        float[] ret = new float[in.length];
        for( int i = 0; i < in.length; ++i )
            ret[i] = average(in[i]);
        return ret;
    }
    // returns the average of an array (or matrix)
    public static float average(float[][] in) {
        float sum = 0;
        int count = 0, i , j;
        for( i = 0; i < in.length; ++i ) {
            for( j = 0; j < in[i].length; ++j ) {
                sum += in[i][j];
                ++count;
            }
        }
        return sum / ( count > 0 ? count : 1 );
    }
    public static float average(float[] in) {
        return sum(in) / ((float) in.length > 0 ? in.length : 1 );
    }
    // sum up an array of values
    public static float sum(float[] in) {
        float sum = 0;
        for( int i = 0; i < in.length; ++i )
            sum += in[i];
        return sum;
    }
    public static float sum(float[][] in) {
        float sum = 0;
        int i , j;
        for( i = 0; i < in.length; ++i )
            for( j = 0; j < in[i].length; ++j )
                sum += in[i][j];
        return sum;
    }

    // multiply an array (or matrix) by a scalar
    // TODO: overlap with product above, refactor?
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

    // make sure a value is within the given range (cap at max and min)
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

    // make sure a value is within a range by cycling the value through the range
    // (eg. 156 in range 25-50 will return 31, -22 in range 0-10 will return 8)
    public static int cyclicMinMax(int val, int min, int max) {
        int range = max - min;
        while( val < min )
            val += range;
        while( val > max )
            val -= range;
        return val;
    }
    public static float cyclicMinMax(float val, float min, float max) {
        float range = max - min;
        while( val < min )
            val += range;
        while( val > max )
            val -= range;
        return val;
    }
    // same as above but with an array of values
    public static float[] cyclicMinMax(float[] val, float min, float max) {
        float range = max - min;
        for( int i = 0; i < val.length; ++i ) {
            while( val[i] < min )
                val[i] += range;
            while( val[i] > max )
                val[i] -= range;
        }
        return val;
    }

    // normalize a value from one rane to another range
    public static float normalize(float val, float oldMin, float oldMax, float newMin, float newMax) {
        return normalize(val, oldMin, oldMax) * ( newMax - newMin ) + newMin;
    }

    // normalize a value in one range to the range [0,1]
    public static float normalize(float val, float min, float max) {
        return (val-min) / (max-min);
    }

    // normalize a value in a range, but also make sure that the returned value is within the given range
    public static float normalizeMinMax(float val, float oldMin, float oldMax, float newMin, float newMax) {
        return minMax( normalize(val, oldMin, oldMax, newMin, newMax), newMin, newMax);
    }
    // same as above but in range [0,1]
    public static float normalizeMinMax(float val, float oldMin, float oldMax) {
        return minMax( normalize(val, oldMin, oldMax), 0, 1);
    }

    public static boolean isNaN(float val) {
        return val != val;
    }
    public static float distanceSquared( float x, float y) {
        return x * x + y * y;
    }
    public static float distance( float x, float y ) {
        return sqrt( x * x + y * y );
    }
    public static float distanceSquared( float x1, float y1, float x2, float y2 ) {
        return distanceSquared( x1-x2, y1-y2 );
    }
    public static float distance( float x1, float y1, float x2, float y2 ) {
        return distance( x1-x2, y1-y2 );
    }
    public static float distance(Vector a, Vector b) {
        float ret = 0;
        int len = Math.min(a.getSize(), b.getSize());
        for( int i = 0; i < len; ++i )
            ret += square(a.getComponent(i)-b.getComponent(i));
        return sqrt(ret);
    }
    public static float distance( float[] point ) {
        float sum = 0;
        for( int i = 0; i < point.length; ++i )
            sum += point[i] * point[i];
        return sqrt(sum);
    }
    public static float[] minMaxLength( float[] point, float min, float max ) {
        float dist = distance( point );
        if( dist >= min && dist <= max )
            return point;
        float scale = ( dist > max ? max : min ) / dist;
        for( int i = 0; i < point.length; ++i )
            point[i] *= scale;
        return point;
    }
    public static float invert(float val) {
        if( val != 0 )
            val = 1f / val;
        return val;
    }
    public static float min(float a, float b) {
        return a > b ? b : a;
    }
    public static float max(float a, float b) {
        return a > b ? a : b;
    }
    public static int floor(float val) {
        return (int)Math.floor((double)val);
    }
    public static int ceil(float val) {
        return (int)Math.ceil((double)val);
    }
    public static int abs(int a) {
        return a < 0 ? -a : a;
    }
    public static float abs(float a) {
        return a < 0 ? -a : a;
    }
    public static float square( float val ) {
        return val*val;
    }
    public static int round(float val) {
        float r = val % 1;
        return (int)(r >= 0.5f ? val - r + 1 : val - r);
    }
    public static float sqrt(float val) {
        return (float)Math.sqrt(val);
    }
    public static float sin(float theta) {
        return (float)Math.sin(theta);
    }
    public static float cos(float theta) {
        return (float)Math.cos(theta);
    }
    public static float atan2(float x, float y) {
        return (float)Math.atan2(x, y);
    }
    public static float[] polarToCartesian(float r, float theta) {
        return new float[] { r * sin(theta), r * cos(theta) };
    }
    public static float[] cartesianToPolar(float x, float y) {
        return new float[] { distance(x, y), atan2(x, y) };
    }
}
