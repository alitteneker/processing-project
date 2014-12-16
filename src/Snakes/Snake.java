package Snakes;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Math.Matrix.Matrix;
import TestSketch.Tools.KernelUtil;

public class Snake {
    Matrix positions;
    Matrix forces;
    Gradient grad;
    Vector[] energy;
    Vector[] delta_energy;
    int size;
    float alpha, beta, certainty;
    Thread inprogress;
    boolean running = false;
    
    PImage back = null;
    
    // tau is tension, roh is rigidity
    public Snake( Vector[] vec, Gradient grad, float tau, float roh, float certainty) {
        this.grad = grad;
        setConstants( tau, roh, certainty );
        initialize(vec);
    }
    public Snake( ArrayList<Vector> vec, Gradient grad, float tau, float roh, float certainty) {
        this.grad = grad;
        setConstants( tau, roh, certainty );
        initialize(vec);
    }
    
    public void setConstants( float tau, float roh, float certainty ) {
        this.alpha = roh * tau;
        this.beta = roh * ( 1 - tau );
        this.certainty = certainty;
    }
    
    public void initialize( ArrayList<Vector> vec ) {
        Vector[] set = new Vector[vec.size()];
        for(int i = 0; i < set.length; ++i)
            set[i] = vec.get(i);
        initialize(set);
    }
    public void initialize( Vector[] vec ) {
        size = vec.length;
        positions = new Matrix( size, 2 );
        forces = new Matrix( size, 2 );
        energy = new Vector[size];
        delta_energy = new Vector[size];
        for( int i = 0; i < size; ++i ) {
            energy[i] = new Vector();
            delta_energy[i] = new Vector();
            positions.setValue(vec[i].getComponent(0), 0, i);
            positions.setValue(vec[i].getComponent(1), 1, i);
        }
        clipPositions();
        updateAllForces();
        updateAllEnergy();
    }
    
    public void updateAllForces() {
        for( int i = 0; i < size; ++i )
            updateForce(i);
    }
    
    public void updateForce(int i) {
        i = getSafeIndex(i);
        Vector val = getForceAtPosition(i);
        forces.setValue(val.getComponent(0), 0, i);
        forces.setValue(val.getComponent(1), 1, i);
    }
    
    public Vector getForceAtPosition(int i) {
        i = getSafeIndex(i);
        Vector ret = getForceAtPosition( positions.getValue(0, i), positions.getValue(1, i) );
        if( ret == null )
            ret = new Vector();
        return ret;
    }
    public Vector getForceAtPosition(Vector pos) {
        Vector ret = grad.getAt(pos.getComponent(0), pos.getComponent(1));
        if( ret == null )
            ret = new Vector();
        return ret;
    }
    public Vector getForceAtPosition(float x, float y) {
        return grad.getAt( x, y );
    }
    
    public int getSafeIndex(int i) {
        while( i < 0 )
            i += size;
        while( i >= size )
            i -= size;
        return i;
    }
    
    public Vector getPosition(int i) {
        i = getSafeIndex(i);
        return new Vector( positions.getValue(0, i), positions.getValue(1, i) );
    }
    
    public void setPosition(int i, Vector set) {
        setPosition(i, set.getComponent(0), set.getComponent(1));
    }
    
    public void clipPositions() {
        float width = grad.getWidth()-1, height = grad.getHeight()-1;
        for(int i = 0; i < size; ++i) {
            Vector pos = getPosition(i);
            if( pos.getComponent(0) < 0 || pos.getComponent(0) > width )
                positions.setValue( MathTools.minMax( pos.getComponent(0), 0, width  ), 0, i);
            if( pos.getComponent(1) < 0 || pos.getComponent(1) > height )
                positions.setValue( MathTools.minMax( pos.getComponent(1), 0, height ), 1, i);
        }
    }

    public void setPosition(int i, float x, float y) {
        i = getSafeIndex(i);
        positions.setValue(x, 0, i);
        positions.setValue(y, 1, i);
        updateForce(i);
        updateEnergy(i, false);
        updateDeltaEnergy(i, false);
    }
    
    public void updateAllEnergy() {
        for( int i = 0; i < size; ++i ) {
            updateEnergy(i);
            updateDeltaEnergy(i);
        }
    }
    public void updateEnergy(int i) {
        updateEnergy(i, true);
    }
    public void updateEnergy(int i, boolean single) {
        i = getSafeIndex(i);
        energy[i] = calcEnergy(i);
        if( !single ) {
            updateEnergy(i+1, true);
            updateEnergy(i-1, true);
        }
    }
    public void updateDeltaEnergy(int i) {
        updateDeltaEnergy(i, true);
    }
    public void updateDeltaEnergy(int i, boolean single) {
        i = getSafeIndex(i);
        delta_energy[i] = calcDeltaEnergy(i);
        if( !single ) {
            updateDeltaEnergy(i+2, true);
            updateDeltaEnergy(i+1, true);
            updateDeltaEnergy(i-1, true);
            updateDeltaEnergy(i-2, true);
        }
    }
    
    public float getScalarEnergy() {
        Vector sum = new Vector();
        for( int i = 0; i < size; ++i )
            sum.addEquals( getEnergy(i) );
        return sum.getLength();
    }
    
    public Vector getEnergy(int i) {
        i = getSafeIndex(i);
        if( energy[i] != null )
            return energy[i];
        energy[i] = calcDeltaEnergy(i);
        return energy[i];
    }
    
    public Vector calcEnergy(int i) {
        i = getSafeIndex(i);
        Vector ret = new Vector();

        ret.addEquals( alpha, getPosition(i + 1).addEquals( -1, getPosition(i) ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( beta, getPosition(i - 1).addEquals( -2, getPosition(i) ).addEquals( getPosition(i + 1) ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( certainty, getForceAtPosition(i).squareEquals().multiplyEquals(0.5f) );

        return ret;
    }
    
    // get the energy of the system if we move the given control point to this new position
    public float getScalarEnergyIf(int replace, Vector pos) {
        replace = getSafeIndex(replace);
        Vector position = new Vector(pos);
        Vector sum = new Vector();
        for( int ind = 0; ind < size; ++ind )
            sum.addEquals( MathTools.abs( ind - replace ) < 2 ? calcEnergyIf(ind, replace, position) : getEnergy(ind) );
        return sum.getLength();
    }
    public Vector calcEnergyIf(int center, int replace, Vector pos) {
        Vector ret = new Vector();
        
        center = getSafeIndex( center );
        replace = getSafeIndex( replace );
        
        int left  = getSafeIndex(center - 1), right = getSafeIndex(center + 1);
        Vector pLeft   = ( left   == replace ) ? pos : getPosition(left);
        Vector pCenter = ( center == replace ) ? pos : getPosition(center);
        Vector pRight  = ( right  == replace ) ? pos : getPosition(right);

        ret.addEquals( alpha, pRight.addEquals( -1, pCenter ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( beta, pLeft.addEquals( -2, pCenter ).addEquals( pRight ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( certainty, getForceAtPosition(pCenter).squareEquals().multiplyEquals(0.5f) );
        
        return ret;
    }
    
    public float getScalarDeltaEnergy() {
        Vector sum = new Vector();
        for( int i = 0; i < size; ++i )
            sum.addEquals( getDeltaEnergy(i) );
        return sum.getLength();
    }
    public Vector getDeltaEnergy(int i) {
        i = getSafeIndex(i);
        if( delta_energy[i] != null )
            return delta_energy[i];
        delta_energy[i] = calcDeltaEnergy(i);
        return delta_energy[i];
    }
    public Vector calcDeltaEnergy(int i) {
        i = getSafeIndex(i);
        Vector ret = new Vector();
        
        ret.addEquals( alpha, getPosition(i-1).addEquals(-2,getPosition(i)).addEquals(getPosition(i+1)).multiplyEquals(-1) );
        ret.addEquals( beta, getPosition(i-2).addEquals(-4,getPosition(i-1)).addEquals(6,getPosition(i)).addEquals(-4,getPosition(i+1)).addEquals(getPosition(i+2)) );
        ret.addEquals( certainty, getForceAtPosition(i) );
        
        return ret;
    }
    
    public Matrix buildMultiplier(float gamma) {
        Matrix multiplier = Matrix.makeIdentity(size);
        
        // initialize meaningful matrix
        float a = -2 * alpha - 6 * beta,
              b = alpha + 4 * beta,
              c = -beta;
        Matrix inprog = new Matrix(size, size);
        for( int i = 0; i < size; ++i ) {
            int diff = size - i;
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
    
    public void runGDA(final float gamma, final PApplet applet) {
        runGDA(gamma, false, applet);
    }
    public void runGDA(final float gamma, final boolean silent, final PApplet applet) {

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
            System.out.println("Starting GDA with "+size+" control points, initial energy of "+getScalarEnergy()
                            + ", and inital dEnergy of "+getScalarDeltaEnergy() );

        while( iteration < max_iterations ) {

            if( use_matrix )
                positions.addEquals(forces.multiply(gamma * certainty)).multiplyEquals(multiplier);
            else
                for( int i = 0; i < size; ++i ) {
                    Vector delt = getDeltaEnergy(i).multiplyEquals( -gamma );
                    setPosition( i, getPosition(i).addEquals(delt) );
                }

            clipPositions();
            updateAllForces();
            updateAllEnergy();
            
            iteration++;
            if( System.currentTimeMillis() - time > 40 ) {
                applet.redraw();
                if( !silent )
                    System.out.println("Iterations " + last_iteration + "-" + iteration + "(" + (iteration-last_iteration) + ")"
                        + " took " + ( System.currentTimeMillis() - time )
                        + " with energy " + getScalarEnergy() + "(" + ( last_energy-getScalarEnergy() ) + ")"
                        + " and delta_energy " + getScalarDeltaEnergy() );
                last_iteration = iteration;
                time = System.currentTimeMillis();
                if( getScalarDeltaEnergy() == 0.0f || MathTools.abs(last_energy-getScalarEnergy()) < 0.001f )
                    break;
                else
                    last_energy = getScalarEnergy();
            }
        }
        float seconds = (float)( System.currentTimeMillis() - start ) / 1000f;
        if( !silent )
            System.out.println("GDA finished after " + iteration + " iterations and " + seconds + " seconds, and "
                            + ((float)iteration/seconds) + " iterations/second.");
    }
    public void runGDAThread(final float gamma, final PApplet applet) {
        running = true;
        inprogress = new Thread() {
            public void run() {
                runGDA(gamma, true, applet);
                running = false;
            }
        };
        inprogress.start();
    }
    
    public void runContinuation(final float gamma, final int filter_size, final float dSigma, final int steps, final PApplet applet) {

        Gradient full_grad = grad;
        for( int i = 0; i < steps; ++i ) {
            long time = System.currentTimeMillis();
            
            grad = ( i == steps - 1 )
                    ? full_grad
                    : full_grad.applyFilter( KernelUtil.buildGaussianBlurPipe(filter_size, (steps - (1 + i)) * dSigma, applet) );
            back = null;
            
            runGDA(gamma, false, applet);
            
            System.out.println("Continuation step " + ( i + 1 ) + " finished in " + ( ( System.currentTimeMillis() - time ) / 1000f ) + " seconds.");
        }
    }
    public void runContinuationThread(final float gamma, final int filter_size, final float dSigma, final int steps, final PApplet applet) {
        running = true;
        inprogress = new Thread() {
            public void run() {
                runContinuation(gamma, filter_size, dSigma, steps, applet);
                running = false;
            }
        };
        inprogress.start();
    }
    
    public void runPSO(final int p_size, final float p_i, final float p_g, final PApplet applet) {
        int max_iterations = 1000;
        ArrayList<ParticleSwarm> swarms = new ArrayList<ParticleSwarm>();
        for( int iteration = 0; iteration < max_iterations; ++iteration ) {
            long time = System.currentTimeMillis();
            for( int j = 0; j < size; ++j )
                swarms.add( new ParticleSwarm(p_size, this, j, p_i, p_g) );
            for( int j = 0; j < size; ++j )
                setPosition( j, swarms.get(j).run() );
            System.out.println("PSO iteration "+iteration+" finished after " + ( System.currentTimeMillis() - time ) + "ms");
            swarms.clear();
            applet.redraw();
        }
    }
    public void runPSOThread(final int p_size, final float p_i, final float p_g, final PApplet applet) {
        running = true;
        inprogress = new Thread() {
            public void run() {
                runPSO(p_size, p_i, p_g, applet);
                running = false;
            }
        };
        inprogress.start();
    }
    
    public void draw( int width, int height, PApplet applet ) {
        draw( width, height, true, applet );
    }
    public void draw( int width, int height, boolean draw_image, PApplet applet ) {
        if( draw_image ) {
            if( back == null )
                back = grad.toImage(applet);
            applet.image(back, 0, 0, width, height);
        }
        
        float scale_w = ((float)width)/((float)grad.getWidth()),
              scale_h = ((float)height)/((float)grad.getHeight());
        applet.stroke(255, 0, 0);
        Vector last_pos = getPosition(0);
        for( int i = 1; i <= size; ++i ) {
            Vector pos = getPosition(i);
            applet.line(scale_w * last_pos.getComponent(0), scale_h * last_pos.getComponent(1),
                        scale_w * pos.getComponent(0),      scale_h * pos.getComponent(1));
            last_pos = pos;
        }
        applet.noStroke();
        applet.fill(0, 255, 0);
        for( int i = 0; i < size; ++i ) {
            Vector pos = getPosition(i);
            applet.ellipse(scale_w * pos.getComponent(0), scale_h * pos.getComponent(1), 3, 3);
        }
    }
}
