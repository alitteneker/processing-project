package TestSketch;

import java.util.ArrayList;

import Snakes.Snake;
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
    Snake snake;
    ArrayList<Vector> vec;
    float min_control_dist = 5;
    float alpha = 0.5f, beta = 0.5f, certainty = 0.2f, gamma = 0.1f;
    boolean started = false;

    public void loadImage() {
        img[0] = loadImage("Gala Apple.jpg");
        KernelUtil.maxPixelSize(img[0],900000);
        iwidth = img[0].width;
        iheight = img[0].height;
        size( iwidth, iheight );
        System.out.println("Image loaded!");
    }

    public void setup() {
        Util.applet = this;

        vec = new ArrayList<Vector>();
        
        loadImage();
        img[1] = KernelUtil.buildGaussianBlur(5, 1.0f, this).apply(img[0]);

        grad = new Gradient( img[1], KernelUtil.buildSobel(this) );
        img[2] = grad.toImage(this); 
    }

    public void draw() {
        background(0);
        iwidth = width; iheight = height;

        image( img[1], 0, 0, iwidth, iheight);
        
        if( mousePressed && !started ) {
            Vector pos = new Vector( MathTools.normalize(mouseX, 0, iwidth, 0, img[2].width), MathTools.normalize(mouseY, 0, iheight, 0, img[2].height) );
            if( vec.size() == 0 || MathTools.distance( pos, vec.get(vec.size()-1) ) >= min_control_dist )
                vec.add(pos);
        }

        if( started )
             snake.draw( iwidth, iheight, this);
        if( vec.size() > 0 ) {
            Vector last_pos = vec.get(0);
            if( started )
                stroke(0,255,0);
            else
                stroke(0,0,255);
            float scale_w = ((float)width)/((float)grad.getWidth()),
                  scale_h = ((float)height)/((float)grad.getHeight());
            for( int i = 1; i < vec.size(); ++i) {
                Vector pos = vec.get(i);
                line(scale_w * last_pos.getComponent(0), scale_h * last_pos.getComponent(1),
                     scale_w * pos.getComponent(0),      scale_h * pos.getComponent(1));
                last_pos = pos;
            }
            if( started )
                line(scale_w * last_pos.getComponent(0),   scale_h * last_pos.getComponent(1),
                     scale_w * vec.get(0).getComponent(0), scale_h * vec.get(0).getComponent(1));
        }
    }
    public void keyPressed() {
        if( key == ' ' && !started ) {
            snake = new Snake(vec, grad, alpha, beta, certainty);
            started = true;
            snake.runGDA(gamma, this);
        }
    }
}