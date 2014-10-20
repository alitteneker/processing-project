import processing.core.*;

public class TestSketch extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage img;
    Kernel kernel;
    int width = 640, height = 480;

    public void setup() {
        size(width, height);

        PImage in = loadImage("potatoes-1.jpg");

        float[] kernelData = KernelUtil.buildHighPass();
        kernel = new Kernel(kernelData, this);

        long time = System.currentTimeMillis();
        img = kernel.apply(in);
        System.out.println("Elapsed Apply Time: " + (System.currentTimeMillis()-time));
    }

    public void draw() {
        background(0);
        image(img, 0, 0, width, height);
    }
}