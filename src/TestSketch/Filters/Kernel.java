package TestSketch.Filters;

import TestSketch.Tools.Util;
import processing.core.*;

public class Kernel extends Filter {
    
    protected int size;
    protected float[] data;
    protected float minval = 0, maxval = 255;
    protected boolean abs = false;

    public Kernel(float[][] data, PApplet applet ) {
        super(applet);
        this.setData(data);
    }
    public Kernel(float[] data, PApplet applet) {
        super(applet);
        this.setData(data);
    }
    public boolean setData(float[][] data) {
        int size = data.length;
        float[] newData = new float[size*size];

        for( int i = 0; i < size; ++i ) {
            if( data[i].length != size )
                return false;
            for( int j = 0; j < size; ++j )
                newData[j+(size*i)] = data[i][j];
        }

        this.size = size;
        this.data = newData;
        return true;
    }
    public boolean setData(float[] data) {
        double size = Math.sqrt(data.length);
        if( size % 1.0 != 0.0 )
            return false;
        
        this.size = (int) size;
        this.data = data;
        return true;
    }
    public boolean setRange(float min, float max) {
        if( min > max )
            return false;
        this.minval = min;
        this.maxval = max;
        return true;
    }
    public void setAbs( boolean set ) {
        this.abs = set;
    }
    public float[][] applyToPixels(float[][] pixels, int width, int height) {
        float[][] ret = new float[pixels.length][3];
        for( int x = 0; x < width; ++x ) {
            float[][] colors = Util.getPixels(x, 0, this.size, pixels, width, height, this.applet);
            for( int y = 0; y < height; ++y ) {
                ret[Util.getPixelIndex(x, y, width)] = applyKernelToSubset(colors);
                if( y < height - 1 ) {
                    for( int i = 0; i < colors.length; ++i ) {
                        if( i < colors.length - this.size )
                            colors[i] = colors[i + this.size];
                        else
                            colors[i] = pixels[Util.getPixelIndex(Util.minMax(x + ( i % this.size ) - this.size/2, 0, width-1), y + 1, width)];
                    }
                }
            }
        }
        return ret;
    }
    protected float[] applyKernelToSubset(float[][] input) {
        float[] val = Util.dotProduct(input, this.data);
        for( int i = 0; i < val.length; ++i ) {
            if( Math.abs(val[i]) < 0.0001 )
                val[i] = 0;
            val[i] = normalize(val[i]);
        }
        return val;
    }
    protected float normalize(float input) {
        if( this.abs )
            input = Math.abs(input);
        return Util.cyclicMaxMin(Util.normalize(input, minval, maxval, 0, 255), minval, maxval);
    }
    protected float[] normalize(float[] input) {
        float[] ret = new float[input.length];
        for( int i = 0; i < input.length; ++i ) 
            ret[i] = normalize(input[i]);
        return ret;
    }
}
