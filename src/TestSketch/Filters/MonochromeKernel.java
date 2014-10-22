package TestSketch.Filters;

import TestSketch.Tools.Util;
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
    protected float[] applyKernelToSubset(float[][] input) {
        float val = monobefore
                ? Util.dotProduct(Util.dotProduct(weights, input), this.data)
                : Util.dotProduct(weights, Util.dotProduct(input, this.data));
        if( Math.abs(val) < 0.0001 )
            val = 0;
        val = normalize(val);
        float[] vals = new float[input[0].length];
        for( int i = 0; i < vals.length; ++i )
            vals[i] = val;
        return vals;
    }
}
