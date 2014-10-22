package TestSketch.Filters;
import java.util.*;

import processing.core.*;

public class FilterStack extends Filter {
    protected ArrayList<Filter> stack = new ArrayList<Filter>();
    protected PApplet applet;
    
    public FilterStack(PApplet applet) {
        super(applet);
    }
    public int size() {
        return stack.size();
    }
    public void push(Filter k) {
        stack.add(0, k);
    }
    public void push(Filter[] k) {
        for( int i = 0; i < k.length; ++i )
            stack.add(0, k[i]);
    }
    public Filter pop() {
        if( stack.size() > 0 )
            return stack.remove( 0 );
        return null;
    }
    public Filter remove(int index) {
        if( index < 0 || index >= stack.size() )
            return null;
        return stack.remove(index);
    }
    public Filter get(int index) {
        if( index < 0 || index >= stack.size() )
            return null;
        return stack.get(index);
    }
    public float[][] applyToPixels(float[][] pixels, int width, int height) {
        long time = System.currentTimeMillis();
        float[][] ret = pixels.clone();
        for( int i = 0; i < size(); ++i )
            ret = get(i).applyToPixels(ret, width, height);
        System.out.println("Kernel Time: " + ( System.currentTimeMillis() - time ));
        return ret;
    }
    
}
