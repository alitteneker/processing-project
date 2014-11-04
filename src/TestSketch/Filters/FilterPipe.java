package TestSketch.Filters;
import java.util.*;

import processing.core.*;

public class FilterPipe extends Filter {
    protected ArrayList<Filter> queue = new ArrayList<Filter>();
    protected PApplet applet;
    
    public FilterPipe(PApplet applet) {
        super(applet);
    }
    public int size() {
        return queue.size();
    }
    public void push(Filter k) {
        queue.add(k);
    }
    public void push(Filter[] k) {
        for( int i = 0; i < k.length; ++i )
            queue.add(k[i]);
    }
    public void pushFront(Filter k) {
        queue.add(0, k);
    }
    public Filter pop() {
        if( queue.size() > 0 )
            return queue.remove( 0 );
        return null;
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
    public float[][] applyToPixels(float[][] pixels, int width, int height) {
        long time = System.currentTimeMillis();
        float[][] ret = pixels.clone();
        for( int i = 0; i < size(); ++i )
            ret = get(i).applyToPixels(ret, width, height);
        System.out.println("Filter Pipe Time: " + ( System.currentTimeMillis() - time ));
        return ret;
    }
    
}
