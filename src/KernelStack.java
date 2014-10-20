import java.util.*;

import processing.core.*;

public class KernelStack {
    protected ArrayList<Kernel> stack = new ArrayList<Kernel>();
    protected PApplet applet;
    
    public KernelStack(PApplet applet) {
        this.applet = applet;
    }
    public int size() {
        return stack.size();
    }
    public void push(Kernel k) {
        stack.add(0, k);
    }
    public void push(Kernel[] k) {
        for( int i = 0; i < k.length; ++i )
            stack.add(0, k[i]);
    }
    public Kernel pop() {
        if( stack.size() > 0 )
            return stack.remove( 0 );
        return null;
    }
    public Kernel remove(int index) {
        if( index < 0 || index >= stack.size() )
            return null;
        return stack.remove(index);
    }
    public Kernel get(int index) {
        if( index < 0 || index >= stack.size() )
            return null;
        return stack.get(index);
    }
    public PImage apply(PImage in, boolean same) {
        in.loadPixels();

        PImage out;
        if( same )
            out = in; 
        else {
            out = this.applet.createImage(in.width, in.height, in.format);
            out.pixels = in.pixels.clone();
        }
        
        for( int i = 0; i < size(); ++i )
            out = get(i).apply(out, true);

        out.updatePixels();
        return out;
    }
    
}
