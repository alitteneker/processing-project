package TestSketch.Filters;
import java.util.*;

import TestSketch.Tools.KernelUtil;
import processing.core.*;

public class FilterPipe extends Filter {
    protected ArrayList<Filter> queue = new ArrayList<Filter>();
    protected PApplet applet;
    protected boolean compressed = false;
    
    public FilterPipe(PApplet applet) {
        super(applet);
    }
    public int size() {
        return queue.size();
    }
    public void push(Filter k) {
        this.compressed = false;
        queue.add(k);
    }
    public void push(Filter[] k) {
        for( int i = 0; i < k.length; ++i )
            queue.add(k[i]);
    }
    public void pushFront(Filter k) {
        this.compressed = false;
        queue.add(0, k);
    }
    public Filter pop() {
        if( queue.size() > 0 ) {
            this.compressed = false;
            return queue.remove( 0 );
        }
        return null;
    }
    public Filter replace(int index, Filter re) {
        this.compressed = false;
        return queue.set(index, re);
    }
    public Filter remove(int index) {
        if( index < 0 || index >= size() )
            return null;
        return queue.remove(index);
    }
    public Filter get(int index) {
        if( index < 0 || index >= queue.size() )
            return null;
        return queue.get(index);
    }
    public PImage apply(PImage in, boolean same) {
        if( !this.compressed )
            compressQueue();
        return super.apply(in, same);
    }
    public void compressQueue() {
        if( size() < 2 )
            return;
        long time = System.currentTimeMillis();
        Filter last = queue.get(0);
        int size = size();
        for( int i = 1; i < queue.size(); ++i ) {
            Filter next = queue.get(i);
            if( next.getClass().equals(last.getClass()) ) {
                // TODO: magic number here, what's the performance relationship?
                if( next instanceof Kernel && ( ((Kernel)next).getWidth() + ((Kernel)last).getWidth() - 1) * (((Kernel)next).getHeight() + ((Kernel)last).getHeight() - 1) < 49 ) {
                    Kernel k = KernelUtil.combineKernels((Kernel)next, (Kernel)last);
                    replace( i, k );
                    remove( --i );
                    last = k;
                    continue;
                }
            }
            last = next;
        }
        if( ( size -= size() ) > 0 )
            System.out.println("Compressed " + size + " kernels away (" + size() + " remaining) in " + (System.currentTimeMillis() - time) + " ms.");
        this.compressed = true;
    }
    public float[][] applyToPixels(float[][] pixels, int width, int height, boolean normalize) {
        long time = System.currentTimeMillis();
        float[][] ret = pixels.clone();
        for( int i = 0; i < size(); ++i )
            ret = get(i).applyToPixels(ret, width, height, normalize);
        System.out.println("Filter Pipe Time: " + ( System.currentTimeMillis() - time ));
        return ret;
    }
}
