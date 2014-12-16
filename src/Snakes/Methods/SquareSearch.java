package Snakes.Methods;

import processing.core.PApplet;
import Snakes.Snake;
import TestSketch.Math.Vector;

public class SquareSearch extends SnakeMethod {
    int s_size;
    public SquareSearch(Snake sn, int size) {
        snake = sn;
        s_size = size;
    }
    public void runMethod(PApplet applet) {
        float limit = ((float)s_size) / 2f;
        int max_iterations = 10000;
        applet.redraw();
        for( int iteration = 0; iteration < max_iterations; ++iteration ) {
            float start_energy = snake.getScalarEnergy();
            long time = System.currentTimeMillis();
            for( int i = 0; i < snake.size; ++i ) {                
                Vector initial = snake.getPosition(i);
                Vector best_pos = new Vector(initial);
                float best_energy = snake.getScalarEnergy();
                for( float x = -limit; x <= limit; ++x ) {
                    for( float y = -limit; y <= limit; ++y ) {
                        Vector pos = initial.add(x, y);
                        float energy = snake.getScalarEnergyIf(i, pos);
                        if( energy < best_energy ) {
                            best_pos = pos;
                            best_energy = energy;
                        }
                    }
                }
                snake.setPosition(i, best_pos);
            }
            applet.redraw();
            System.out.println("Square search iteration "+iteration+" with dEnergy of "+(start_energy-snake.getScalarEnergy())+" finished after "+(System.currentTimeMillis()-time)+ "ms");
        }
    }
}
