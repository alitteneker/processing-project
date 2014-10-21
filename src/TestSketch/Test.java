package TestSketch;

import TestSketch.Filters.*;
import TestSketch.Tools.Histogram;
import TestSketch.Tools.KernelUtil;
import processing.core.*;

public class Test extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage[] img = new PImage[2];
    Histogram[] hist = new Histogram[2];
    int width = 640, height = 480;

    public void setup() {
        
        img[0] = loadImage("Russet-Potato.jpg");
        width = img[0].width; height = img[0].height;
        size( width, height );

        Kernel kernel = new MonochromeKernel(new float[]{0,-0.25f,0,-0.25f,1,-0.25f,0,-0.25f,0}, this);
        kernel.setAbs(true);
//        kernel.setRange(-255, 255);

//        Kernel invert = new Kernel(new float[]{-1f}, this);
        
        FilterStack stack = new FilterStack(this);
        stack.push( new MonochromeKernel(KernelUtil.buildGaussianBlur(3, 0.2f), this) );
//        stack.push( new MaxContrastFilter(this) );
        stack.push(kernel);

        hist[0] = new Histogram(img[0], this);
        img[1] = stack.apply(img[0], false);
        hist[1] = new Histogram(img[1], this);
        
        System.out.println("Filter Max/Min: " + hist[1].getMax() + "/" + hist[1].getMin() );
    }

    public void draw() {
        background(0);
        int index = mousePressed ? 0 : 1;
        image( img[index], 0, 0, width, height);
        hist[index].draw();
    }
}