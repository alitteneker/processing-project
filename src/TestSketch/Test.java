package TestSketch;

import TestSketch.Filters.*;
import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Math.Matrix.Matrix;
import TestSketch.Tools.Histogram;
import TestSketch.Tools.KernelUtil;
import TestSketch.Tools.Util;
import processing.core.*;

public class Test extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage[] img = new PImage[3];
    Histogram[] hist = new Histogram[2];
    Gradient grad;
    int iwidth, iheight;
    float lastx = 0, lasty = 0;

    public void loadImage() {
      img[0] = loadImage("bridge-to-nowhere.jpg");
      iwidth = img[0].width;
      iheight = img[0].height;
      size( iwidth, iheight );
      System.out.println("Image loaded!");
    }

    public void setup() {
        Util.applet = this;

        loadImage();

        FilterPipe queue = new FilterPipe(this);
        queue.push( KernelUtil.buildLaplacian(true, true, this) );
        queue.push( new ContrastFilter(this) );

        grad = new Gradient(img[0], KernelUtil.buildSobel(this));
        img[2] = grad.toImage(this);

        hist[0] = new Histogram(img[0], this);

        img[1] = queue.apply(img[0], false);
        hist[1] = new Histogram(img[1], this);
        hist[1].printStats(); 
    }

    public void draw() {
        background(0);
        iwidth = width; iheight = height;

        image( img[2], 0,0, iwidth, iheight);

        if( mousePressed ) {
            float x = grad.getWidth() *  ( ((float)mouseX) / ((float)iwidth) );
            float y = grad.getHeight() * ( ((float)mouseY) / ((float)iheight) );
            if( x != lastx && y != lasty ) {
                Vector val = grad.getAt(x, y);
                System.out.print(x + ", " + y + "\t=>\t");
                val.printVector();
                lastx = x;
                lasty = y;
            }
        }
        
//      image( img[index], 0, 0, iwidth, iheight);
//      hist[index].draw(iwidth, iheight);
    }
}