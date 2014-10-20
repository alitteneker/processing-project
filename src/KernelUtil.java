
public class KernelUtil {
    public static float[] buildBoxBlur(int N) {
        if( N%2 != 1 )
            return null;

        float[] ret = new float[N];
        float val = 1f/(float)N;
        for( int i = 0; i < N; ++i )
            ret[i] = val;

        return ret;
    }
    
    public static float[][] buildGaussianBlur(int N, float var) {
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
        
        return ret;
    }
    
    public static float[] buildHighPass() {
        float[] ret = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
        ret = Util.multiply(1f/9f, ret);
        return ret;
    }
}
