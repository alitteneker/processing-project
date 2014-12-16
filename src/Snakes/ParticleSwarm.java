package Snakes;

import java.util.ArrayList;
import java.util.Random;

import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;

public class ParticleSwarm {
    public ArrayList<Particle> particles = new ArrayList<Particle>();
    Vector initial, g_best_pos, neighbor_left, neighbor_right;
    int index;
    float phi_individual, phi_global, clip_threshold = 2, g_best;
    Random gen = new Random();
    Snake snake;
    
    public ParticleSwarm(int size, Snake sn, int ind, float p_i, float p_g) {
        snake = sn;
        index = ind;
        initial = snake.getPosition(index);
        neighbor_left  = snake.getPosition(index - 1);
        neighbor_right = snake.getPosition(index + 1);
        phi_individual = p_i;
        phi_global = p_g;
        
        g_best_pos = initial;
        g_best = snake.getScalarEnergy();
        
        float initial_velocity_range = 2;
        for( int i = 0; i < size; ++i )
            particles.add( new Particle( initial, MathTools.randomVector(initial_velocity_range), g_best, this ) );
    }
    public boolean insideRange(Vector check) {
        float d1 = MathTools.distance(neighbor_left, check),
              d2 = MathTools.distance(neighbor_right, check);
        if( MathTools.abs(d1 - d2) > clip_threshold )
            return false;
        return true;
    }
    public float evaluateFitness(Vector pos) {
        return snake.getScalarEnergyIf(index, pos);
    }
    public Vector run() {
        int time_since_change = 0;
        int size = particles.size();
        while( time_since_change < 100 ) {
            ++time_since_change;
            for( int i = 0; i < size; ++i ) {
                Particle a = particles.get(i);
                if( a.move() < g_best ) {
                    g_best_pos = a.p_best_pos;
                    g_best = a.p_best;
                    time_since_change = 0;
                }
            }
//            System.out.println("Move iteration "+time_since_change);
//            for( int i = 0; i < size; ++i )
//                System.out.println("\tMoved " + i + "th particle " + MathTools.distance(initial, particles.get(i).pos));
        }
        System.out.println( "Moved " + index + "th control point " + MathTools.distance(initial, g_best_pos) + " pixels" );
        return g_best_pos;
    }
    
    public class Particle {
        Vector pos, vel, p_best_pos;
        float fitness, p_best;
        ParticleSwarm parent;
        
        public Particle(Vector s_p, Vector s_v, float start_fit, ParticleSwarm par) {
            pos = new Vector(s_p);
            vel = s_v;
            fitness = start_fit;
            p_best = start_fit;
            p_best_pos = new Vector(s_p);
            parent = par;
        }
        public float move() {
            Vector accel = getRandAccel();
            vel.addEquals( accel );
            Vector newpos = pos.add( vel );
            if( parent.insideRange(newpos) ) {
                pos = newpos;
                fitness = parent.evaluateFitness(pos);
            }
            if( fitness < p_best ) {
                p_best = fitness;
                p_best_pos = pos;
            }
            return fitness;
        }
        public Vector getRandAccel() {
            Vector ret = new Vector();
            ret.addEquals( gen.nextFloat() * phi_individual, p_best_pos.add(-1, pos) );
            ret.addEquals( gen.nextFloat() * phi_global,     g_best_pos.add(-1, pos) );
            return ret;
        }
    }
}
