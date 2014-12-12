package TestSketch.Math.Matrix;

import TestSketch.Math.MathTools;

public class DiagonalMatrix extends Matrix {

    // Store diagonal data in flattened array
    
    public DiagonalMatrix(float[] data, int width, int height) {
        setData(data, width, height);
    }
    public DiagonalMatrix(int width, int height) {
        setSize(width, height);
    }
    public DiagonalMatrix( float[][] data ) {
        throw new IllegalArgumentException("Cannot use a two-dimensional array to declare a diagonal matrix.");
    }
    public void setSize(int newWidth, int newHeight) {
        if( newWidth != newHeight )
            throw new IllegalArgumentException("Non-square diagonal matrices are not supported.");
        width = newWidth;
        height = newHeight;
        data = new float[width];
    }
    public void setData(float[] data, int newWidth, int newHeight) {
        if( newWidth != newHeight )
            throw new IllegalArgumentException("Non-square diagonal matrices are not supported.");
        if( data.length != newWidth )
            throw new IllegalArgumentException("Data length does match matrix width.");
        width = newWidth;
        height = newHeight;
        this.data = data;
    }
    public void setData(float[] data) {
        if( data.length != width )
            throw new IllegalArgumentException("Data length does match matrix width.");
        this.data = data;
    }
    public void setData(float[][] data) {
        throw new IllegalArgumentException("Cannot use a two-dimensional array to declare a diagonal matrix.");
    }
    public int getIndex(int i, int j) {
        if( i == j )
            return i;
        return -1;
    }
    public int getSafeIndex(int i, int j) {
        return MathTools.minMax( i, 0, width );
    }
    public float getValue(int i, int j) {
        if( i != j )
            return 0;
        return data[getSafeIndex(i,j)];
    }
    public void setValue(float val, int i, int j) {
        if( i != j )
            throw new IllegalArgumentException("Cannot set the value of a variable not on the diagonal. Convert to a more detailed matrix to complete this calculation.");
        data[getSafeIndex(i,j)] = val;
    }
    public boolean isSquare()                   { return true; }
    public boolean isSymmetric()                { return true; }
    public boolean isDiagonal()                 { return true; }
    public boolean isTriangular()               { return true; }
    public boolean isTechnicalTriangular()      { return true; }
    public boolean isUpperTriangular()          { return true; }
    public boolean isLowerTriangular()          { return true; }
    public boolean isTechnicalUpperTriangular() { return true; }
    public boolean isTechnicalLowerTriangular() { return true; }
    public Matrix transpose() { return new DiagonalMatrix(data, width, height); }
    public void transposeEquals() { /* Nothing to do here. */ }
    public Matrix multiply(float val) {
        float[] newData = new float[data.length];
        for( int i = 0; i < data.length; ++i )
            newData[i] = data[i] * val;
        return new DiagonalMatrix(newData, width, height);
    }
    public Matrix multiply(Matrix other) {
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        if( other.isDiagonal() ) {
            float[] data = new float[width];
            for( int i = 0; i < width; ++i )
                data[i] = this.data[i] * other.getValue(i, i);
            return new DiagonalMatrix(data, width, height);
        }
        else
            return super.multiply(other);
    }
    public Matrix multiplyEquals(Matrix other) {
        if( !other.isDiagonal() )
            throw new IllegalArgumentException("Cannot save product of diagonal and non-diagonal matrix in diagonal matrix.");
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        for( int i = 0; i < height; ++i )
            data[i] *= other.getValue(i, i);
        return this;
    }
    public Matrix add(Matrix other) {
        if( width != other.getWidth() || height != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        if( other.isDiagonal() ) {
            float[] data = new float[width];
            for( int i = 0; i < width; ++i )
                data[i] = this.data[i] + other.getValue(i, i);
            return new DiagonalMatrix(data, width, height);
        }
        else
            return super.add(other);
    }
    public Matrix addEquals(Matrix other) {
        if( !other.isDiagonal() )
            throw new IllegalArgumentException("Cannot save sum of diagonal and non-diagonal matrix in diagonal matrix.");
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        for( int i = 0; i < height; ++i )
            data[i] += other.getValue(i, i);
        return this;
    }
    public Matrix toFullMatrix() {
        Matrix ret = new Matrix(width, height);
        for( int i = 0; i < width; ++i )
            ret.setValue(data[i], i, i);
        return ret;
    }
}
