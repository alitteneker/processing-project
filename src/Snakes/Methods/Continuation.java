package Snakes.Methods;

import processing.core.PApplet;
import Snakes.Snake;
import TestSketch.Math.Gradient;
import TestSketch.Tools.KernelUtil;

// This runs continuation over any other implemented search method
public class Continuation extends SnakeMethod {
    int filter_size;
    float dSigma;
    int steps;
    SnakeMethod method;
    
    public Continuation(Snake sn, SnakeMethod method, int filter_size, float dSigma, int steps, boolean sil) {
        super(sn);
        this.filter_size = filter_size;
        this.dSigma = dSigma;
        this.steps = steps;
        this.method = method;
    }
    
    public void runMethod(final PApplet applet) {
        Gradient full_grad = snake.grad;
        
        for( int i = 0; i < steps; ++i ) {
            long time = System.currentTimeMillis();
            
            snake.grad = ( i == steps - 1 )
                    ? full_grad
                    : full_grad.applyFilter( KernelUtil.buildGaussianBlurPipe(filter_size, (steps - (1 + i)) * dSigma, applet) );
            snake.back = null;
            
            method.runMethod(applet);
            
            System.out.println("Continuation step " + ( i + 1 ) + " finished in " + ( ( System.currentTimeMillis() - time ) / 1000f ) + " seconds.");
        }
    }
}
