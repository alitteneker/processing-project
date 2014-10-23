package TestSketch.Filters;

import TestSketch.Tools.Histogram;
import TestSketch.Tools.Util;
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
    public float[][] applyToPixels(float[][] pixels, float deviations, int width, int height) {
        Histogram hist = new Histogram(pixels, this.applet);
        float mean = hist.getMean(),
                range = deviations * hist.getStdev(),
                max = Util.minMax(mean + range, 0, 255),
                min = Util.minMax(mean - range, 0, 255);
        float[][] ret = new float[pixels.length][3];
        for( int i = 0; i < pixels.length; ++i )
            for( int j = 0; j < pixels[i].length; ++j )
                ret[i][j] = Util.normalizeMinMax(pixels[i][j], min, max, 0, 255);
        return ret;
    }
}
