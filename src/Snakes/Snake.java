package Snakes;

import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Math.Matrix.Matrix;

public class Snake {
    Matrix positions;
    Matrix forces;
    Matrix multiplier;
    Gradient grad;
    Vector[] energy;
    int size;
    float alpha, beta, gamma;
    
    public Snake( Vector[] vec, Gradient grad, float alpha, float beta, float gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.grad = grad;
        initialize(vec);
        updateAllForces();
        buildMultiplier();
    }
    
    public void initialize( Vector[] vec ) {
        size = vec.length;
        positions = new Matrix( size, 2 );
        forces = new Matrix(size, 2);
        energy = new Vector[size];
        for( int i = 0; i < size; ++i ) {
            energy[i] = new Vector();
            positions.setValue(vec[i].getComponent(0), 0, i);
            positions.setValue(vec[i].getComponent(1), 1, i);
        }
    }
    
    public void buildMultiplier() {
        if( size == 0 )
            throw new IllegalArgumentException("Cannot initialize a snake with 0 control points.");
        multiplier = Matrix.makeIdentity(size);
        
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
    }
    
    public void updateAllForces() {
        for( int i = 0; i < size; ++i )
            updateForce(i);
    }
    
    public void updateForce(int i) {
        if( i < 0 || i >= size )
            return;
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
    
    public void runGDA() {
        while( /* we haven't reached some stopping condition */ true ) {
            Matrix work = forces.multiply(gamma);
            work.addEquals(positions);
            positions = multiplier.multiply(work);
        }
    }
    
    public float getScalarEnergy() {
        float sum = 0;
        for( int i = 0; i < size; ++i ) {
            energy[i] = getEnergy(i);
            sum += energy[i].getLength();
        }
        return sum;
    }
    
    public Vector getEnergy(int i) {
        i = getSafeIndex(i);
        Vector ret = new Vector();

        // add on string energy
        ret.addEquals( getPosition(i + 1).addEquals( getPosition(i), -1 ).squareEquals().multiplyEquals( 0.5f ) );

        // add on beam energy
        ret.addEquals( getPosition(i - 1).addEquals( getPosition(i), -2 ).addEquals( getPosition(i + 1) ).squareEquals().multiplyEquals( 0.5f ) );

        // add on force energy
        ret.addEquals( getForceAtPosition(i).squareEquals().multiplyEquals(0.5f) );

        return ret;
    }
    
    public Vector getDeltaEnergy(int i) {
        i = getSafeIndex(i);
        Vector ret = new Vector();
        
        // add on string delta energy
        ret.addEquals( getPosition(i-1).addEquals(getPosition(i),-2).addEquals(getPosition(i+1)).multiplyEquals(-1) );

        // add on beam delta energy
        ret.addEquals( getPosition(i-2).addEquals(getPosition(i-1),-4).addEquals(getPosition(i),6).addEquals(getPosition(i+1),-4).addEquals(getPosition(i+2)) );

        // add on force delta energy
        ret.addEquals( getForceAtPosition(i) );
        
        return ret;
    }
}
