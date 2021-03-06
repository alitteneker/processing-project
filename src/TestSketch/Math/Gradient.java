package TestSketch.Math;

import processing.core.PApplet;
import processing.core.PImage;
import TestSketch.Filters.ContrastFilter;
import TestSketch.Filters.Filter;
import TestSketch.Tools.Util;

public class Gradient {
    protected int width, height, components;
    protected float[][] data;
    
    public Gradient(float [][] data, int width, int height) {
        if( data.length == 0 || data[0].length == 0 )
            throw new IllegalArgumentException("Cannot create gradient with no data.");
        if( data.length != width * height )
            throw new IllegalArgumentException("Input data is not compatible size to width and height.");
        this.data = data;
        this.width = width;
        this.height = height;
        this.components = data[0].length;
    }
    public Gradient(float[][] pixels, int width, int height, MonoOperator[] operators) {
        setSize(width, height, operators.length);
        buildFromOperators(pixels, width, height, operators);
    }
    public Gradient(float[] pixels, int width, int height, MonoOperator[] operators) {
        setSize(width, height, operators.length);
        buildFromOperators(pixels, width, height, operators);
    }
    public Gradient(PImage img, MonoOperator[] operators) {
        setSize(img.width, img.height, operators.length);
        buildFromOperators(img, operators);
    }
    public boolean buildFromOperators(PImage img, MonoOperator[] operators) {
        img.loadPixels();
        return buildFromOperators(Util.getRGB(img.pixels, Util.applet), img.width, img.height, operators);
    }
    public boolean buildFromOperators(float[][] pixels, int width, int height, MonoOperator[] operators) {
        if( this.width != width || this.height != height || this.components != operators.length )
            return false;
        int i,j;
        for( i = 0; i < pixels.length; ++i )
            for( j = 0; j < operators.length; ++j )
                data[i][j]= operators[j].getPixelValue(pixels, i%width, i/width, i, width, height);
        return true;
    }
    public boolean buildFromOperators(float[] pixels, int width, int height, MonoOperator[] operators) {
        if( this.width != width || this.height != height || this.components != operators.length )
            return false;
        int i,j;
        for( i = 0; i < pixels.length; ++i )
            for( j = 0; j < operators.length; ++j )
                data[i][j]= operators[j].getPixelValue(pixels, i%width, i/width, i, width, height);
        return true;
    }
    public Gradient( int width, int height, int components ) {
        setSize(width, height, components);
    }
    public Gradient( Vector[][] data ) {
        if( data.length == 0 || data[0].length == 0 || data[0][0].getSize() == 0 )
            return;
        setSize( data[0].length, data.length, data[0][0].getSize() );
        setData(data);
    }
    public Gradient( float[][][] data ) {
        if( data.length == 0 || data[0].length == 0 || data[0][0].length == 0 )
            return;
        setSize(data[0].length, data.length, data[0][0].length);
        setData(data);
    }
    public Gradient(float[][] data) {
        if( data.length == 0 || data[0].length == 0 )
            return;
        setSize(data[0].length, data.length, 1);
        setData(data);
    }
    public void setSize( int width, int height, int components) {
        data = new float[width * height][components];
        this.width = width;
        this.height = height;
        this.components = components;
    }
    public boolean setData(float[][][] data) {
        if( data.length != width )
            return false;
        for( int i = 0; i < data.length; ++i ) {
            if( data[i].length != height )
                return false;
            for( int j = 0; j < data[0].length; ++j ) {
                if( data[i][j].length != components )
                    return false;
                this.data[i + j * width] = data[i][j];
            }
        }
        return true;
    }
    public boolean setData(float[][] data) {
        if( data.length != (width * height) )
            return false;
        for( int i = 0; i < data.length; ++i ) {
            if( data[i].length != components )
                return false;
            this.data[i] = data[i];
        }
        return true;
    }
    public boolean setData(Vector[] data) {
        if( data.length != width * height )
            return false;
        for( int i = 0; i < data.length; ++i ) {
            if( data[i].getSize() != components )
                return false;
            this.data[i] = data[i].data;
        }
        return true;
    }
    public boolean setData(Vector[][] data) {
        if( data.length != width )
            return false;
        for( int i = 0; i < data.length; ++i ) {
            if( data[i].length != height)
            for( int j = 0; j < data[i].length; ++j ) {
                if( data[i][j].getSize() != components )
                    return false;
                this.data[i + j * width] = data[i][j].data;
            }
        }
        return true;
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public int getComponents() {
        return components;
    }
    public boolean isOutOfBounds(float x, float y) {
        return ( x < 0 || x >= width || y < 0 || y >= height );
    }
    public Vector getAt(float x, float y) {
        if( isOutOfBounds(x, y) )
            return null;

        int minX = MathTools.floor(x), minY = MathTools.floor(y),
            maxX = MathTools.ceil(x),  maxY = MathTools.ceil(y);

        if( x == maxX )
            ++maxX;
        if( y == maxY )
            ++maxY;

        Vector ret = new Vector(components);

        maxX = MathTools.minMax(maxX, 0, width - 1);
        maxY = MathTools.minMax(maxY, 0, height - 1);

        ret.addEquals((1 - x + minX) * (1 - y + minY), getAt(minX, minY));
        ret.addEquals((1 - x + minX) * (1 - maxY + y), getAt(minX, maxY));
        ret.addEquals((1 - maxX + x) * (1 - y + minY), getAt(maxX, minY));
        ret.addEquals((1 - maxX + x) * (1 - maxY + y), getAt(maxX, maxY));

        return ret;
    }
    public Vector getAt(int x, int y) {
        if( isOutOfBounds(x, y) )
            return null;
        return new Vector(data[x + y * width ]);
    }
    public float getLengthAt(int x, int y) {
        return getAt(x, y).getLength();
    }
    public void scale(float scale) {
        for( int i = 0; i < data.length; ++i )
            for( int j = 0; j < data[i].length; ++j )
                data[i][j] *= scale;
    }
    public float[] getAllLengths() {
        float[] ret = new float[width * height];
        for( int y = 0; y < height; ++y )
            for( int x = 0; x < width; ++x )
                ret[x + ( y * width )] = getLengthAt(x,y);
        return ret;
    }
    public int[] toPixels(PApplet applet) {
        float[] fdata = getAllLengths();
        int[] ret = new int[data.length];
        for( int i = 0; i < fdata.length; ++i )
            ret[i] = Util.toProcColor( MathTools.minMax(fdata[i], 0, 255), applet );
        return ret;
    }
    public PImage toImage(PApplet applet) {
        PImage out = applet.createImage(width, height, PApplet.RGB);

        float[] fdata = getAllLengths();
        (new ContrastFilter(applet)).applyInline(fdata, false);
        out.pixels = Util.greyToProcColor(fdata, applet);
        out.updatePixels();

        return out;
    }
    public Gradient applyFilter(Filter app) {
        float[][] new_data = app.applyToPixels(data, width, height, false);
        return new Gradient(new_data, width, height);
    }
    public void applyFilterInline(Filter app) {
        setData(app.applyToPixels(data, width, height));
    }
}
