package TestSketch.Math;

public class Vector {
    protected float[] data;
    
    public Vector( float... set) {
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
        return addArray(b, 1);
    }
    public float[] addArray(float[] b, float scale) {
        int size = Math.min(b.length, getSize());
        float[] ret = new float[size];
        for( int i = 0; i < size; ++i )
            ret[i] = data[i] + b[i] * scale;
        return ret;
    }
    public Vector add(Vector b) {
        return new Vector( addArray( b.getData() ) );
    }
    public Vector addEquals(Vector b) {
        return addEquals(1,b);
    }
    public Vector addEquals(float scale, Vector b) {
        int size = Math.min(b.getSize(), getSize());
        for( int i = 0; i < size; ++i )
            data[i] += b.getComponent(i) * scale;
        return this;
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
    public Vector multiplyEquals(float s) {
        for( int i = 0; i < this.data.length; ++i )
            this.data[i] *= s;
        return this;
    }
    
    public Vector square() {
        float[] data = new float[getSize()];
        for( int i = 0; i < data.length; ++i )
            data[i] = this.data[i] * this.data[i];
        return new Vector(data);
    }
    public Vector squareEquals() {
        for( int i = 0; i < data.length; ++i )
            data[i] *= data[i];
        return this;
    }

    public float dotProduct(Vector b) {
        return MathTools.dotProduct(getData(), b.getData());
    }
    
    public float getLength() {
        return MathTools.length(data);
    }
    
    public void printVector() {
        for( int i = 0; i < data.length; ++i )
            System.out.print(data[i] + "\t");
        System.out.println();
    }
}
