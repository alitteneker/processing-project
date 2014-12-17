package Snakes.Methods;

import processing.core.PApplet;
import Snakes.Snake;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Math.Matrix.Matrix;

public class GradientDescent extends SnakeMethod {
    float gamma;
    boolean silent = false;
    
    public GradientDescent(Snake sn, float gam, boolean sil) {
        super(sn);
        gamma = gam;
        silent = sil;
    }
    
    public Matrix buildMultiplier(float gamma) {
        Matrix multiplier = Matrix.makeIdentity(snake.size);
        
        // initialize meaningful matrix
        float a = -2 * snake.alpha - 6 * snake.beta,
              b = snake.alpha + 4 * snake.beta,
              c = -snake.beta;
        Matrix inprog = new Matrix(snake.size, snake.size);
        for( int i = 0; i < snake.size; ++i ) {
            int diff = snake.size - i;
            inprog.setValue(a, i, i);
            if( diff > 1 ) {
                inprog.setValue( b, i + 1, i     );
                inprog.setValue( b, i,     i + 1 );
            }
            else {
                inprog.setValue( b, i, 0 );
                inprog.setValue( b, 0, i );
            }
            if( diff > 2 ) {
                inprog.setValue( c, i + 2, i     );
                inprog.setValue( c, i,     i + 2 );
            }
            else {
                inprog.setValue( c, i,        2 - diff );
                inprog.setValue( c, 2 - diff, i        );
            }
        }
        
        // annoying subtraction
        inprog.multiplyEquals(-gamma);
        multiplier.addEquals(inprog);
        Matrix ret = multiplier.invert();
        if( ret == null )
            throw new IllegalArgumentException("Unable to invert matrix for GDA.");
        return ret;
    }
    
    public void runMethod(PApplet applet) {

        // We can do GDA either with a matrix or with vector math
        final boolean use_matrix = false;
        Matrix multiplier = null;
        if( use_matrix )
            multiplier = buildMultiplier(gamma);

        int iteration = 0, last_iteration = 0;
        final int max_iterations = 10000;
        float last_energy = 0;
        
        long start = System.currentTimeMillis(), time = System.currentTimeMillis();
        if( !silent )
            System.out.println("Starting GDA with "+snake.size+" control points, initial energy of "+snake.getScalarEnergy()
                            + ", and inital dEnergy of "+snake.getScalarDeltaEnergy() );

        while( iteration < max_iterations ) {

            if( use_matrix )
                snake.positions.addEquals(snake.forces.multiply(gamma * snake.certainty)).multiplyEquals(multiplier);
            else
                for( int i = 0; i < snake.size; ++i ) {
                    Vector delt = snake.getDeltaEnergy(i).multiplyEquals( -gamma );
                    snake.setPosition( i, snake.getPosition(i).addEquals(delt), false );
                }

            snake.clipPositions();
            snake.updateAllForces();
            snake.updateAllEnergy();
            
            iteration++;
            if( System.currentTimeMillis() - time > 40 ) {
                applet.redraw();
                if( !silent )
                    System.out.println("Iterations " + last_iteration + "-" + iteration + "(" + (iteration-last_iteration) + ")"
                        + " took " + ( System.currentTimeMillis() - time )
                        + " with energy " + snake.getScalarEnergy() + "(" + ( last_energy-snake.getScalarEnergy() ) + ")"
                        + " and delta_energy " + snake.getScalarDeltaEnergy() );
                last_iteration = iteration;
                time = System.currentTimeMillis();
                if( snake.getScalarDeltaEnergy() == 0.0f || MathTools.abs(last_energy-snake.getScalarEnergy()) < 0.001f )
                    break;
                else
                    last_energy = snake.getScalarEnergy();
            }
        }
        float seconds = (float)( System.currentTimeMillis() - start ) / 1000f;
        if( !silent )
            System.out.println("GDA finished after " + iteration + " iterations and " + seconds + " seconds, and "
                            + ((float)iteration/seconds) + " iterations/second.");
    }
}
