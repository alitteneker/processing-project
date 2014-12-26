package Snakes.Methods;

import java.util.ArrayList;

import processing.core.PApplet;
import Snakes.Snake;
import TestSketch.Math.MathTools;
import TestSketch.Math.ParticleSwarm;
import TestSketch.Math.Vector;

public class PointPSO extends SnakeMethod {
    int p_size;
    float p_i, p_g;
    public PointPSO(Snake sn, int p_size, float p_i, float p_g) {
        super(sn);
        this.p_size = p_size;
        this.p_i = p_i;
        this.p_g = p_g;
    }
    public void runMethod(PApplet applet) {
        long start = System.currentTimeMillis();

        int max_iterations = 10000;
        applet.redraw();
        
        ArrayList<ParticleSwarm> swarms = new ArrayList<ParticleSwarm>();
        swarms.clear();
        for( int j = 0; j < snake.size; ++j )
            swarms.add( new BasicSnakePSO(p_size, snake, j, p_i, p_g) );
        
        for( int iteration = 0; iteration < max_iterations; ++iteration ) {
            long time = System.currentTimeMillis();
            float start_energy = snake.getScalarEnergy();

            for( int j = 0; j < snake.size; ++j ) {
                ParticleSwarm curr = swarms.get(j);
                Vector new_pos = curr.run();
                snake.setPosition(j, new_pos);
            }

            snake.updateAllEnergy();
            snake.updateAllForces();

            float energy_diff = start_energy - snake.getScalarEnergy();
            System.out.println("PSO iteration "+iteration+" finished with d_energy "+energy_diff+" after " + ( System.currentTimeMillis() - time ) + "ms");
            if( energy_diff < 0.00001f )
                break;

            applet.redraw();
        }
        System.out.println("Finished after " + ( ( System.currentTimeMillis() - start ) /1000f ) + " seconds");
    }
    class BasicSnakePSO extends ParticleSwarm {
        public int index;
        public Vector initial, neighbor_left, neighbor_right;
        public float grad_angle;
        public float clip_threshold = 5;
        public Snake snake;

        public BasicSnakePSO(int size, Snake sn, int ind, float p_i, float p_g) {
            super(size, p_i, p_g);

            snake = sn;
            index = ind;
        }
        
        public void initialize() {
            initial = snake.getPosition(index);
            neighbor_left  = snake.getPosition(index - 1);
            neighbor_right = snake.getPosition(index + 1);
            
            Vector grad = snake.getDeltaEnergy(index).multiplyEquals(-1);
            grad_angle = MathTools.atan2(grad);

            g_best_pos = new Vector(initial);
            g_best = snake.getScalarEnergy();
        }

        public boolean insideRange(Vector check) {
            if( disable_clipping )
                return true;
            float d1 = MathTools.distance(neighbor_left, check),
                  d2 = MathTools.distance(neighbor_right, check);
            if( MathTools.abs(d1 - d2) > clip_threshold )
                return false;
            return true;
        }

        public float evaluateFitness(Vector pos) {
            return snake.getScalarEnergyIf(index, pos);
        }

        public Particle buildParticle(int index) {
            final float initial_velocity = 1f,
                        angle_range = 0.1f;
            return new Particle( initial,
                    MathTools.polarToCartesianVector( initial_velocity, grad_angle + ( ((float)gen.nextGaussian()) * angle_range ) ),
                    g_best, this );
        }

    }
}
