import processing.core.*;

public class Kernel {

    PApplet applet;
    
    protected int size;
    protected float[] data;
    protected float minval = 0, maxval = 255;

    public Kernel(float[][] data, PApplet applet ) {
        this.setData(data);
        this.applet = applet;
    }
    public Kernel(float[] data, PApplet applet) {
        this.setData(data);
        this.applet = applet;
    }
    public boolean setData(float[][] data) {
        int size = data.length;
        float[] newData = new float[size*size];

        for( int i = 0; i < size; ++i ) {
            if( data[i].length != size )
                return false;
            for( int j = 0; j < size; ++j )
                newData[j+(size*i)] = data[i][j];
        }

        this.size = size;
        this.data = newData;
        return true;
    }
    public boolean setData(float[] data) {
        double size = Math.sqrt(data.length);
        if( size % 1.0 != 0.0 )
            return false;
        
        this.size = (int) size;
        this.data = data;
        return true;
    }
    public boolean setRange(float min, float max) {
        if( min > max )
            return false;
        this.minval = min;
        this.maxval = max;
        return true;
    }
    public PImage apply(PImage in) {
        return apply(in, false);
    }
    public PImage apply(PImage in, boolean same) {
        int width = in.width, height = in.height;
        in.loadPixels();

        PImage out = same ? in : this.applet.createImage(width, height, in.format);
        int[] pixels = same ? in.pixels.clone() : in.pixels;
        out.pixels = applyKernelToPixels(pixels, in.width, in.height);
        out.updatePixels();

        return out;
    }
    protected int[] applyKernelToPixels(int[] pixels, int width, int height) {
        int[] ret = new int[pixels.length];
        for( int x = 0; x < width; ++x ) {
            for( int y = 0; y < height; ++y ) {
                // TODO: would be much better if we could shift by row, meaning we'd only have to get the next row each time
                float[] color = applyKernelToSubset(Util.getPixelsARGB(x, y, this.size, pixels, width, height, this.applet));
                ret[Util.getPixelIndex(x, y, width)] = applet.color(color[1], color[2], color[3], color[0]);
            }
        }
        return ret;
    }
    protected float[] applyKernelToSubset(float[][] input) {
        float[] val = Util.dotProduct(input, this.data);
        for( int i = 0; i < val.length; ++i )
            if( Math.abs(val[i]) < 0.0001)
                val[i] = 0;
        val = normalize(val);
        return Util.cyclicMaxMin(val, minval, maxval);
    }
    protected float applyKernelToSubset(float[] input) {
        float val = Util.dotProduct(input, this.data);
        if( Math.abs(val) < 0.0001)
            val = 0;
        val = normalize(val);
        if( Math.abs(val) > 255 )
            System.out.println(val);
        return Util.cyclicMaxMin(val, minval, maxval);
    }
    protected float normalize(float input) {
        if( minval == 0 && maxval == 255 )
            return input;
        return Util.normalize(input, minval, maxval, 0, 255);
    }
    protected float[] normalize(float[] input) {
        float[] ret = new float[input.length];
        for( int i = 0; i < input.length; ++i ) 
            ret[i] = normalize(input[i]);
        return ret;
    }
}
