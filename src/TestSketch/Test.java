package TestSketch;

import java.util.ArrayList;

import Snakes.Snake;
import Snakes.Methods.Continuation;
import Snakes.Methods.GradientDescent;
import Snakes.Methods.GradientDescentPSO;
import Snakes.Methods.PointPSO;
import Snakes.Methods.SnakeMethod;
import Snakes.Methods.SquareSearch;
import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Tools.KernelUtil;
import TestSketch.Tools.Util;
import processing.core.*;

public class Test extends PApplet {

    private static final long serialVersionUID = 1L;

    PImage[] img = new PImage[4];
    int iwidth, iheight;
    float lastx = 0, lasty = 0;
    
    String filename = "maple leaf.jpg";
    boolean started = false;
    int saved = 1;

    Gradient grad;
    Snake snake;
    SnakeMethod method;
    ArrayList<Vector> vec;
    float min_control_dist = 2;
    
    // standard controls for snakes
    final float grad_cap = 10f;
    final float tau = 0.7f, roh = 0.7f, certainty = 2f;

    public void loadImage() {
        img[0] = loadImage(filename);
        
        // scale the image down if it is too big
        KernelUtil.maxPixelSize(img[0], 360000);
        
        // store the initial size of the image, we'll be using it later
        iwidth = img[0].width;
        iheight = img[0].height;
        
        // set the window to attempt to be the same size as the image
        size( iwidth, iheight );
    }

    public void setup() {
        Util.applet = this;
        noLoop();

        vec = new ArrayList<Vector>();
        
        loadImage();
        img[1] = KernelUtil.buildGaussianBlur( 5, 1.0f, this ).apply(img[0]);

        // The idea here it to try to build a gradient that attracts to EDGES rather than just light or dark
        Gradient original = new Gradient( img[1], KernelUtil.buildGradientSet(this) );
        float[] lengths = original.getAllLengths();
        grad = new Gradient( MathTools.minMax(lengths, 0, grad_cap), img[1].width, img[1].height, KernelUtil.buildGradientSet(this) );
        grad.applyFilter(KernelUtil.buildGaussianBlurPipe(11, 5, this));
        
        // The next line should be uncommented if the area inside the snake is darker than what's outside.
        grad.scale(-1);
        
        img[2] = grad.toImage(this);
        img[3] = original.toImage(this);
    }

    public void draw() {
        background(0);
        iwidth = width; iheight = height;
        
        image( img[0], 0, 0, iwidth, iheight);
        if( snake != null )
            snake.draw( iwidth, iheight, true, this);
        drawVertices();
    }
    public void drawVertices() {
       if( vec.size() > 0 ) {
           Vector last_pos = vec.get(0);
           float scale_w = ((float)width)/((float)grad.getWidth()),
                 scale_h = ((float)height)/((float)grad.getHeight());
           stroke(0,0,255);
           for( int i = 1; i < vec.size(); ++i) {
               Vector pos = vec.get(i);
               line(scale_w * last_pos.getComponent(0), scale_h * last_pos.getComponent(1),
                    scale_w * pos.getComponent(0),      scale_h * pos.getComponent(1));
               last_pos = pos;
           }
           if( started )
               line(scale_w * last_pos.getComponent(0),   scale_h * last_pos.getComponent(1),
                    scale_w * vec.get(0).getComponent(0), scale_h * vec.get(0).getComponent(1));
           noStroke();
           fill(255, 0, 0);
           for( int i = 0; i < vec.size(); ++i ) {
               Vector pos = vec.get(i);
               ellipse(scale_w * pos.getComponent(0), scale_h * pos.getComponent(1), 3, 3);
           }
       }
    }
    public void mouseDragged() {
        addVertex();
    }
    public void mousePressed() {
        addVertex();
    }
    public void addVertex() {
        iwidth  = width;
        iheight = height;

        if( !started ) {
            Vector pos = new Vector(
                    MathTools.normalize(mouseX, 0, iwidth,  0, img[2].width),
                    MathTools.normalize(mouseY, 0, iheight, 0, img[2].height) );
            if( vec.size() == 0 || MathTools.distance( pos, vec.get(vec.size()-1) ) >= min_control_dist )
                vec.add(pos);
        }
        redraw();
    }
    public void keyPressed() {
        if( key == ' ' && !started && vec.size() > 2 ) {
            started = true;
            startMethod("GDAPSO");
        }
    }
    
    public void startMethod(String select) {
        // controls for specific snakes method: GDA & continuity
        float gamma = 0.05f, dSigma = 2f;
        int size = 51, steps = 3;
        
        snake = new Snake(vec, grad, tau, roh, certainty);
        method = null;
        switch(select) {
            case "GDA":
                method = new GradientDescent(snake, gamma, true);
                break;
            case "CGDA":
                method = new Continuation(snake, new GradientDescent(snake, gamma, true), size, dSigma, steps, true);
                break;
            case "CPSO":
                method = new Continuation(snake, new PointPSO(snake, 15, 2f, 2f), size, dSigma, steps, true);
                break;
            case "CSS":
                method = new Continuation(snake, new SquareSearch(snake, 20), size, dSigma, steps, true);
                break;
            case "GDAPSO":
                method = new GradientDescentPSO(2*gamma, 40, 2f, 2f, snake);
                break;
            case "CGDAPSO":
                method = new Continuation(snake, new GradientDescentPSO(2*gamma, 40, 2f, 2f, snake), size, dSigma, steps, true);
                break;
        }
        if( method != null )
            method.runThread(this);
    }
}
