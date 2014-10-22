package TestSketch.Tools;

import processing.core.*;

public class Histogram {
    protected PApplet applet;
    
    // [intensity][ARGB]
    protected float[][] data;
    protected float mean;
    protected float meanComponent[];
    protected float stdev;
    protected float[] stdevComponent;
    protected float minrange = 0, maxrange = 255;
    protected final float resolution = 1;
    protected final float RANGE = 100;

    public Histogram(PImage img, PApplet applet) {
        this.applet = applet;
        initialize();

        img.loadPixels();
        processColorPixels(Util.getRGB(img.pixels, applet));
    }
    public Histogram(int[] pixels, PApplet applet) {
        this.applet = applet;
        initialize();
        processColorPixels(Util.getRGB(pixels, applet));
    }
    public Histogram(float[][] pixels, PApplet applet) {
        this.applet = applet;
        initialize();
        processColorPixels(pixels);
    }
    protected void initialize() {
        this.mean = 0;
        this.stdev = 0;
        this.data = new float[(int)Math.ceil(( maxrange - minrange ) / resolution) + 1][3];
        this.meanComponent = new float[3];
        this.stdevComponent = new float[3];
    }
    protected void processColorPixels(float[][] pixels) {
        float diff = this.RANGE/(float)pixels.length;
        float[] stdAComponent = new float[3];
        for( int i = 0; i < pixels.length; ++i ) {
            for( int j = 0; j < pixels[i].length; ++j ) {
                float intensity = pixels[i][j] / resolution;
                data[ (int)Math.floor(intensity) ][j] += diff;
                meanComponent[j] += intensity;
                stdAComponent[j] += intensity*intensity;
            }
        }
        float stdA = 0;
        for( int j = 0; j < meanComponent.length; ++j ) {
            float meanVal = meanComponent[j] / (float)pixels.length;
            stdA += stdAComponent[j] / (float)meanComponent.length;
            mean += meanComponent[j] / (float)meanComponent.length;
            stdevComponent[j] =
                    ( stdAComponent[j] - ( 2 * meanVal * meanComponent[j] ) + ( meanVal * meanVal ) )
                            / (float)pixels.length;
            meanComponent[j] = meanVal;
        }
        float meanVal = mean / (float)pixels.length;
        this.stdev = ( stdA - ( 2 * meanVal * mean ) + ( meanVal * meanVal ) ) / (float)pixels.length;
        this.mean = meanVal;
        System.out.println("Mean, Stdev: " + this.mean + ", " + this.stdev);
    }
    
    public void draw() {
        int width = applet.width, height = applet.height;
        float diffWidth = ((float)width) / ((float)data.length);
        int[] colors = new int[]{
                applet.color(255, 0, 0),
                applet.color(0, 255, 0),
                applet.color(0, 0, 255) 
        };
        float[] lastY = new float[data[0].length];
        for( int i = 0; i < lastY.length; ++i )
            lastY[i] = height - getParabolicHeight( data[0][i]/this.RANGE, height );
        for( int i = 1; i < data.length; ++i ) {
            for( int j = 0; j < data[i].length; ++j ) {
                applet.stroke(colors[j]);
                float thisY = height - getParabolicHeight( data[i][j]/this.RANGE, height );
                applet.line( ( i - 1) * diffWidth, lastY[j], i * diffWidth, thisY);
                lastY[j] = thisY;
            }
        }
    }
    protected float getParabolicHeight(float val, int height) {
        return ( ( val - 2 ) * -val ) * (float)height;
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
