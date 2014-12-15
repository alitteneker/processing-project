package TestSketch.Filters;

import TestSketch.Math.MathTools;
import TestSketch.Tools.Histogram;
import processing.core.PApplet;

public class ContrastFilter extends Filter {
    // increase for less clipping, decrease for more
    public float deviations = 6;

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

    public float[][] applyToPixels(float[][] pixels, int width, int height, boolean normalize) {
        return applyToPixels(pixels, this.deviations, width, height, 0, 255, normalize);
    }

    // NB: only expands range right now (linear expansion)
    public float[][] applyToPixels(float[][] pixels, float deviations, int width, int height, float t_min, float t_max, boolean normalize) {
        float[][] ret = new float[pixels.length][pixels[0].length];
        applyToPixels(pixels, ret, deviations, t_min, t_max, normalize);
        return ret;
    }
    public void applyInline(float[][] pixels, float deviations, float t_min, float t_max, boolean normalize) {
        applyToPixels(pixels, pixels, deviations, t_min, t_max, normalize);
    }
    public void applyInline(float[] pixels, boolean normalize) {
        applyToPixels(pixels, pixels, this.deviations, 0, 255, normalize);
    }
    public void applyInline(float[] pixels, float deviations, float t_min, float t_max, boolean normalize) {
        applyToPixels(pixels, pixels, deviations, t_min, t_max, normalize);
    }
    public void applyToPixels(float[] pixels, float[] ret, float deviations, float t_min, float t_max, boolean normalize) {
        Histogram hist = new Histogram(pixels, this.applet);
        float mean = hist.getMean(),
                range = deviations * hist.getStdev(),
                max = MathTools.minMax(mean + range, t_min, t_max),
                min = MathTools.minMax(mean - range, t_min, t_max);
        for( int i = 0; i < pixels.length; ++i ) {
            ret[i] = MathTools.normalize(pixels[i], min, max, t_min, t_max);
            if( normalize )
                ret[i] = MathTools.minMax(ret[i], t_min, t_max);
        }
    }
    public void applyToPixels(float[][] pixels, float[][] ret, float deviations, float t_min, float t_max, boolean normalize) {
        Histogram hist = new Histogram(pixels, this.applet);
        float mean = hist.getMean(),
                range = deviations * hist.getStdev(),
                max = MathTools.minMax(mean + range, t_min, t_max),
                min = MathTools.minMax(mean - range, t_min, t_max);
        for( int i = 0; i < pixels.length; ++i ) {
            for( int j = 0; j < pixels[i].length; ++j ) {
                ret[i][j] = MathTools.normalize(pixels[i][j], min, max, t_min, t_max);
                if( normalize )
                    ret[i][j] = MathTools.minMax(ret[i][j], t_min, t_max);
            }
        }
    }
}
