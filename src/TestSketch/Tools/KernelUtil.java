package TestSketch.Tools;
import TestSketch.Filters.Kernel;
import TestSketch.Filters.MonochromeKernel;
import processing.core.PApplet;

public class KernelUtil {
    public static Kernel buildBoxBlur(int N, boolean mono, PApplet applet) {
        if( Math.sqrt(N) % 1 != 0 )
            return null;

        float[] ret = new float[N];
        float val = 1f/(float)N;
        for( int i = 0; i < N; ++i )
            ret[i] = val;

        return new Kernel(ret, applet);
    }
    public static Kernel buildBoxBlur(int N, PApplet applet) {
        return buildBoxBlur(N, false, applet);
    }
    
    public static Kernel buildGaussianBlur(int N, float var, boolean mono, PApplet applet) {
        if( N%2 != 1 )
            return null;

        float[][] ret = new float[N][N];
        
        float scale = 1f/(2f * (float)Math.PI * var);
        int limit = N/2;
        for( int x = 0; x <= limit; ++x ) {
            for( int y = 0; y <= limit; ++y ) {
                float val = scale * (float) Math.exp(-((x*x)+(y*y))/(2f*var));
                ret[x+limit][y+limit] = val;
                if( x > 0 )
                    ret[limit - x][y + limit] = val;
                if( y > 0 )
                    ret[x + limit][limit - y] = val;
                if( x > 0 && y > 0 )
                    ret[limit - x][limit - y] = val;
            }
        }
        
        return new Kernel(ret, applet);
    }
    public static Kernel buildGaussianBlur(int N, float var, PApplet applet) {
        return buildGaussianBlur(N, var, false, applet);
    }
    
    public static Kernel buildHighPass(boolean abs, boolean mono, PApplet applet) {
        float[] ret = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
        ret = Util.multiply(1f/8f, ret);
        Kernel kern = makeKernel(ret, mono, applet);
        manageNegative(kern, abs);
        return kern;
    }
    public static Kernel buildHighPass(PApplet applet) {
        return buildHighPass(false, false, applet);
    }

    public static Kernel buildLaplacian(boolean abs, boolean mono, PApplet applet) {
        Kernel kern = makeKernel( new float[]{ 0, -0.25f, 0, -0.25f, 1, -0.25f, 0, -0.25f, 0 }, mono, applet );
        manageNegative(kern, abs);
        return kern;
    }
    public static Kernel buildLaplacian(PApplet applet) {
        return buildLaplacian(false, false, applet);
    }
    
    public static void manageNegative(Kernel kern, boolean abs) {
        if( abs )
            kern.setAbs(true);
        else
            kern.setRange(-255, 255);
    }
    
    public static Kernel makeKernel(float[] data, boolean mono, PApplet applet) {
        Kernel ret;
        if( mono )
            ret = new MonochromeKernel(data, applet);
        else
            ret = new Kernel(data, applet);
        return ret;
    }
    public static Kernel makeKernel(float[][] data, boolean mono, PApplet applet) {
        Kernel ret;
        if( mono )
            ret = new MonochromeKernel(data, applet);
        else
            ret = new Kernel(data, applet);
        return ret;
    }
}
