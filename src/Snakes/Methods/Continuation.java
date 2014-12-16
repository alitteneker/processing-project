package Snakes.Methods;

import processing.core.PApplet;
import Snakes.Snake;
import TestSketch.Math.Gradient;
import TestSketch.Tools.KernelUtil;

public class Continuation extends GradientDescent {
    int filter_size;
    float dSigma;
    int steps;
    
    public Continuation(Snake sn, float gam, int filter_size, float dSigma, int steps, boolean sil) {
        super(sn, gam, sil);
        this.filter_size = filter_size;
        this.dSigma = dSigma;
        this.steps = steps;
    }
    
    public void runMethod(final PApplet applet) {
        Gradient full_grad = snake.grad;
        
        for( int i = 0; i < steps; ++i ) {
            long time = System.currentTimeMillis();
            
            snake.grad = ( i == steps - 1 )
                    ? full_grad
                    : full_grad.applyFilter( KernelUtil.buildGaussianBlurPipe(filter_size, (steps - (1 + i)) * dSigma, applet) );
            snake.back = null;
            
            super.runMethod(applet);
            
            System.out.println("Continuation step " + ( i + 1 ) + " finished in " + ( ( System.currentTimeMillis() - time ) / 1000f ) + " seconds.");
        }
    }
}
