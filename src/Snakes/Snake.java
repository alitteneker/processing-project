package Snakes;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import TestSketch.Math.Gradient;
import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;
import TestSketch.Math.Matrix.Matrix;

public class Snake {
    public Matrix positions;
    public Matrix forces;
    public Gradient grad;
    public Vector[] energy;
    public Vector[] delta_energy;
    public int size;
    public float alpha, beta, certainty;
    public Thread inprogress;
    public boolean running = false;
    
    public PImage back = null;
    
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
    public int getSafeIndex(int i, int size) {
        while( i < 0 )
            i += size;
        while( i >= size )
            i -= size;
        return i;
    }
    public int getSafeIndex(int i) {
        return getSafeIndex(i, size);
    }
    
    public Vector getPosition(int i) {
        i = getSafeIndex(i);
        return new Vector( positions.getValue(0, i), positions.getValue(1, i) );
    }
    
    public void setPosition(int i, Vector set) {
        setPosition(i, set.getComponent(0), set.getComponent(1), true);
    }

    public void setPosition(int i, Vector set, boolean single) {
        setPosition(i, set.getComponent(0), set.getComponent(1), single);
    }
    public void setPosition(int i, float x, float y, boolean single) {
        i = getSafeIndex(i);
        positions.setValue(x, 0, i);
        positions.setValue(y, 1, i);
        updateForce(i);
        updateEnergy(i, single);
        updateDeltaEnergy(i, single);
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
    
    public void followGradientDescent(float gamma) {
        if( gamma < 0 )
            throw new IllegalArgumentException("Cannot use gamma of less than 0.");
        for( int i = 0; i < size; ++i ) {
            Vector delt = getDeltaEnergy(i).multiplyEquals( -gamma );
            setPosition( i, getPosition(i).addEquals(delt), false );
        }
        clipPositions();
        updateAllForces();
        updateAllEnergy();
    }

    public void followGradientDescent(Vector gammas) {
        if( gammas.getSize() < size )
            throw new IllegalArgumentException("Not enough gamma values.");
        for( int i = 0; i < size; ++i ) {
            float gamma = gammas.getComponent(i);
            if( gamma < 0 )
                throw new IllegalArgumentException("Cannot use gamma of less than 0.");
            Vector delt = getDeltaEnergy(i).multiplyEquals( -gamma );
            setPosition( i, getPosition(i).addEquals(delt), false );
        }
        clipPositions();
        updateAllForces();
        updateAllEnergy();
    }
    
    public float calcScalarEnergyIfDescendedBy(Vector gammas) {
        if( gammas.getSize() < size )
            throw new IllegalArgumentException("Not enough gamma values provided.");

        Matrix new_positions = new Matrix(2, size);

        for( int i = 0; i < size; ++i ) {
            float gamma = gammas.getComponent(i);
            if( gamma < 0 )
                throw new IllegalArgumentException("Cannot use gamma of less than 0.");
            Vector delt = getDeltaEnergy(i).multiplyEquals(gamma);
            Vector new_pos = getPosition(i).addEquals(delt);
            new_positions.setValue(new_pos.getComponent(0), 0, i);
            new_positions.setValue(new_pos.getComponent(1), 1, i);
        }
        
        return getScalarEnergyIf(new_positions);
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
    
    public float getScalarEnergyIf(Matrix positions) {
        Vector left, center, right, ret = new Vector();
        int size = positions.getWidth();
        
        center = new Vector( positions.getValue(0, size - 2), positions.getValue(1, size - 2) );
        right = new Vector( positions.getValue(0, size - 1), positions.getValue(1, size - 1) );
        
        for( int i = 0; i < positions.getWidth(); ++i ) {
            left = center;
            center = right;
            right = new Vector( positions.getValue(0, i), positions.getValue(1, i) );
            ret.addEquals( calcEnergyIf(left, center, right) );
        }
        return ret.getLength();
    }
    
    // get the energy of the system if we move the given control point to this new position
    public float getScalarEnergyIf(int replace, Vector pos) {
        replace = getSafeIndex(replace);
        Vector position = new Vector(pos);
        Vector sum = new Vector();
        for( int ind = 0; ind < size; ++ind )
            sum.addEquals( MathTools.abs(ind-replace) == size-1 || MathTools.abs(ind-replace) < 2 ? calcEnergyIf(ind, replace, position) : getEnergy(ind) );
        return sum.getLength();
    }
    public Vector calcEnergyIf(int center, int replace, Vector pos) {
        
        center = getSafeIndex( center );
        replace = getSafeIndex( replace );
        
        int left = getSafeIndex(center - 1), right = getSafeIndex(center + 1);
        Vector pLeft   = ( left   == replace ) ? pos : getPosition(left);
        Vector pCenter = ( center == replace ) ? pos : getPosition(center);
        Vector pRight  = ( right  == replace ) ? pos : getPosition(right);

        return calcEnergyIf(pLeft, pCenter, pRight);
    }
    
    public Vector calcEnergyIf(Vector pLeft, Vector pCenter, Vector pRight) {
        Vector ret = new Vector();
        
        ret.addEquals( alpha, pRight.add( -1, pCenter ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( beta, pLeft.add( -2, pCenter ).addEquals( pRight ).squareEquals().multiplyEquals( 0.5f ) );
        ret.addEquals( certainty, getForceAtPosition( pCenter ).squareEquals().multiplyEquals(0.5f) );
        
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
    public Vector calcDeltaEnergyIf(Matrix positions, int index) {
        int size = positions.getWidth(),
            iLL = getSafeIndex(index - 2, size),
            iL  = getSafeIndex(index - 1, size),
            iC  = getSafeIndex(index,     size),
            iR  = getSafeIndex(index + 1, size),
            iRR = getSafeIndex(index + 2, size);
        
        Vector pLL = new Vector(positions.getValue(0, iLL), positions.getValue(1, iLL));
        Vector pL  = new Vector(positions.getValue(0, iL ), positions.getValue(1, iL ));
        Vector pC  = new Vector(positions.getValue(0, iC ), positions.getValue(1, iC ));
        Vector pR  = new Vector(positions.getValue(0, iR ), positions.getValue(1, iR ));
        Vector pRR = new Vector(positions.getValue(0, iRR), positions.getValue(1, iRR));

        return calcDeltaEnergyIf(pLL, pL, pC, pR, pRR);
    }
    public Vector calcDeltaEnergyIf(Vector pLL, Vector pL, Vector pC, Vector pR, Vector pRR) {
        Vector ret = new Vector();
        
        ret.addEquals( alpha, pL.add( -2, pC ).addEquals( pR ).multiplyEquals( -1 ) );
        ret.addEquals( beta, pLL.add( -4, pL ).addEquals( 6, pC ).addEquals( -4, pR ).addEquals( pRR ) );
        ret.addEquals( certainty, pC );
        
        return ret;
    }
    
    public void draw( int width, int height, PApplet applet ) {
        draw( width, height, true, applet );
    }
    public void draw( int width, int height, boolean draw_image, PApplet applet ) {
        if( draw_image && running ) {
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
