package TestSketch.Filters;

import TestSketch.Math.MathTools;
import TestSketch.Tools.Histogram;
import processing.core.PApplet;

public class ContrastFilter extends Filter {
    // increase for less clipping, decrease for more
    public float deviations = 4;

    public ContrastFilter(PApplet applet) {
        super(applet);
    }

    public ContrastFilter(PApplet applet, float deviations) {
        super(applet);
        setDeviations(deviations);
    }

    public void setDeviations(float set) {
        this.deviations = set;
    }

    public float[][] applyToPixels(float[][] pixels, int width, int height) {
        return applyToPixels(pixels, this.deviations, width, height);
    }

    // NB: only expands range right now (linear expansion)
    public float[][] applyToPixels(float[][] pixels, float deviations, int width, int height) {
        Histogram hist = new Histogram(pixels, this.applet);
        float mean = hist.getMean(),
                range = deviations * hist.getStdev(),
                max = MathTools.minMax(mean + range, 0, 255),
                min = MathTools.minMax(mean - range, 0, 255);
        float[][] ret = new float[pixels.length][pixels[0].length];
        for( int i = 0; i < pixels.length; ++i )
            for( int j = 0; j < pixels[i].length; ++j )
                ret[i][j] = MathTools.normalizeMinMax(pixels[i][j], min, max, 0, 255);
        return ret;
    }
}
