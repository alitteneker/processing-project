package Snakes;

import java.util.ArrayList;

import processing.core.PApplet;
import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Math.Matrix.Matrix;

public class Snake {
    Matrix positions;
    Matrix forces;
    Gradient grad;
    Vector[] energy;
    Vector[] delta_energy;
    int size;
    float alpha, beta, certainty;
    Thread inprogress;
    
    public Snake( Vector[] vec, Gradient grad, float alpha, float beta, float certainty) {
        this.alpha = alpha;
        this.beta = beta;
        this.certainty = certainty;
        this.grad = grad;
        initialize(vec);
    }
    public Snake( ArrayList<Vector> vec, Gradient grad, float alpha, float beta, float certainty) {
        this.alpha = alpha;
        this.beta = beta;
        this.certainty = certainty;
        this.grad = grad;
        initialize(vec);
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
        System.out.println("Is multiplier symmetric: " + multiplier.isSymmetric());
        return ret;
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
        return getForceAtPosition( positions.getValue(0, i), positions.getValue(1, i) );
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
            if( pos.getComponent(0) < 0 || pos.getComponent(0) > width-1 )
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
    
    public void runGDA(final float gamma, final PApplet applet) {
        inprogress = new Thread() {
            public void run() {
                Matrix multiplier = buildMultiplier(gamma);
                int iteration = 0, last_iteration = 0;
                int max_iterations = 100000;
                float last_energy = 0;
                
                long start = System.currentTimeMillis(), time = System.currentTimeMillis();
                System.out.println("Starting GDA with "+size+" control points, initial energy of "+getScalarEnergy()+", and inital dEnergy of "+getScalarDeltaEnergy());
                while( iteration < max_iterations && getScalarDeltaEnergy() > 0.0f && MathTools.abs(last_energy-getScalarEnergy()) > 0 ) {
                    last_energy = getScalarEnergy();
                    
//                    positions.addEquals(forces.multiply(gamma * certainty)).multiplyEquals(multiplier);
                    for( int i = 0; i < size; ++i ) {
                        Vector delt = getDeltaEnergy(i).multiplyEquals( -gamma );
                        setPosition( i, getPosition(i).addEquals(delt) );
                    }
                    clipPositions();
                    updateAllForces();
                    updateAllEnergy();
                    
                    iteration++;
                    if( System.currentTimeMillis() - time > 40 ) {
                        System.out.println("Iterations " + last_iteration + "-" + iteration + "(" + (iteration-last_iteration) + ")"
                                + " took " + ( System.currentTimeMillis() - time )
                                + " with energy " + getScalarEnergy()
                                + " and delta_energy " + getScalarDeltaEnergy() );
                        last_iteration = iteration;
                        time = System.currentTimeMillis();
                    }
                }
                System.out.println("GDA finished after " + iteration + " iterations and " + ( System.currentTimeMillis() - start ) + "ms.");
            }
        };
        inprogress.start();
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
    // get the energy of the system if we move the given control point to this new position
    public float getScalarEnergyIf(int i, Vector pos) {
        i = getSafeIndex(i);
        Vector sum = new Vector();
        for( int ind = 0; ind < size; ++ind )
            sum.addEquals( ind == i ? calcEnergyIf(i, pos) : getEnergy(i) );
        return sum.getLength();
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
    public Vector calcEnergyIf(int i, Vector pos) {
        i = getSafeIndex(i);
        Vector ret = new Vector();
        
        ret.addEquals( alpha, getPosition(i + 1).addEquals( -1, pos ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( beta, getPosition(i - 1).addEquals( -2, pos ).addEquals( getPosition(i + 1) ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( -certainty, getForceAtPosition(i).squareEquals().multiplyEquals(0.5f) );
        
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
    
    public void draw( int width, int height, PApplet applet ) {
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
    }
}
