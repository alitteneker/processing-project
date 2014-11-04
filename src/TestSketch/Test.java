package TestSketch;

import TestSketch.Filters.*;
import TestSketch.Tools.Histogram;
import TestSketch.Tools.KernelUtil;
import processing.core.*;

public class Test extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage[] img = new PImage[2];
    Histogram[] hist = new Histogram[2];
    int iwidth, iheight;

    public void setup() {
        
        img[0] = loadImage("pier-in-lake.jpg");
        iwidth = img[0].width;
        iheight = img[0].height;
        size( iwidth, iheight );
        System.out.println("Image loaded!");
        
        FilterPipe queue = new FilterPipe(this);
        queue.push( KernelUtil.buildLaplacian(true, true, this) );
        queue.push( new ContrastFilter(this) );
        Kernel blur = KernelUtil.buildGaussianBlur(21, 15f, true, this);
        for( int i = 0; i < 10; ++i )
            queue.push(blur);

        hist[0] = new Histogram(img[0], this);
        img[1] = queue.apply(img[0], false);
        hist[1] = new Histogram(img[1], this);
        
        hist[1].printStats();
    }

    public void draw() {
        background(0);
        iwidth = width; iheight = height;
        int index = mousePressed ? 0 : 1;
        image( img[index], 0, 0, iwidth, iheight);
        hist[index].draw(iwidth, iheight);
    }
}