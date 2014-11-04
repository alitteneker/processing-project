package TestSketch.Math;

import processing.core.PApplet;
import TestSketch.Tools.Util;

public class Gradient {
    protected int width, height, components;
    protected Vector[][] data;
    
    public Gradient( int width, int height, int components ) {
        setSize(width, height, components);
    }
    public Gradient( Vector[][] data ) {
        if( data.length == 0 || data[0].length == 0 || data[0][0].getSize() == 0 )
            return;
        setSize( data.length, data[0].length, data[0][0].getSize() );
        setData(data);
    }
    public Gradient( float[][][] data ) {
        if( data.length == 0 || data[0].length == 0 || data[0][0].length == 0 )
            return;
        setSize(data.length, data[0].length, data[0][0].length);
        setData(data);
    }
    public Gradient(float[][] data) {
        if( data.length == 0 || data[0].length == 0 )
            return;
        setSize(data.length, data[0].length, 1);
        setData(data);
    }
    public void setSize( int width, int height, int components ) {
        setSize( width, height, components, false );
    }
    public void setSize( int width, int height, int components, boolean instantiate) {
        data = new Vector[width][height];
        this.width = width; this.height = height; this.components = components;
        if( instantiate )
            for( int i = 0; i < width; ++i )
                for( int j = 0; j < height; ++j )
                    data[i][j] = new Vector(components);
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
                this.data[i][j].setData(data[i][j]);
            }
        }
        return true;
    }
    public boolean setData(float[][] data) {
        if( data.length != width )
            return false;
        for( int i = 0; i < data.length; ++i ) {
            if( data[i].length != height )
                return false;
            for( int j = 0; j < data[0].length; ++j ) {
                this.data[i][j].setData(data[i][j]);
            }
        }
        return true;
    }
    public boolean setData(Vector[][] data) {
        if( data.length != width )
            return false;
        for( int i = 0; i < data.length; ++i ) {
            if( data[i].length != height )
                return false;
            for( int j = 0; j < data[0].length; ++j ) {
                if( data[i][j].getSize() != components )
                    return false;
                this.data[i][j] = data[i][j];
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
    
    public Vector getAt(int x, int y) {
        if( x < 0 || x >= width || y < 0 || y >= height )
            return null;
        return data[x][y];
    }
    public float getLengthAt(int x, int y) {
        return getAt(x, y).getVectorLength();
    }
    public float[] toFloatPixels() {
        float[] ret = new float[width * height];
        for( int y = 0; y < height; ++y )
            for( int x = 0; x < width; ++x )
                ret[x + ( y * width )] = getLengthAt(x,y);
        return ret;
    }
    public int[] toPixels(PApplet applet) {
        float[] fdata = toFloatPixels();
        int[] ret = new int[data.length];
        for( int i = 0; i < fdata.length; ++i )
            ret[i] = Util.toProcColor(fdata[i], applet);
        return ret;
    }
}
