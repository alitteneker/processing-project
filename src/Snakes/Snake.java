package Snakes;

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
    Vector[] denergy;
    int size;
    float alpha, beta, certainty;
    
    public Snake( Vector[] vec, Gradient grad, float alpha, float beta, float certainty) {
        this.alpha = alpha;
        this.beta = beta;
        this.certainty = certainty;
        this.grad = grad;
        initialize(vec);
        updateAllForces();
    }
    
    public void initialize( Vector[] vec ) {
        size = vec.length;
        positions = new Matrix( size, 2 );
        forces = new Matrix(size, 2);
        energy = new Vector[size];
        energy = new Vector[size];
        for( int i = 0; i < size; ++i ) {
            energy[i] = new Vector();
            positions.setValue(vec[i].getComponent(0), 0, i);
            positions.setValue(vec[i].getComponent(1), 1, i);
        }
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
        multiplier = multiplier.invert();
        if( multiplier == null )
            throw new IllegalArgumentException("Unable to invert matrix for GDA.");
        return multiplier;
    }
    
    public void updateAllForces() {
        for( int i = 0; i < size; ++i )
            updateForce(i);
    }
    
    public void updateForce(int i) {
        i = getSafeIndex(i);
        Vector val = getForceAtPosition(i);
        forces.setValue(val.getComponent(0), 0, i);
        forces.setValue(val.getComponent(0), 1, i);
    }
    
    public Vector getForceAtPosition(int i) {
        i = getSafeIndex(i);
        return getForceAtPosition( positions.getValue(0, i), positions.getValue(1, i) );
    }
    
    public Vector getForceAtPosition(float x, float y) {
        return grad.getAt( x, y );
    }
    
    public int getSafeIndex(int i) {
        return MathTools.cyclicMinMax(i, 0, size - 1);
    }
    
    public Vector getPosition(int i) {
        i = getSafeIndex(i);
        return new Vector( new float[] { positions.getValue(0, i), positions.getValue(0, i) } );
    }
    
    public void setPosition(int i, Vector set) {
        setPosition(i, set.getComponent(0), set.getComponent(1));
    }

    public void setPosition(int i, float x, float y) {
        i = getSafeIndex(i);
        positions.setValue(x, 0, i);
        positions.setValue(y, 1, i);
        updateForce(i);
        updateEnergy(i,false);
        updateDeltaEnergy(i, false);
    }
    
    public void runGDA(float gamma) {
        Matrix multiplier = buildMultiplier(gamma);
        int iteration = 0;
        
        System.out.println("Starting GDA with "+size+" control points, initial energy of "+getScalarEnergy()+", and inital dEnergy of "+getScalarDeltaEnergy());
        while( getScalarDeltaEnergy() > 0.5f ) {
            long time = System.currentTimeMillis();
            
            Matrix work = forces.multiply(gamma * certainty);
            work.addEquals(positions);
            positions = multiplier.multiply(work);
            
            updateAllForces();
            updateAllEnergy();
            
            iteration++;
            System.out.println("Iteration "+iteration+" took "+(System.currentTimeMillis()-time)+" with energy "+getScalarEnergy()+" and dEnergy "+getScalarDeltaEnergy());
        }
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
        energy[i] = getEnergy(i);
        if( !single ) {
            updateEnergy(i+1);
            updateEnergy(i-1);
        }
    }
    public void updateDeltaEnergy(int i) {
        updateEnergy(i);
    }
    public void updateDeltaEnergy(int i, boolean single) {
        i = getSafeIndex(i);
        denergy[i] = getDeltaEnergy(i);
        if( !single ) {
            updateDeltaEnergy(i+2);
            updateDeltaEnergy(i+1);
            updateDeltaEnergy(i-1);
            updateDeltaEnergy(i-2);
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
    public float getScalarDeltaEnergy() {
        Vector sum = new Vector();
        for( int i = 0; i < size; ++i )
            sum.addEquals( getDeltaEnergy(i) );
        return sum.getLength();
    }
    
    public Vector getEnergy(int i) {
        i = getSafeIndex(i);
        if( denergy[i] != null )
            return denergy[i];
        denergy[i] = calcDeltaEnergy(i);
        return denergy[i];
    }
    public Vector calcEnergy(int i) {
        i = getSafeIndex(i);
        Vector ret = new Vector();

        // add on string energy
        ret.addEquals( alpha, getPosition(i + 1).addEquals( -1, getPosition(i) ).squareEquals().multiplyEquals( 0.5f ) );

        // add on beam energy
        ret.addEquals( beta, getPosition(i - 1).addEquals( -2, getPosition(i) ).addEquals( getPosition(i + 1) ).squareEquals().multiplyEquals( 0.5f ) );

        // add on force energy
        ret.addEquals( certainty, getForceAtPosition(i).squareEquals().multiplyEquals(0.5f) );

        return ret;
    }
    public Vector calcEnergyIf(int i, Vector pos) {
        i = getSafeIndex(i);
        Vector ret = new Vector();
        
        // add on string energy
        ret.addEquals( alpha, getPosition(i + 1).addEquals( -1, pos ).squareEquals().multiplyEquals( 0.5f ) );

        // add on beam energy
        ret.addEquals( beta, getPosition(i - 1).addEquals( -2, pos ).addEquals( getPosition(i + 1) ).squareEquals().multiplyEquals( 0.5f ) );

        // add on force energy
        ret.addEquals( certainty, getForceAtPosition(i).squareEquals().multiplyEquals(0.5f) );
        
        return ret;
    }
    
    public Vector getDeltaEnergy(int i) {
        i = getSafeIndex(i);
        if( denergy[i] != null )
            return denergy[i];
        denergy[i] = calcDeltaEnergy(i);
        return denergy[i];
    }
    public Vector calcDeltaEnergy(int i) {
        i = getSafeIndex(i);
        Vector ret = new Vector();
        
        // add on string delta energy
        ret.addEquals( alpha, getPosition(i-1).addEquals(-2,getPosition(i)).addEquals(getPosition(i+1)).multiplyEquals(-1) );

        // add on beam delta energy
        ret.addEquals( beta, getPosition(i-2).addEquals(-4,getPosition(i-1)).addEquals(6,getPosition(i)).addEquals(-4,getPosition(i+1)).addEquals(getPosition(i+2)) );

        // add on force delta energy
        ret.addEquals( certainty, getForceAtPosition(i) );
        
        return ret;
    }
    
    public void draw(PApplet applet) {
        applet.stroke(255,0,0);
        Vector last_pos = getPosition(0);
        for( int i = 1; i <= size; ++i ) {
            Vector pos = getPosition(i);
            applet.line(last_pos.getComponent(0), last_pos.getComponent(1), pos.getComponent(0), pos.getComponent(1));
            last_pos = pos;
        }
    }
}
