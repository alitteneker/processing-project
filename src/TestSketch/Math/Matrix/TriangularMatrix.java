package TestSketch.Math.Matrix;

import TestSketch.Math.MathTools;

public class TriangularMatrix extends Matrix {
    // store data only for necessary side
    protected boolean isUpper;
    public TriangularMatrix( float[][] data ) {
        throw new IllegalArgumentException("Cannot use a two-dimensional array to declare a triangular matrix.");
    }
    public TriangularMatrix(int width, int height, boolean upper) {
        isUpper = upper;
        setSize(width, height);
    }
    public TriangularMatrix(float[] data, int width, int height, boolean upper) {
        isUpper = upper;
        setData(data, width, height);
    }
    public void setSize(int newWidth, int newHeight) {
        if( newWidth != newHeight )
            throw new IllegalArgumentException("Non-square triangular matrices are not supported.");
        width = newWidth;
        height = newHeight;
        data = new float[ width * ( width + 1 ) / 2 ];
    }
    public void setData(float[] data, int newWidth, int newHeight) {
        if( newWidth != newHeight )
            throw new IllegalArgumentException("Non-square triangular matrices are not supported.");
        if( data.length != ( newWidth * ( newWidth + 1 ) / 2 ) )
            throw new IllegalArgumentException("Data length does match expected size.");
        width = newWidth;
        height = newHeight;
        this.data = data;
    }
    public void setData(float[] data) {
        if( data.length != ( width * ( width + 1 ) / 2 ) )
            throw new IllegalArgumentException("Data length does match matrix width.");
        this.data = data;
    }
    public void setData(float[][] data) {
        throw new IllegalArgumentException("Cannot use a two-dimensional array to declare a triangular matrix.");
    }
    public int getIndex(int i, int j) {
        if( i < j == isUpper || i == j ) {
            if( isUpper ) {
                int temp = i;
                i = j;
                j = temp;
            }
            return ( i * ( i + 1 ) / 2 ) + j;
        }
        return -1;
    }
    public int getSafeIndex(int i, int j) {
        i = MathTools.minMax(i, 0, width);
        if( isUpper ) {
            int temp = i;
            i = MathTools.minMax(j, 0, width);
            j = temp;
        }
        j = MathTools.minMax(j, 0, i);
        return ( i * ( i + 1 ) / 2 ) + j;
    }
    public float getValue(int i, int j) {
        if( i < j != isUpper && i != j )
            return 0;
        return data[getSafeIndex(i,j)];
    }
    public void setValue(float val, int i, int j) {
        if( i < j != isUpper && i != j )
            throw new IllegalArgumentException("Cannot set the value of a variable not in the triangular. Convert to a full matrix to complete this calculation.");
        data[getSafeIndex(i,j)] = val;
    }
    public boolean isSquare() {
        return true;
    }
    public boolean isSymmetric() {
        return false;
    }
    public boolean isTriangular() {
        for( int i = 0; i < width; ++i )
            if( getValue(i,i) == 0 )
                return false;
        return true;
    }
    public boolean isTechnicalTriangular() {
        return true;
    }
    public boolean isUpperTriangular() {
        if( !isUpper )
            return false;
        for( int i = 0; i < width; ++i )
            if( getValue(i, i) == 0 )
                return false;
        return true;
    }
    public boolean isLowerTriangular() {
        if( isUpper )
            return false;
        for( int i = 0; i < width; ++i )
            if( getValue(i, i) == 0 )
                return false;
        return true;
    }
    public boolean isTechnicalUpperTriangular() {
        return isUpper;
    }
    public boolean isTechnicalLowerTriangular() {
        return !isUpper;
    }
    public Matrix transpose() {
        return new TriangularMatrix(data, width, height, !isUpper);
    }
    public void transposeEquals() {
        isUpper = !isUpper;
    }
    public Matrix multiply(float val) {
        float[] newData = new float[data.length];
        for( int i = 0; i < data.length; ++i )
            newData[i] = data[i] * val;
        return new TriangularMatrix(newData, width, height, isUpper);
    }
    public Matrix multiply(Matrix other) {
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        if( other.isTechnicalTriangular() && other.isTechnicalUpperTriangular() == isTechnicalUpperTriangular() ) {
            TriangularMatrix ret = new TriangularMatrix(data, width, height, isUpper);
            for( int i = 0; i < width; ++i )
                for( int j = 0; j < width; ++j )
                    for( int b = 0; b < width; ++b )
                        if( ( i < j == isUpper || i == j ) && ( j < b == isUpper || j == b ) )
                            ret.setValue( ret.getValue(i, j) + getValue(i, j) * other.getValue(j, b), i, j );
            return ret;
        }
        else
            return super.multiply(other);
    }
    public Matrix multiplyEquals(Matrix other) {
        if( !other.isTechnicalTriangular() )
            throw new IllegalArgumentException("Cannot save product of triangular and non-triangular matrix in triangular matrix.");
        if( other.isTechnicalUpperTriangular() != isTechnicalUpperTriangular() )
            throw new IllegalArgumentException("Cannot save sum of upper and lower matrix in triangular matrix.");
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        float[] newData = new float[data.length];
        for( int i = 0; i < width; ++i )
            for( int j = 0; j < width; ++j )
                for( int b = 0; b < width; ++b )
                    if( ( i < j == isUpper || i == j ) && ( j < b == isUpper || j == b ) )
                        newData[getSafeIndex(i,j)] += getValue(i, j) * other.getValue(j, b);
        setData(newData);
        return this;
    }
    public Matrix add(Matrix other) {
        if( width != other.getWidth() || height != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        if( other.isTechnicalTriangular() && other.isTechnicalUpperTriangular() == isTechnicalUpperTriangular() ) {
            TriangularMatrix ret = new TriangularMatrix(width, height, isUpper);
            for( int i = 0; i < height; ++i )
                for( int j = 0; j < width; ++j )
                    if( i < j == isUpper || i == j )
                        ret.setValue( getValue(i, j) + other.getValue(i, j), i, j );
            return ret;
        }
        else
            return super.add(other);
    }
    public Matrix addEquals(Matrix other) {
        if( !other.isTechnicalTriangular() )
            throw new IllegalArgumentException("Cannot save sum of triangular and non-triangular matrix in triangular matrix.");
        if( other.isTechnicalUpperTriangular() != isTechnicalUpperTriangular() )
            throw new IllegalArgumentException("Cannot save sum of upper and lower matrix in triangular matrix.");
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        for( int i = 0; i < height; ++i )
            for( int j = 0; j < width; ++j )
                if( i < j == isUpper || i == j )
                    setValue( getValue(i, j) + other.getValue(i, j), i, j );
        return this;
    }
    public Matrix toFullMatrix() {
        Matrix ret = new Matrix(width, height);
        for( int i = 0; i < width; ++i )
            ret.setValue(data[i], i, i);
        return ret;
    }
}
