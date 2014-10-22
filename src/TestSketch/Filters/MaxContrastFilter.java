package TestSketch.Filters;

import TestSketch.Tools.Histogram;
import TestSketch.Tools.Util;
import processing.core.PApplet;

public class MaxContrastFilter extends Filter {

    public boolean ignoreAlpha = true;
    public MaxContrastFilter(PApplet applet) {
        super(applet);
    }
    public float[][] applyToPixels(float[][] pixels, int width, int height) {
        Histogram hist = new Histogram(pixels, this.applet);
        float max = hist.getMax(ignoreAlpha)+1, min = hist.getMin(ignoreAlpha)-1;
        float[][] ret = new float[pixels.length][3];
        for( int i = 0; i < pixels.length; ++i ) {
            for( int j = 0; j < pixels[i].length; ++j )
                pixels[i][j] = Util.normalize(pixels[i][j], min, max, 0, 255);
        }
        return ret;
    }
}
