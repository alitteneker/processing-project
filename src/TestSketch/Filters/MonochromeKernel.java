package TestSketch.Filters;

import TestSketch.Math.MathTools;
import TestSketch.Math.MonoOperator;
import processing.core.PApplet;

public class MonochromeKernel extends Kernel implements MonoOperator {
    protected boolean monobefore = true;
    //RGB format
    protected float[] weights = { 0.25f, 0.25f, 0.25f }; 

    public MonochromeKernel(float[] data, int width, int height, PApplet applet) {
        super(data, width, height, applet);
    }
    public MonochromeKernel(float[] data, int width, int height, boolean monobefore, PApplet applet) {
        super(data, width, height, applet);
        setMonoBefore(monobefore);
    }
    public MonochromeKernel(float[][] data, PApplet applet) {
        super(data, applet);
    }
    public MonochromeKernel(float[][] data, boolean monobefore, PApplet applet) {
        super(data, applet);
        setMonoBefore(monobefore);
    }
    public void setMonoBefore( boolean set ) {
        this.monobefore = set;
    }
    public boolean setWeights(float[] set) {
        if( set.length != 4 )
            return false;
        this.weights = set;
        return true;
    }
    public Kernel transpose(boolean same) {
        float[] data = getTransposedData();
        if( same ) {
            setData(data, height, width);
            return this;
        }
        return new MonochromeKernel(data, height, width, this.applet);
    }
    protected void applyToPixel(float[] out, float[][] input, int x, int y, int loca, int width, int height, boolean normalize) {
        float val = getPixelValue(input, x, y, loca, width, height);
        if( normalize )
            val = normalizeValue(val);
        for( int i = 0; i < out.length; ++i )
            out[i] = val;
    }
    public float getPixelValue(float[][] input, int x, int y, int loca, int width, int height) {
        int mx, my, locb;
        float kv, val = 0;
        int limitW = this.width / 2,
            limitH = this.height / 2;
        for( mx = -limitW; mx <= limitW; ++mx ) {
            for( my = -limitH; my <= limitH; ++my ) {
                locb = MathTools.minMax(x + mx, 0, width - 1) + MathTools.minMax(y + my, 0, height - 1) * width;
                kv = data[ limitW + mx + this.width * ( limitH + my ) ];
                val += monobefore
                        ? ( MathTools.dotProduct(weights, input[locb]) * kv )
                        : MathTools.dotProduct(weights, MathTools.product(input[locb], kv));
            }
        }
        return val;
    }
    public float getPixelValue(float[] input, int x, int y, int loca, int width, int height) {
        int mx, my, locb;
        float kv, val = 0;
        int limitW = this.width / 2,
            limitH = this.height / 2;
        for( mx = -limitW; mx <= limitW; ++mx ) {
            for( my = -limitH; my <= limitH; ++my ) {
                locb = MathTools.minMax(x + mx, 0, width - 1) + MathTools.minMax(y + my, 0, height - 1) * width;
                kv = data[ limitW + mx + this.width * ( limitH + my ) ];
                val += input[locb] * kv;
            }
        }
        return val;
    }
}
