package TestSketch;
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
        int[] pixels = same ? in.pixels.clone() : in.pixels;
        out.pixels = applyToPixels(pixels, in.width, in.height);
        out.updatePixels();

        return out;
    }
    public PImage apply(PImage in) {
        return apply(in, false);
    }
    protected abstract int[] applyToPixels(int[] pixels, int width, int height);
}
