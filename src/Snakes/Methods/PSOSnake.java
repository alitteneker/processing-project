package Snakes.Methods;

import java.util.ArrayList;

import processing.core.PApplet;
import Snakes.Snake;

public class PSOSnake extends SnakeMethod {
    int p_size;
    float p_i, p_g;
    public PSOSnake(Snake sn, int p_size, float p_i, float p_g) {
        snake = sn;
        this.p_size = p_size;
        this.p_i = p_i;
        this.p_g = p_g;
    }
    public void runMethod(PApplet applet) {
        long start = System.currentTimeMillis();
        
        int max_iterations = 10000;
        applet.redraw();
        ArrayList<ParticleSwarm> swarms = new ArrayList<ParticleSwarm>();
        for( int iteration = 0; iteration < max_iterations; ++iteration ) {
            long time = System.currentTimeMillis();
            float start_energy = snake.getScalarEnergy();
            
            for( int j = 0; j < snake.size; ++j )
                swarms.add( new ParticleSwarm(p_size, snake, j, p_i, p_g) );
            for( int j = 0; j < snake.size; ++j )
                snake.setPosition(j, swarms.get(j).run());

            snake.updateAllEnergy();
            snake.updateAllForces();

            System.out.println("PSO iteration "+iteration+" finished with d_energy "+(start_energy-snake.getScalarEnergy())+" after " + ( System.currentTimeMillis() - time ) + "ms");

            swarms.clear();
            applet.redraw();
        }
        System.out.println("Finished after " + ( ( System.currentTimeMillis() - start ) /1000f ) + " seconds");
    }
}
