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
        if( pixels.length == 0 )
            return;

        float diff = this.RANGE/(float)pixels.length;
        
        float[] mean = new float[3];
        float meanall = 0;
        float[] M2 = new float[3];
        float M2all = 0;

        for( int i = 0; i < pixels.length; ++i ) {
            float intensity = Util.average(pixels[i]);
            float delta = intensity - meanall;
            meanall += delta / (float)( i + 1 );
            M2all += delta * (intensity - meanall);
            for( int j = 0; j < pixels[i].length; ++j ) {
                intensity = pixels[i][j] / resolution;
                data[ (int)Math.floor(intensity) ][j] += diff;

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
    
    public void draw() {
        int width = applet.width, height = applet.height;
        float diffWidth = ((float)width) / ((float)data.length);
        int[] colors = new int[]{
                applet.color(255, 0, 0),
                applet.color(0, 255, 0),
                applet.color(0, 0, 255),
                applet.color(255, 255, 255)
        };
        float[] lastY = new float[data[0].length + 1];
        int i = -1;
        while( ++i < lastY.length - 1 )
            lastY[i] = getParabolicHeight( data[0][i]/this.RANGE, height );
        lastY[i] = getParabolicHeight(Util.average(data[0])/this.RANGE, height);
        float x = 0, lastX = 0;
        for( i = 1; i < data.length; ++i ) {
            int j = 0;
            x = i * diffWidth;
            while( j < data[i].length ) {
                applet.stroke(colors[j]);
                float thisY = getParabolicHeight( data[i][j]/this.RANGE, height );
                applet.line( lastX, lastY[j], x, thisY);
                lastY[j++] = thisY;
            }
            applet.stroke(colors[j]);
            float thisY = getParabolicHeight(Util.average(data[i])/this.RANGE, height);
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
                    return Util.normalize(i, 0, length, minrange, maxrange);
        return 0;
    }
    // get the maximum intensity (normalized to range) with a non-zero value
    public float getMin() {
        int length = data.length - 1;
        for( int i = 0; i <= length; ++i )
            for( int j = 0; j < data[i].length; ++j )
                if( data[i][j] > 0 )
                    return Util.normalize(i, 0, length, minrange, maxrange);
        return length;
    }
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
    public void printStats() {
        System.out.println("Mean, Stdev: " + this.mean + ", " + this.stdev);
    }
}
