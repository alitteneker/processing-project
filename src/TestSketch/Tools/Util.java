package TestSketch.Tools;
import processing.core.*;

public class Util {
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
    public static int getPixelIndex(int x, int y, int width) {
        return x + ( y * width );
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
    
    // N.B.: THESE METHODs WILL NOT LOAD PIXEL DATA BEFORE BEGINNING
    public static int[] getPixels(int center_x, int center_y, int size, int[] pixels, int width, int height, PApplet applet) {
        int[] ret = new int[size*size];
        int limit = size/2;
        for( int x = -limit; x <= limit; ++x ) {
            for( int y = -limit; y <= limit; ++y ) {
                int locx = minMax(center_x + x, 0, width - 1), locy = minMax(center_y + y, 0, height - 1);
                ret[ ( limit + x ) + ( ( y + limit ) * size ) ] = getPixel(locx, locy, width, pixels);
            }
        }
        return ret;
    }
    public static float[][]getPixels(int center_x, int center_y, int size, float[][] pixels, int width, int height, PApplet applet) {
        if( pixels.length == 0 || pixels[0].length == 0 )
            return null;
        float[][] ret = new float[size*size][pixels[0].length];
        int limit = size/2;
        for( int x = -limit; x <= limit; ++x ) {
            for( int y = -limit; y <= limit; ++y ) {
                int locx = minMax(center_x + x, 0, width - 1), locy = minMax(center_y + y, 0, height - 1);
                ret[ ( limit + x ) + ( ( y + limit ) * size ) ] = pixels[ getPixelIndex(locx, locy, width) ];
            }
        }
        return ret;
    }
    public static float[][] getPixelsRGB(int center_x, int center_y, int size, int[] pixels, int width, int height, PApplet applet) {
        int[] result = getPixels(center_x, center_y, size, pixels, width, height, applet);
        return getRGB(result, applet);
    }
    public static int getPixel(int x, int y, PImage img) {
        return getPixel(x,y,img.width,img.pixels);
    }
    public static int getPixel(int x, int y, int width, int[] pixels) {
        return pixels[getPixelIndex(x,y,width)];
    }
    public static float[] getARGB(int pixel, PApplet applet) {
        float[] ret = new float[4];
        ret[0] = applet.alpha(pixel);
        ret[1] = applet.red(pixel);
        ret[2] = applet.green(pixel);
        ret[3] = applet.blue(pixel);
        return ret;
    }
    public static float[] getRGB(int pixel, PApplet applet) {
        float[] ret = new float[3];
        ret[0] = applet.red(pixel);
        ret[1] = applet.green(pixel);
        ret[2] = applet.blue(pixel);
        return ret;
    }
    public static float[][] getRGB(int[] pixels, PApplet applet) {
        float[][] ret = new float[pixels.length][3];
        for( int i = 0; i < pixels.length; ++i )
            ret[i] = getRGB(pixels[i], applet);
        return ret;
    }
    public static int[] toProcColor(float[][] colors, PApplet applet) {
        int[] ret = new int[colors.length];
        for( int i = 0; i < colors.length; ++i )
            ret[i] = toProcColor(colors[i], applet);
        return ret;
    }
    public static int toProcColor(float[] colors, PApplet applet) {
        if( colors.length == 0 )
            return -1;
        if( colors.length > 3 )
            return applet.color(colors[1], colors[2], colors[3], colors[0]);
        if( colors.length == 3 )
            return applet.color(colors[0], colors[1], colors[2]);
        if( colors.length == 2 )
            return applet.color(colors[0], colors[2]);
        return applet.color(colors[0]);
                    
    }
}
