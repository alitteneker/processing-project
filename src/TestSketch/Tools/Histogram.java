package TestSketch.Tools;

import processing.core.*;

public class Histogram {
    protected PApplet applet;
    
    // [intensity][ARGB]
    protected float[][] data;
    protected float minrange = 0, maxrange = 255;
    protected float resolution = 1;

    public Histogram(PImage img, PApplet applet) {
        this.applet = applet;
        initialize();

        img.loadPixels();
        processColorPixels(img.pixels);
    }
    public Histogram(int[] pixels, PApplet applet) {
        this.applet = applet;
        initialize();
        processColorPixels(pixels);
    }
    protected void initialize() {
        this.data = new float[(int)Math.ceil(( maxrange - minrange ) / resolution) + 1][4];
    }
    protected void processColorPixels(int[] pixels) {
        float diff = 100f/(float)pixels.length;
        for( int i = 0; i < pixels.length; ++i ) {
            float[] color = Util.getPixelARGB(pixels[i], this.applet);
            for( int j = 0; j < color.length; ++j )
                data[ getColorIntensity(color[j]) ][j] += diff;
        }
    }
    protected int getColorIntensity( float color ) {
        return (int)Math.floor(color/resolution);
    }
    
    public void draw() {
        int width = applet.width, height = applet.height;
        float diffWidth = ((float)width) / ((float)data.length), diffHeight = ((float)height)/100f;
        int[] colors = new int[]{
                applet.color(255, 255, 255),
                applet.color(255, 0, 0),
                applet.color(0, 255, 0),
                applet.color(0, 0, 255) 
        };
        for( int i = 1; i < data.length; ++i ) {
            for( int j = 0; j < data[i].length; ++j ) {
                applet.stroke(colors[j]);
                applet.line( ( i - 1) *diffWidth, height - ( data[i-1][j] * diffHeight ), i * diffWidth, height - ( data[i][j] * diffHeight ) );
            }
        }
    }
    
    // get the minimum intensity (normalized to range) with a non-zero value
    public float getMax(boolean ignoreAlpha) {
        int length = data.length - 1;
        for( int i = length; i >= 0; --i )
            for( int j = 0; j < data[i].length; ++j )
                if( data[i][j] > 0 )
                    return Util.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
    public float getMax() { return getMax(false); }
    // get the maximum intensity (normalized to range) with a non-zero value
    public float getMin(boolean ignoreAlpha) {
        int length = data.length - 1;
        for( int i = 0; i <= length; ++i )
            for( int j = 0; j < data[i].length; ++j )
                if( data[i][j] > 0 )
                    return Util.normalize(i, 0, length, minrange, maxrange);
        return length;
    }
    public float getMin() { return getMin(false); }
    public float getMaxComponent(int comp) {
        if( comp < 0 || comp >= 4 )
            return -1;
        int length = data.length - 1;
        for( int i = length; i >= 0; --i )
            if( data[i][comp] > 0 )
                return Util.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
    public float getMinComponent(int comp) {
        if( comp < 0 || comp >= 4 )
            return -1;
        int length = data.length - 1;
        for( int i = 0; i <= length; ++i )
            if( data[i][comp] > 0 )
                return Util.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
}
