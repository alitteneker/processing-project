import processing.core.*;

public class TestSketch extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage img, in;
    int width = 640, height = 480;

    public void setup() {
        size(width, height);

        in = loadImage("bridge-to-nowhere.jpg");

        Kernel kernel = new MonochromeKernel(new float[]{0,-0.25f,0,-0.25f,1,-0.25f,0,-0.25f,0}, this);
        kernel.setRange(-255, 255);

        Kernel invert = new Kernel(new float[]{-1f}, this);
        
        KernelStack stack = new KernelStack(this);
//        stack.push(invert);
        stack.push(kernel);

        long time = System.currentTimeMillis();
        img = stack.apply(in, false);
        System.out.println("Elapsed Apply Time: " + (System.currentTimeMillis()-time));
    }

    public void draw() {
        background(0);
        image(mousePressed ? in : img, 0, 0, width, height);
    }
}