package TestSketch.Tools;
import TestSketch.Filters.FilterPipe;
import TestSketch.Filters.Kernel;
import TestSketch.Filters.MonochromeKernel;
import TestSketch.Math.MathTools;
import processing.core.PApplet;
import processing.core.PImage;

public class KernelUtil {
    public static Kernel buildBoxBlur(int N, boolean mono, PApplet applet) {
        if( Math.sqrt(N) % 1 != 0 )
            return null;

        float[] ret = new float[N];
        float val = 1f/(float)N;
        for( int i = 0; i < N; ++i )
            ret[i] = val;

        return new Kernel(ret, N, N, applet);
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
        
        return new Kernel(normalizeKernelData(ret), applet);
    }
    public static Kernel buildGaussianBlur(int N, float var, PApplet applet) {
        return buildGaussianBlur(N, var, false, applet);
    }
    public static Kernel buildLinearGaussianBlur(int N, float var, PApplet applet) {
        if( N % 2 == 0 )
            return null;
        float[] data = new float[N];
        float scale = 1f / (float)Math.sqrt(2f * (float)Math.PI * var);
        int limit = N/2;
        for( int x = -limit; x <= limit; ++x )
            data[x+limit] = scale * (float)Math.exp( -(x * x) / (2f * var) );
        return new Kernel(normalizeKernelData(data), N, 1, applet);
    }
    public static FilterPipe buildGaussianBlurPipe(int N, float var, PApplet applet) {
        FilterPipe ret = new FilterPipe(applet);
        Kernel blur = buildLinearGaussianBlur(N, var, applet);
        ret.push(blur);
        ret.push(blur.transpose(false));
        return ret;
    }
    
    public static Kernel buildHighPass(boolean abs, boolean mono, PApplet applet) {
        float[][] ret = { { -1, -1, -1 }, { -1, 8, -1 }, { -1, -1, -1 } };
        ret = MathTools.multiply(1f/8f, ret);
        Kernel kern = makeKernel(ret, mono, applet);
        manageNegative(kern, abs);
        return kern;
    }
    public static Kernel buildHighPass(PApplet applet) {
        return buildHighPass(false, false, applet);
    }

    public static Kernel buildLaplacian(boolean abs, boolean mono, PApplet applet) {
        Kernel kern = makeKernel( new float[][]{ { 0, -0.25f, 0 }, { -0.25f, 1, -0.25f }, { 0, -0.25f, 0 } }, mono, applet );
        manageNegative(kern, abs);
        return kern;
    }
    public static Kernel buildLaplacian(PApplet applet) {
        return buildLaplacian(false, false, applet);
    }
    public static MonochromeKernel[] buildSobel(PApplet applet) {
        float[][] data = normalizeKernelData(new float[][]{{ -1, -2, -1 },{ 0, 0, 0 },{ 1, 2, 1 }});
        MonochromeKernel y = new MonochromeKernel(data, applet);
        return new MonochromeKernel[] { (MonochromeKernel)y.transpose(false), y };
    }
    public static MonochromeKernel[] buildGradientSet(PApplet applet) {
        float[][] data = normalizeKernelData(new float[][]{{ -1, 0, 1 }});
        MonochromeKernel x = new MonochromeKernel(data, applet);
        return new MonochromeKernel[] { x, (MonochromeKernel)x.transpose(false) };
    }
    public static MonochromeKernel[] buildNthGradientSet(int n, PApplet applet) {
        if( n % 2 != 0 )
            throw new IllegalArgumentException("Cannot calculate unevenly ordered derivative via this method.");
        float[][] data = new float[1][n+1];
        for( int i = 0; i <= n; ++i )
            data[0][i] = ( i % 2 == 0 ? 1 : -1 ) * MathTools.choose( n, i );
        MonochromeKernel x = new MonochromeKernel(normalizeKernelData(data), applet);
        printKernel(x);
        return new MonochromeKernel[] { x, (MonochromeKernel)x.transpose(false) };
    }
    
    public static Kernel combineKernels(Kernel a, Kernel b) {
        Kernel k = makeKernel(
                combineKernels( a.getData(), a.getWidth(), a.getHeight(), b.getData(), b.getWidth(), b.getHeight() ),
                a instanceof MonochromeKernel && b instanceof MonochromeKernel,
                a.applet);
        if( a.getMode() == b.getMode() )
            k.setMode(a.getMode());
        k.setRange(Math.min(a.getMin(), b.getMin()), Math.max(a.getMax(), b.getMax()));    
        return k;
    }
    public static float[][] combineKernels(float[] a, int widthA, int heightA, float[] b, int widthB, int heightB) {
        int widthRet = widthA + widthB - 1, heightRet = heightA + heightB - 1;
        float[][] ret = new float[heightRet][widthRet];
        int i, j;
        for( i = 0; i < a.length; ++i ) {
            for( j = 0; j < b.length; ++j ) {
                ret[ (i / widthA) + (j / widthB) ][ (i % widthA) + (j % widthB) ] += a[i] * b[j];
            }
        }
        return normalizeKernelData(ret);
    }
    public static float[][] normalizeKernelData(float[][] data) {
        float posSum = 0, negSum = 0;
        for( int i = 0; i < data.length; ++i )
            for( int j = 0; j < data[i].length; ++j )
                if( data[i][j] < 0 )
                    negSum -= data[i][j];
                else
                    posSum += data[i][j];
        if( ( posSum == 0 && negSum == 0 ) || ( posSum == 1 && negSum == 1 ) )
            return data;
        return MathTools.multiply( 1f / Math.max(posSum, negSum), data );
    }
    public static float[] normalizeKernelData(float[] data) {
        float posSum = 0, negSum = 0;
        for( int i = 0; i < data.length; ++i )
            if( data[i] < 0 )
                negSum -= data[i];
            else
                posSum += data[i];
        if( ( posSum == 0 && negSum == 0 ) || ( posSum == 1 && negSum == 1 ) )
            return data;
        return MathTools.product( data, 1f / Math.max(posSum, negSum) );
    }
    
    public static void manageNegative(Kernel kern, boolean abs) {
        if( abs )
            kern.setMode(Kernel.MODE_ABS);
        else
            kern.setRange(-255, 255);
    }
    
    public static Kernel makeKernel(float[] data, int width, int height, boolean mono, PApplet applet) {
        Kernel ret;
        if( mono )
            ret = new MonochromeKernel(data, width, height, applet);
        else
            ret = new Kernel(data, width, height, applet);
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
    public static void printKernel(Kernel k) {
        int width = k.getWidth();
        float[] data = k.getData();
        for( int i = 0; i < data.length; ++i ) {
            if( i > 0 )
                System.out.print( ( i % width == 0 ) ? "\n" : "\t" );
            System.out.print( data[i] );
        }
        System.out.println('\n');
    }
    
    public static void maxPixelSize(PImage check, int maxSize) {
        if( check.width * check.height > maxSize ) {
            float scale = Util.sqrt((float)( check.width * check.height ) / maxSize );
            check.resize( Math.round(((float)check.width) / scale), Math.round(((float)check.height) / scale) );
        }
    }
}
