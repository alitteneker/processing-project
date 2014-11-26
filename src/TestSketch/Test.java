package TestSketch;

import TestSketch.Filters.*;
import TestSketch.Math.Gradient;
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
    final int maxSize = 600000;

    public void setup() {
        Util.applet = this;
        
        long time = System.currentTimeMillis();
        img[0] = loadImage("bridge-to-nowhere.jpg");
        if( img[0].width * img[0].height > maxSize ) {
            float scale = Util.sqrt((float)( img[0].width * img[0].height ) / maxSize );
            img[0].resize( Math.round(((float)img[0].width) / scale), Math.round(((float)img[0].height) / scale) );
        }
        iwidth = img[0].width;
        iheight = img[0].height;
        size( iwidth, iheight );
        System.out.println("Image loaded!");
        
        FilterPipe queue = new FilterPipe(this);
        queue.push( KernelUtil.buildLaplacian(true, true, this) );
        queue.push( new ContrastFilter(this) );
        Kernel blurA = KernelUtil.buildLinearGaussianBlur(21, 10f, this);
        Kernel blurB = blurA.transpose(false);
        for( int i = 0; i < 2; ++i )
            queue.push(blurA);
        for( int i = 0; i < 2; ++i )
            queue.push(blurB);

        System.out.println("Setup time: "+ ( System.currentTimeMillis() - time ) );
        
        time = System.currentTimeMillis();
        grad = new Gradient(img[0], KernelUtil.buildSobel(this));
        img[2] = grad.toImage(this);
        System.out.println("Gradient time: "+ ( System.currentTimeMillis() - time ) );

        hist[0] = new Histogram(img[0], this);

        img[1] = queue.apply(img[0], false);
        hist[1] = new Histogram(img[1], this);
        
        hist[1].printStats();
    }

    public void draw() {
        background(0);
        iwidth = width; iheight = height;

        int index = mousePressed ? 0 : 1;
        if( index == 1 ) {
            image( img[2], 0,0, iwidth, iheight);
        }
        else {
            image( img[index], 0, 0, iwidth, iheight);
            hist[index].draw(iwidth, iheight);
        }
    }
}