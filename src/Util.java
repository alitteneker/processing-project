import processing.core.*;

public class Util {
    public static float dotProduct(float[] a, float[] b) {
        int size = Math.min(a.length, b.length);
        float ret = 0;
        for( int i = 0; i < size; ++i )
            ret += a[i] * b[i];
        return ret;
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
    // Modification of the normal dotProduct to support multiple components simultaneously.
    public static float[] dotProduct(float[][] a, float[] b) {
        int size = Math.min(a.length, b.length), components = a[0].length;
        if( size == 0 || components == 0 )
            return null;
        float[] ret = new float[components];
        for( int i = 0; i < size; ++i ) {
            for( int j = 0; j < components; ++j ) {
                ret[j] += a[i][j] * b[i];
            }
        }
        return ret;
    }
    public static int getPixelIndex(int x, int y, int width) {
        return x + ( y * width );
    }
    
    public static int maxMin(int val, int min, int max) {
        return ( val > max ) ? max : ( ( val < min ) ? min : val );
    }
    public static double maxMin(double val, double min, double max) {
        return ( val > max ) ? max : ( ( val < min ) ? min : val );
    }
    public static float maxMin(float val, float min, float max) {
        return ( val > max ) ? max : ( ( val < min ) ? min : val );
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
    
    // N.B.: THESE METHODs WILL NOT LOAD PIXEL DATA BEFORE BEGINNING
    public static float[][] getPixelsARGB(int center_x, int center_y, int size, int[] pixels, int width, int height, PApplet applet) {
        int data[] = getPixels(center_x, center_y, size, pixels, width, height, applet);
        float[][] ret = new float[data.length][4];
        for( int i = 0; i < data.length; ++i )
            ret[i] = getPixelARGB(data[i], applet);
        return ret;
    }
    public static int[] getPixels(int center_x, int center_y, int size, int[] pixels, int width, int height, PApplet applet) {
        int[] ret = new int[size*size];
        int limit = size/2;
        for( int x = -limit; x <= limit; ++x ) {
            for( int y = -limit; y <= limit; ++y ) {
                int locx = maxMin(center_x + x, 0, width - 1), locy = maxMin(center_y + y, 0, height - 1);
                ret[ ( limit + x ) + ( ( y + limit ) * size ) ] = getPixel(locx, locy, width, pixels);
            }
        }
        return ret;
    }
    public static int getPixel(int x, int y, PImage img) {
        return getPixel(x,y,img.width,img.pixels);
    }
    public static int getPixel(int x, int y, int width, int[] pixels) {
        return pixels[getPixelIndex(x,y,width)];
    }
    public static float[] getPixelARGB(int color, PApplet applet) {
        float[] ret = new float[4];
        ret[0] = applet.alpha(color);
        ret[1] = applet.red(color);
        ret[2] = applet.green(color);
        ret[3] = applet.blue(color);
        return ret;
    }
}
