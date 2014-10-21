package TestSketch.Filters;

import TestSketch.Tools.Util;
import processing.core.PApplet;

public class MonochromeKernel extends Kernel {
    protected boolean monobefore = true;
    //ARGB format
    protected float[] weights = { 0.25f, 0.25f, 0.25f, 0.25f }; 

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
    public int[] applyKernelToPixels(int[] pixels, int width, int height) {
        int[] ret = new int[pixels.length];
        float max = -300, min = 300;
        for( int x = 0; x < width; ++x ) {
            for( int y = 0; y < height; ++y ) {
                // TODO: would be much better if we could shift by row, then only have to get the next row each time
                float[][] colors = Util.getPixelsARGB(x, y, this.size, pixels, width, height, this.applet);
                float colorProc;
                if( monobefore )
                    colorProc = applyKernelToSubset(Util.dotProduct(weights, colors));
                else
                    colorProc = Util.dotProduct(weights, applyKernelToSubset(colors));
                max = Math.max(max, colorProc);
                min = Math.min(min, colorProc);
                ret[Util.getPixelIndex(x, y, width)] = applet.color(colorProc);
            }
        }
        System.out.println("Max, Min: " + max + ", " + min );
        return ret;
    }
}
