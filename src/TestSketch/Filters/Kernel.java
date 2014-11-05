package TestSketch.Filters;

import TestSketch.Math.MathTools;
import processing.core.*;

public class Kernel extends MultithreadedFilter {
    public final static int MODE_ABS = 1, MODE_CYCLE = 2, MODE_MINMAX = 3;
    
    protected int width, height;
    protected float[] data;
    protected float minval = 0, maxval = 255;
    protected int mode = MODE_MINMAX;
    protected boolean single = false;

    public Kernel(float[][] data, PApplet applet ) {
        super(applet);
        this.setData(data);
    }
    public Kernel(float[] data, int width, int height, PApplet applet) {
        super(applet);
        this.setData(data, width, height);
    }
    public boolean setData(float[][] data) {
        if( data.length == 0 || data[0].length == 0 )
            return false;
        int height = data.length, width = data[0].length;
        float[] newData = new float[width * height];
        for( int i = 0; i < height; ++i ) {
            if( data[i].length != width )
                return false;
            for( int j = 0; j < width; ++j )
                newData[j+(width*i)] = data[i][j];
        }
        this.width = width;
        this.height = height;
        this.data = newData;
        return true;
    }
    public boolean setData(float[] data, int width, int height) {
        if( data.length != width * height )
            return false;
        this.width = width;
        this.height = height;
        this.data = data;
        return true;
    }
    public Kernel transpose() {
        return transpose(true);
    }
    public Kernel transpose(boolean same) {
        int width = this.width, height = this.height;
        float[] data = new float[this.data.length];
        for( int i = 0; i < data.length; ++i )
            data[ (i % width) + (i / width) * height ] = this.data[i];
        if( same ) {
            setData(data, height, width);
            return this;
        }
        return new Kernel(data, height, width, this.applet);
    }

    // assume the kernel is square
    public boolean setData(float[] data) {
        double size = Math.sqrt(data.length);
        if( width % 1.0 != 0.0 )
            return false;
        return setData(data, (int)size, (int)size);
    }
    public float[] getData() {
        return this.data;
    }
    public int getSize() {
        return this.width * this.height;
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
    public boolean setRange(float min, float max) {
        if( min > max )
            return false;
        this.minval = min;
        this.maxval = max;
        return true;
    }
    public float getMin() {
        return this.minval;
    }
    public float getMax() {
        return this.maxval;
    }
    public void setMode( int set ) {
        this.mode = set;
    }
    public int getMode() {
        return this.mode;
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
        int limitW = this.width / 2,
            limitH = this.height / 2,
                mx, my, locb, i;
        float kv;
        for( mx = -limitW; mx <= limitW; ++mx ) {
            for( my = -limitH; my <= limitH; ++my ) {
                locb = MathTools.minMax(x + mx, 0, width - 1) + MathTools.minMax(y + my, 0, height - 1) * width;
                kv = data[ limitW + mx + this.width * ( limitH + my ) ];
                for( i = 0; i < out.length; ++i )
                    out[i] += input[locb][i] * kv;
            }
        }
        for( i = 0; i < out.length; ++i )
            out[i] = normalizeValue( out[i] );
    }
    protected float normalizeValue(float input) {
        if( Math.abs(minval - input) < 0.0001 )
            input = minval;
        if( Math.abs(maxval - input) < 0.0001 )
            input = maxval;

        if( this.mode == MODE_ABS )
            input = Math.abs(input);
        else if( this.mode == MODE_CYCLE )
            input = MathTools.cyclicMinMax(MathTools.normalize(input, minval, maxval, 0, 255), 0, 255);
        else
            input = MathTools.normalizeMinMax(input, minval, maxval, 0, 255);

        return input;
    }
    protected float[] normalizeValue(float[] input) {
        for( int i = 0; i < input.length; ++i ) 
            input[i] = normalizeValue(input[i]);
        return input;
    }
}
