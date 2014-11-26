package TestSketch.Math;

public class Vector {
    protected float[] data;
    
    public Vector( float[] set ) {
        data = set;
    }
    public Vector( int size ) {
        if( size >= 0 )
            data = new float[size];
    }
    // default to (X,Y)
    public Vector() {
        this(2);
    }
    
    public int getSize() {
        return data.length;
    }
    public float getComponent( int comp ) {
        if( comp < getSize() && comp >= 0 )
            return data[comp];
        return 0;
    }
    public boolean setComponent( float val, int comp ) {
        if( comp >= getSize() || comp < 0 )
            return false;
        data[comp] = val;
        return true;
    }
    public float[] getData() {
        return data;
    }
    public void setData( float[] data ) {
        this.data = data;
    }
    public void setData( float data ) {
        for( int i = 0; i < this.data.length; ++i )
            this.data[i] = data;
    }

    public float[] addArray(float[] b) {
        int size = Math.min(b.length, getSize());
        float[] ret = new float[size];
        for( int i = 0; i < size; ++i ) {
            ret[i] = data[i] + b[i];
        }
        return ret;
    }
    public Vector add(Vector b) {
        return new Vector( addArray( b.getData() ) );
    }
    public void addEquals(Vector b) {
        setData(addArray(b.getData()));
    }

    public float[] multiplyArray(float s) {
        float[] ret = new float[getSize()];
        for( int i = 0; i < ret.length; ++i )
            ret[i] = this.data[i] * s;
        return ret;
    }
    public Vector multiply(float s) {
        return new Vector(multiplyArray(s));
    }
    public void multiplyEquals(float s) {
        for( int i = 0; i < this.data.length; ++i )
            this.data[i] *= s;
    }

    public float dotProduct(Vector b) {
        return MathTools.dotProduct(getData(), b.getData());
    }
    
    public float getVectorLength() {
        return MathTools.length(data);
    }
}
