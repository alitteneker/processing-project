package TestSketch.Filters;

import TestSketch.Math.MathTools;
import processing.core.*;

public class Kernel extends MultithreadedFilter {
    
    protected int size;
    protected float[] data;
    protected float minval = 0, maxval = 255;
    protected boolean abs = false;
    protected boolean single = false;

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
        if(!single)
            return super.applyToPixels(pixels, width, height);
        int x, y, loca;
        float[][] ret = new float[pixels.length][3];
        for( x = 0; x < width; ++x ) {
            for( y = 0; y < height; ++y ) {
                loca = x + y * width;
                applyToPixel(ret[loca], pixels, x, y, loca, width, height);
            }
        }
        return ret;
    }
    protected void applyToPixel(float[] out, float[][] input, int x, int y, int loca, int width, int height) {
        int limit = size / 2, mx, my, locb, i;
        float kv;
        for( mx = -limit; mx <= limit; ++mx ) {
            for( my = -limit; my <= limit; ++my ) {
                locb = MathTools.minMax(x + mx, 0, width - 1) + MathTools.minMax(y + my, 0, height - 1) * width;
                kv = data[ limit + mx + size * ( limit + my ) ];
                for( i = 0; i < out.length; ++i )
                    out[i] += input[locb][i] * kv;
            }
        }
        for( i = 0; i < out.length; ++i )
            out[i] = normalize( out[i] );
    }
    protected float normalize(float input) {
        if( Math.abs(input) < 0.0001 )
            input = 0;
        if( this.abs )
            input = Math.abs(input);
        return MathTools.cyclicMaxMin(MathTools.normalize(input, minval, maxval, 0, 255), minval, maxval);
    }
    protected float[] normalize(float[] input) {
        for( int i = 0; i < input.length; ++i ) 
            input[i] = normalize(input[i]);
        return input;
    }
}
