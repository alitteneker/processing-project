import processing.core.PApplet;


public class MonochromeKernel extends Kernel {
    public MonochromeKernel(float[] data, PApplet applet) {
        super(data, applet);
    }
    public MonochromeKernel(float[][] data, PApplet applet) {
        super(data, applet);
    }
    public int[] applyKernelToPixels(int[] pixels, int width, int height) {
        int[] ret = new int[pixels.length];
        for( int x = 0; x < width; ++x ) {
            for( int y = 0; y < height; ++y ) {
                // TODO: would be much better if we could shift by row, then only have to get the next row each time
                float color = applyKernelToSubset(
                        Util.average(Util.getPixelsARGB(x, y, this.size, pixels, width, height, this.applet)));
                ret[Util.getPixelIndex(x, y, width)] = applet.color(color);
            }
        }
        return ret;
    }
}
