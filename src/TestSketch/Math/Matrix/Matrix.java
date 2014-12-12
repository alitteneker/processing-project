package TestSketch.Math.Matrix;

import TestSketch.Math.MathTools;

public class Matrix {
    protected float[] data;
    protected int width, height;
    
    public Matrix() { }
    public Matrix(float[] data, int width, int height) {
        setData(data, width, height);
    }
    public Matrix( float[][] data ) {
        setSize(data.length > 0 ? data[0].length : 0, data.length);
        setData(data);
    }
    public Matrix(int width, int height) {
        setSize(width, height);
    }
    public void setSize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        data = new float[width * height];
    }
    public void setData(float[] data, int newWidth, int newHeight) {
        if( data.length != newWidth * newHeight )
            throw new IllegalArgumentException("Data length does match matrix width and height.");
        width = newWidth;
        height = newHeight;
        this.data = data;
    }
    public void setData(float[] data) {
        if( data.length != width * height )
            throw new IllegalArgumentException("Data length does match matrix width and height.");
        this.data = data;
    }
    public void setData(float[][] data) {
        if( data.length != height || ( data.length > 0 ? data[0].length : 0 ) != width )
            throw new IllegalArgumentException("Data size does match matrix width and height.");
        for( int i = 0; i < height; ++i )
            for( int j = 0; j < width; ++j )
                this.data[ j + i * width ] = data[i][j];
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getIndex(int i, int j) {
        return MathTools.minMax( i * height + j, 0, width * height - 1 );
    }
    public int getSafeIndex(int i, int j) {
        return MathTools.minMax( i, 0, height-1 ) * width + MathTools.minMax( j, 0, width );
    }
    public float getValueAtIndex(int ind) {
        if( ind < 0 || ind >= width * height)
            return 0;
        return data[ind];
    }
    public float getValue(int i, int j) {
        return data[getSafeIndex(i,j)];
    }
    public void setValue(float val, int i, int j) {
        data[getSafeIndex(i,j)] = val;
    }
    public boolean isSquare() {
        return getWidth() == getHeight();
    }
    public boolean isDiagonal() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i == j && getValue(i,j) == 0 )
                    return false;
                if( i != j && getValue(i, j) != 0 )
                    return false;
            }
        return true;
    }
    public boolean isTechnicalDiagonal() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i != j && getValue(i, j) != 0 )
                    return false;
            }
        return true;
    }
    public boolean isTriangular() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        boolean foundUpper = false, foundLower = false;
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i > j && getValue(i,j) != 0 )
                    foundUpper = true;
                if( i < j && getValue(i,j) != 0 )
                    foundLower = true;
                if( i == j && getValue(i,j) == 0 )
                    return false;
                if( foundUpper && foundLower )
                    return false;
            }
        return true;
    }
    public boolean isTechnicalTriangular() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        boolean foundUpper = false, foundLower = false;
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i > j && getValue(i,j) != 0 )
                    foundUpper = true;
                if( i < j && getValue(i,j) != 0 )
                    foundLower = true;
                if( foundUpper && foundLower )
                    return false;
            }
        return true;
    }
    public boolean isUpperTriangular() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i > j && getValue(i,j) != 0 )
                    return false;
                if( i == j && getValue(i,j) == 0 )
                    return false;
            }
        return true;
    }
    public boolean isLowerTriangular() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i < j && getValue(i,j) != 0 )
                    return false;
                if( i == j && getValue(i,j) == 0 )
                    return false;
            }
        return true;
    }
    public boolean isTechnicalUpperTriangular() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i > j && getValue(i,j) != 0 )
                    return false;
            }
        return true;
    }
    public boolean isTechnicalLowerTriangular() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 0; i < size; ++i )
            for( int j = 0; j < size; ++j ) {
                if( i < j && getValue(i,j) != 0 )
                    return false;
            }
        return true;
    }
    public boolean isSymmetric() {
        if( !isSquare() )
            return false;
        int size = getWidth();
        for( int i = 1; i < size; ++i )
            for( int j = i + 1; j < size; ++j )
                if( getValue(i, j) != getValue(j, i) )
                    return false;
        return true;
    }
    public Matrix transpose() {
        float[] newData = new float[data.length];
        for( int i = 0; i < height; ++i )
            for( int j = 0; j < width; ++j )
                newData[j * height + i] = getValue(i, j);
        return new Matrix(newData, height, width);
    }
    public void transposeEquals() {
        float[] newData = new float[data.length];
        for( int i = 0; i < height; ++i )
            for( int j = 0; j < width; ++j )
                newData[j * height + i] = getValue(i, j);
        setData(newData, height, width);
    }
    public Matrix multiply(float val) {
        int width = getWidth(), height = getHeight();
        Matrix ret = new Matrix(width, height);
        for( int i = 0; i < height; ++i )
            for( int j = 0; j < width; ++j )
                ret.setValue( getValue(i,j) * val, i, j );
        return ret;
    }
    public void multiplyEquals(float val) {
        for( int i = 0; i < data.length; ++i )
            data[i] *= val;
    }
    public Matrix multiply(Matrix other) {
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        int newWidth = other.getWidth(), newHeight = getHeight(), comps = getWidth();
        float[] data = new float[ newWidth * newHeight ];
        for( int i = 0; i < newHeight; ++i )
            for( int j = 0; j < comps; ++j )
                for( int b = 0; b < newWidth; ++b )
                    data[ b + i * newWidth ] += getValue(i, j) * other.getValue(j, b);
        return new Matrix(data, newWidth, newHeight);
    }
    public Matrix multiplyEquals(Matrix other) {
        if( getWidth() != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        int newWidth = other.getWidth(), newHeight = getHeight(), comps = getWidth();
        float[] data = new float[ newWidth * newHeight ];
        for( int i = 0; i < newHeight; ++i )
            for( int j = 0; j < comps; ++j )
                for( int b = 0; b < newWidth; ++b )
                    data[  b + i * newWidth ] += getValue(i, j) * other.getValue(j, b);
        setData(data, newWidth, newHeight);
        return this;
    }
    public Matrix add(Matrix other) {
        int width = getWidth(), height = getHeight();
        if( width != other.getWidth() || height != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        float[] data = new float[ width * height ];
        for( int i = 0; i < getHeight(); ++i )
            for( int j = 0; j < getWidth(); ++j )
                data[ j + i * width ] = getValue(i,j) + other.getValue(i,j);
        return new Matrix(data, width, height);
    }
    public Matrix addEquals(Matrix other) {
        int width = getWidth(), height = getHeight();
        if( width != other.getWidth() || height != other.getHeight() )
            throw new IllegalArgumentException("Matrices are not of compatible sizes.");
        for( int i = 0; i < getHeight(); ++i )
            for( int j = 0; j < getWidth(); ++j )
                setValue( getValue(i, j) + other.getValue(i,j), i, j );
        return this;
    }
    public Matrix addEquals(float val, int i, int j) {
        setValue(getValue(i,j) + val, i, j);
        return this;
    }
    public DiagonalMatrix invertDiagonal() {
        if( !isDiagonal() )
            return null;
        DiagonalMatrix ret = new DiagonalMatrix(width, height);
        float val;
        for( int i = 0; i < width; ++i )
            if( ( val = getValue(i,i) ) != 0 )
                ret.setValue(1f/val, i, i);
        return ret;
    }
    public void invertDiagonalEquals() {
        if( !isDiagonal() )
            return;
        float val;
        for( int i = 0; i < width; ++i )
            if( ( val = getValue(i,i) ) != 0 )
                setValue(1f/val, i, i);
    }
    public Matrix invertTriangular() {
        if( !isTriangular() )
            return null;
        boolean isUpper = isUpperTriangular();
        TriangularMatrix ret = new TriangularMatrix(width, height, isUpper);
        for( int i = 0; i < height; ++i ) {
            for( int j = 0; j < width; ++j ) {
                if( i == j )
                    ret.setValue( 1f/getValue(i, j), i, j);
                else if( i < j == isUpper ) {
                    int a = i, b = j;
                    float calc = 0;
                    if( isUpper ) {
                        a = j;
                        b = i;
                    }
                    int limit = b + MathTools.abs(a - b);
                    for( int k = b; k < limit; ++k )
                        calc -= isUpper ? ( getValue(k, a) * ret.getValue(b, k) ) : ( getValue(a, k) * ret.getValue(k, b) );
                    calc /= getValue(a, a);
                    ret.setValue(calc, i, j);
                }
            }
        }
        return ret;
    }
    public void invertTriangularEquals() {
        if( !isTriangular() )
            return;
        boolean isUpper = isUpperTriangular();
        float[] newData = new float[data.length];
        for( int i = 0; i < height; ++i ) {
            for( int j = 0; j < width; ++j ) {
                if( i == j )
                    data[getSafeIndex(i,j)] = 1f/getValue(i, j);
                else if( i < j == isUpper ) {
                    int a = i, b = j;
                    float calc = 0;
                    if( isUpper ) {
                        a = j;
                        b = i;
                    }
                    int limit = b + MathTools.abs(a - b);
                    for( int k = b; k < limit; ++k )
                        calc -= isUpper ? ( getValue(k, a) * newData[getSafeIndex(k, b)] ) : ( getValue(a, k) * newData[getSafeIndex(k, b)] );
                    calc /= getValue(a, a);
                    data[getSafeIndex(i,j)] = calc;
                }
            }
        }
        setData(newData);
    }
    public Matrix[] decomposeLDL() {
        if( !isSymmetric() )
            return null;
        Matrix[] ret = new Matrix[]{ new TriangularMatrix(width, height, false), new DiagonalMatrix(width, height), null };
        for( int i = 0; i < height; ++i ) {
            for( int j = 0; j < width; ++j ) {
                if( i == j ) {
                    float val = getValue(i, j);
                    for( int k = 0; k < j; ++k )
                        val -= MathTools.square( ret[0].getValue(j, k) ) * ret[1].getValue(k, k);
                    ret[1].setValue(val, i, j);
                    ret[0].setValue(1, i, j);
                }
                if( i > j ) {
                    float val = getValue(i, j);
                    for( int k = 0; k < j; ++k )
                        val -= ret[0].getValue(i, k) * ret[0].getValue(j, k) * ret[1].getValue(k, k);
                    val /= ret[1].getValue(j, j);
                    ret[0].setValue(val, i, j);
                }
            }
        }
        ret[2] = ret[0].transpose();
        return ret;
    }
    public Matrix invertLDL() {
        Matrix[] mat = decomposeLDL();
        if( mat == null || mat.length == 0)
            return null;
        Matrix ret = makeIdentity(width);
        for( int i = mat.length - 1; i >= 0; --i ) {
            Matrix inv = mat[i].invert();
            if( inv == null )
                return null;
            else
                ret.multiplyEquals(inv);
        }
        return ret;
    }
    public Matrix attemptDowngrade() {
        if( isTechnicalDiagonal() ) {
            if( this instanceof DiagonalMatrix )
                return this;
            DiagonalMatrix ret = new DiagonalMatrix(width, height);
            for( int i = 0; i < width; ++i )
                ret.setValue( getValue(i, i), i, i );
            return ret;
        }
        if( isTechnicalTriangular() ) {
            if( this instanceof TriangularMatrix )
                return this;
            boolean isUpper = isUpperTriangular();
            TriangularMatrix ret = new TriangularMatrix(width, height, isUpper);
            for( int i = 0; i < height; ++i )
                for( int j = 0; j < width; ++j )
                    if( i < j == isUpper || i == j )
                        ret.setValue( getValue(i, j), i, j );
            return ret;
        }
        // TODO: symmetric and or banded storage if developed
        return this;
    }
    public Matrix invert() {
        Matrix ret = null;
        ret = invertDiagonal();
        if( ret == null )
            ret = invertTriangular();
        if( ret == null )
            ret = invertLDL();
        // TODO: Add more techniques
        return ret;
    }
    public void printMatrix() {
        for( int i = 0; i < height; ++i ) {
            for( int j = 0; j < width; ++j ) {
                float val = getValue(i, j);
                System.out.print( ( MathTools.abs(val) > 0.00001 ? val : 0 ) + "\t" );
            }
            System.out.println();
        }
        System.out.println();
    }
    public static Matrix makeIdentity(int size, boolean diagonal) {
        Matrix ret = diagonal ? new DiagonalMatrix(size, size) : new Matrix(size, size);
        for( int i = 0; i < size; ++i )
            ret.setValue(1, i, i);
        return ret;
    }
    public static Matrix makeIdentity(int size) {
        return makeIdentity(size, false);
    }
}
