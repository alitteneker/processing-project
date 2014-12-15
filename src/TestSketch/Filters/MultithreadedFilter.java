package TestSketch.Filters;

import java.util.ArrayList;

import processing.core.PApplet;

public abstract class MultithreadedFilter extends Filter {
    public final double PROC_THREAD_SCALE = 1;
    protected static int UID = 0;
    
    public MultithreadedFilter(PApplet applet) {
        super(applet);
    }
    // This is a stub for children to setup before running
    public void setup(float[][] pixels, int width, int height) {}
    public float[][] applyToPixels(float[][] pixels, int width, int height) {
        return applyToPixels(pixels, width, height, true);
    }
    public float[][] applyToPixels(float[][] pixels, int width, int height, boolean normalize) {
        setup(pixels, width, height);

        ThreadGroup tg = new ThreadGroup( "FT_GROUP" + ( UID++ ) );
        float[][] ret = new float[ pixels.length ][ pixels[0].length ];

        ArrayList<FilterThread> threads = new ArrayList<FilterThread>();

        final int processors = Runtime.getRuntime().availableProcessors();
        int THREAD_COUNT = Math.max( 1, (int)( processors * PROC_THREAD_SCALE ) );
        int size = height/THREAD_COUNT, i;
        for ( i = 0; i < THREAD_COUNT; ++i ) {
            threads.add(new FilterThread( this, pixels, ret, 0 , width - 1, i * size,
                    ( THREAD_COUNT - i > 1 ? ( i + 1 ) * size : height ) - 1, width, height, normalize, "FT"+i, tg) );
        }
 
        for( i = 0; i < threads.size(); ) {
            if( tg.activeCount() < processors )
                threads.get( i++ ).start();
            else
                try { Thread.sleep(5); }
                    catch (InterruptedException e) { e.printStackTrace(); }
        }
        while( tg.activeCount() > 0 ) {
            try { Thread.sleep(5); } 
                catch (InterruptedException e) { e.printStackTrace(); }
        }

        return ret;
    }

    protected void applyToPixel(float[] out, float[][] input, int x, int y, int width, int height) {
        applyToPixel(out, input, x, y, x + y * width, width, height);
    }
    protected void applyToPixel(float[] out, float[][] input, int x, int y, int loca, int width, int height) {
        applyToPixel(out, input, x, y, loca, width, height, true);
    }
    protected abstract void applyToPixel(float[] out, float[][] input, int x, int y, int loca, int width, int height, boolean normalize);
    
    protected class FilterThread extends Thread {
        public MultithreadedFilter filter;
        public float[][] input;
        public float[][] out;
        public int minx, maxx, miny, maxy, width, height, iwidth;
        public boolean normalize;
        public FilterThread(MultithreadedFilter filter,
                float[][] input, float[][] out,
                int minx, int maxx, int miny, int maxy,
                int width, int height,
                boolean normalize,
                String name, ThreadGroup group) {
            super(group, name);
            this.filter = filter; this.normalize = normalize;
            this.input = input;   this.out = out;
            this.minx = minx;     this.maxx = maxx;
            this.miny = miny;     this.maxy = maxy;
            this.width = width;   this.height = height;
        }
        public void run() {
            int x, y, loca;
            for( x = minx; x <= maxx; ++x )
                for( y = miny; y <= maxy; ++y ) {
                    loca = x + y * width;
                    filter.applyToPixel(out[loca], input, x, y, loca, width, height, normalize);
                }
        }
    }
}
