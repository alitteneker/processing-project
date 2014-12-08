package TestSketch;

import java.util.ArrayList;

import TestSketch.Filters.*;
import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Tools.Histogram;
import TestSketch.Tools.KernelUtil;
import TestSketch.Tools.Util;
import processing.core.*;

public class Test extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage[] img = new PImage[3];
    int iwidth, iheight;
    float lastx = 0, lasty = 0;
    
    Gradient grad;
    ArrayList<Vector> vec;
    float min_control_dist = 5;
    float alpha = 0.001f, beta = 0.4f, certainty = 0.8f, gamma = 1f;
    boolean started = false;

    public void loadImage() {
        img[0] = loadImage("bridge-to-nowhere.jpg");
        KernelUtil.maxPixelSize(img[0],90000);
        iwidth = img[0].width;
        iheight = img[0].height;
        size( iwidth, iheight );
        System.out.println("Image loaded!");
    }

    public void setup() {
        Util.applet = this;

        vec = new ArrayList<Vector>();
        
        loadImage();
        img[1] = KernelUtil.buildGaussianBlur(5, 0.6f, this).apply(img[0]);

        grad = new Gradient(img[1], KernelUtil.buildSobel(this));
        img[2] = grad.toImage(this); 
    }

    public void draw() {
        background(0);
        iwidth = width; iheight = height;

        image( img[1], 0,0, iwidth, iheight);
        
         if( mousePressed && !started ) {
             Vector pos = new Vector( MathTools.normalize(mouseX, 0, iwidth, 0, img[0].width), MathTools.normalize(mouseY, 0, iheight, 0, img[1].height));
             if( vec.size() == 0 || MathTools.distance( pos, vec.get(vec.size()-1 ) ) >= min_control_dist )
                 vec.add(pos);
         }
        // if( key == SPACE && !started ) {
        //     snake = new Snake(vec.toArray(), grad, alpha, beta, certainty);
        //     snake.runGDA(gamma);
        //     started = true;
        // }

        // if( started )
        //     snake.draw();
        if( vec.size() > 0 ) {
            Vector last_pos = vec.get(0);
            stroke(0,255,0);
            for( int i = 1; i < vec.size(); ++i) {
                Vector pos = vec.get(i);
                line(last_pos.getComponent(0), last_pos.getComponent(1), pos.getComponent(0), pos.getComponent(1));
                last_pos = pos;
            }
            if( started )
                line(last_pos.getComponent(0), last_pos.getComponent(1), vec.get(0).getComponent(0), vec.get(0).getComponent(1));
        }
    }
}