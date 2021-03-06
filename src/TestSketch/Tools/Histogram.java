package TestSketch.Tools;

import TestSketch.Math.MathTools;
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
    protected static final float RANGE = 100;

    public Histogram(PImage img, PApplet applet) {
        this.applet = applet;
        img.loadPixels();
        processPixels(Util.getRGB(img.pixels, applet));
    }
    public Histogram(int[] pixels, PApplet applet) {
        this.applet = applet;
        processPixels(Util.getRGB(pixels, applet));
    }
    public Histogram(float[][] pixels, PApplet applet) {
        this.applet = applet;
        processPixels(pixels);
    }
    public Histogram(float[] pixels, PApplet applet) {
        this.applet = applet;
        processPixels(pixels);
    }
    protected void initialize(int size) {
        this.mean = 0;
        this.stdev = 0;
        this.data = new float[(int)Math.ceil(( maxrange - minrange ) / resolution) + 1][size];
        this.meanComponent = new float[size];
        this.stdevComponent = new float[size];
    }
    protected void processPixels(float[][] pixels) {
        if( pixels.length == 0 )
            return;
        int size = pixels[0].length;
        initialize(size);

        float diff = Histogram.RANGE/(float)pixels.length;
        
        float[] mean = new float[size];
        float meanall = 0;
        float[] M2 = new float[size];
        float M2all = 0;

        for( int i = 0; i < pixels.length; ++i ) {
            float intensity = MathTools.average(pixels[i]);
            float delta = intensity - meanall;
            meanall += delta / (float)( i + 1 );
            M2all += delta * (intensity - meanall);
            for( int j = 0; j < pixels[i].length; ++j ) {
                intensity = pixels[i][j] / resolution;
                data[ (int)MathTools.minMax(intensity,0,data.length - 1) ][j] += diff;

                delta = pixels[i][j] - mean[j];
                mean[j] += delta / (float)( i + 1 );
                M2[j] += delta * (pixels[i][j] - mean[j]);
            }
        }
        for( int j = 0; j < meanComponent.length; ++j ) {
            this.stdevComponent[j] = (float) Math.sqrt( M2[j] / (float)(pixels.length - 1) );
            this.meanComponent[j] = mean[j];
        }
        this.stdev = (float) Math.sqrt( M2all / (float)(pixels.length - 1) );
        this.mean = meanall;
    }
    protected void processPixels(float[] pixels) {
        if( pixels.length == 0 )
            return;
        initialize(1);
        
        float diff = Histogram.RANGE/(float)pixels.length;
        
        float meanall = 0;
        float M2all = 0;

        for( int i = 0; i < pixels.length; ++i ) {
            float intensity = pixels[i];
            float delta = intensity - meanall;
            meanall += delta / (float)( i + 1 );
            M2all += delta * (intensity - meanall);
            
            data[ (int)MathTools.minMax(intensity,0,data.length - 1) ][0] += diff;
        }
        this.stdev = (float) Math.sqrt( M2all / (float)(pixels.length - 1) );
        this.stdevComponent[0] = this.stdev;
        this.mean = meanall;
        this.meanComponent[0] = meanall;
    }
    public float aggregate(float[] data) {
        return MathTools.length(data);
    }
    
    public void draw() {
        draw(applet.width, applet.height);
    }
    public void draw(int width, int height) {
        float diffWidth = ((float)width) / ((float)data.length);
        int[] colors = new int[]{
                applet.color(255, 0, 0),
                applet.color(0, 255, 0),
                applet.color(0, 0, 255),
                applet.color(255, 255, 255)
        };
        float[] lastY = new float[data[0].length + 1];
        int i = -1, j = 0;
        while( ++i < lastY.length - 1 )
            lastY[i] = getParabolicHeight( data[0][i]/Histogram.RANGE, height );
        lastY[i] = getParabolicHeight(MathTools.average(data[0])/Histogram.RANGE, height);
        float x = 0, lastX = 0;
        for( i = 1; i < data.length; ++i ) {
            j = 0;
            x = i * diffWidth;
            while( j < data[i].length ) {
                applet.stroke(colors[j]);
                float thisY = getParabolicHeight( data[i][j]/Histogram.RANGE, height );
                applet.line( lastX, lastY[j], x, thisY);
                lastY[j++] = thisY;
            }
            applet.stroke(colors[j]);
            float thisY = getParabolicHeight(MathTools.average(data[i])/Histogram.RANGE, height);
            applet.line( lastX, lastY[j], x, thisY );
            lastY[j] = thisY;
            lastX = x;
        }
    }
    protected float getParabolicHeight(float val, int height) {
        return (float)height - ( ( ( 2 - val ) * val ) * (float)height );
    }

    public float getMean() {
        return this.mean;
    }
    public float getMean(int i) {
        if( i >= 0 && i < this.meanComponent.length )
            return this.meanComponent[i];
        return 0;
    }
    public float getStdev() {
        return this.stdev;
    }
    public float getStdev(int i) {
        if( i >= 0 && i < this.stdevComponent.length )
            return this.stdevComponent[i];
        return 0;
    }
    
    // get the minimum intensity (normalized to range) with a non-zero value
    public float getMax() {
        int length = data.length - 1;
        for( int i = length; i >= 0; --i )
            for( int j = 0; j < data[i].length; ++j )
                if( data[i][j] > 0 )
                    return MathTools.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
    // get the maximum intensity (normalized to range) with a non-zero value
    public float getMin() {
        int length = data.length - 1;
        for( int i = 0; i <= length; ++i )
            for( int j = 0; j < data[i].length; ++j )
                if( data[i][j] > 0 )
                    return MathTools.normalize(i, 0, length, minrange, maxrange);
        return length;
    }
    public float getMaxComponent(int comp) {
        if( comp < 0 || comp >= 4 )
            return -1;
        int length = data.length - 1;
        for( int i = length; i >= 0; --i )
            if( data[i][comp] > 0 )
                return MathTools.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
    public float getMinComponent(int comp) {
        if( comp < 0 || comp >= 4 )
            return -1;
        int length = data.length - 1;
        for( int i = 0; i <= length; ++i )
            if( data[i][comp] > 0 )
                return MathTools.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
    public void printStats() {
        System.out.println("Filter Max, Min: " + getMax() + ", " + getMin() + "\nMean, Stdev: " + this.mean + ", " + this.stdev);
    }
}
