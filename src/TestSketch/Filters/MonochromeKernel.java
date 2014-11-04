package TestSketch.Filters;

import TestSketch.Math.MathTools;
import processing.core.PApplet;

public class MonochromeKernel extends Kernel {
    protected boolean monobefore = true;
    //RGB format
    protected float[] weights = { 0.25f, 0.25f, 0.25f }; 

    public MonochromeKernel(float[] data, PApplet applet) {
        super(data, applet);
    }
    public MonochromeKernel(float[] data, boolean monobefore, PApplet applet) {
        super(data, applet);
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
    protected void applyToPixel(float[] out, float[][] input, int x, int y, int loca, int width, int height) {
        int limit = size / 2, mx, my, locb, i;
        float kv, val = 0;
        for( mx = -limit; mx <= limit; ++mx ) {
            for( my = -limit; my <= limit; ++my ) {
                locb = MathTools.minMax(x + mx, 0, width - 1) + MathTools.minMax(y + my, 0, height - 1) * width;
                kv = data[ limit + mx + size * ( limit + my ) ];
                val += monobefore
                        ? ( MathTools.dotProduct(weights, input[locb]) * kv )
                        : MathTools.dotProduct(weights, MathTools.product(input[locb], kv));
            }
        }
        val = normalize(val);
        for( i = 0; i < out.length; ++i )
            out[i] = val;
    }
}
