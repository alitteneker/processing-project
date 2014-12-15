package TestSketch.Tools;
import processing.core.*;

public class Util {
    public static PApplet applet;
    
    public static float sqrt(float val) {
        return (float)Math.sqrt(val);
    }
    public static int getPixelIndex(int x, int y, int width) {
        return x + ( y * width );
    }
    
    public static int[] getPixels(int center_x, int center_y, int size, int[] pixels, int width, int height, PApplet applet) {
        int[] ret = new int[size*size];
        int limit = size/2;
        for( int x = -limit; x <= limit; ++x ) {
            for( int y = -limit; y <= limit; ++y ) {
                int locx = TestSketch.Math.MathTools.minMax(center_x + x, 0, width - 1),
                        locy = TestSketch.Math.MathTools.minMax(center_y + y, 0, height - 1);
                ret[ ( limit + x ) + ( ( y + limit ) * size ) ] = getPixel(locx, locy, width, pixels);
            }
        }
        return ret;
    }
    public static float[][] getPixels(int center_x, int center_y, int size, float[][] pixels, int width, int height, PApplet applet) {
        if( pixels.length == 0 || pixels[0].length == 0 )
            return null;
        float[][] ret = new float[size*size][pixels[0].length];
        int limit = size/2;
        for( int x = -limit; x <= limit; ++x ) {
            for( int y = -limit; y <= limit; ++y ) {
                int locx = TestSketch.Math.MathTools.minMax(center_x + x, 0, width - 1), locy = TestSketch.Math.MathTools.minMax(center_y + y, 0, height - 1);
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
    public static int toProcColor(float color, PApplet applet) {
        return applet.color(color);
    }
    public static int[] greyToProcColor(float[] colors, PApplet applet) {
        int[] ret = new int[colors.length];
        for( int i = 0; i < colors.length; ++i )
            ret[i] = toProcColor(colors[i], applet);
        return ret;
    }
}
