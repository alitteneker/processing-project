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
        
        FilterPipe queue = new FilterPipe(this);
        queue.push( KernelUtil.buildLaplacian(false, true, this) );
        queue.push( new ContrastFilter(this) );
        queue.push( KernelUtil.buildGaussianBlur(5, 0.6f, true, this) );

        hist[0] = new Histogram(img[0], this);
        img[1] = queue.apply(img[0], false);
        hist[1] = new Histogram(img[1], this);
        
        hist[1].printStats();
    }

    public void draw() {
        background(0);
        int index = mousePressed ? 0 : 1;
        image( img[index], 0, 0, width, height);
        hist[index].draw();
    }
}