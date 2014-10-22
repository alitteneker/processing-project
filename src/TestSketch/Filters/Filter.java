package TestSketch.Filters;
import TestSketch.Tools.Util;
import processing.core.*;

public abstract class Filter {
    public PApplet applet;
    public Filter(PApplet applet) {
        this.applet = applet;
    }
    public PImage apply(PImage in, boolean same) {
        int width = in.width, height = in.height;
        in.loadPixels();

        PImage out = same ? in : this.applet.createImage(width, height, in.format);
        float[][] pixels = applyToPixels(Util.getRGB(in.pixels, this.applet), in.width, in.height);
        out.pixels = Util.toProcColor(pixels, applet);
        out.updatePixels();

        return out;
    }
    public PImage apply(PImage in) {
        return apply(in, false);
    }
    public abstract float[][] applyToPixels(float[][] pixels, int width, int height);
}
