package TestSketch.Filters;

import TestSketch.Filter;
import TestSketch.Tools.Histogram;
import TestSketch.Tools.Util;
import processing.core.PApplet;

public class MaxContrastFilter extends Filter {

    public boolean ignoreAlpha = true;
    public MaxContrastFilter(PApplet applet) {
        super(applet);
    }
    protected int[] applyToPixels(int[] pixels, int width, int height) {
        Histogram hist = new Histogram(pixels, this.applet);
        float max = hist.getMax(ignoreAlpha)+1, min = hist.getMin(ignoreAlpha)-1;
        int[] ret = new int[pixels.length];
        for( int i = 0; i < pixels.length; ++i ) {
            float[] colors = Util.getPixelARGB(pixels[i], this.applet);
            for( int j = ignoreAlpha ? 1 : 0; j < colors.length; ++j )
                colors[j] = Util.normalize(colors[j], min, max, 0, 255);
            ret[i] = this.applet.color(colors[1], colors[2], colors[3], colors[0]);
        }
        return ret;
    }
}
