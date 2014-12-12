package Snakes;

import java.util.ArrayList;
import java.util.Random;

import TestSketch.Math.MathTools;
import TestSketch.Math.Vector;

public class ParticleSwarm {
    public ArrayList<Particle> particles = new ArrayList<Particle>();
    Vector initial, g_best_pos, neighbor_left, neighbor_right;
    float phi_individual, phi_global, clip_threshold = 2, g_best = Float.POSITIVE_INFINITY;
    Random gen = new Random();
    Snake snake;
    
    public ParticleSwarm(int size, Snake sn, int index, float p_i, float p_g) {
        snake = sn;
        initial = snake.getPosition(index);
        neighbor_left  = snake.getPosition(index - 1);
        neighbor_right = snake.getPosition(index + 1);
        phi_individual = p_i;
        phi_global = p_g;
        g_best_pos = new Vector();
        
        float initial_velocity_range = 1;
        for( int i = 0; i < size; ++i )
            particles.add( new Particle( initial, MathTools.randomVector(initial_velocity_range) ) );
    }
    public boolean insideRange(Vector check) {
        float d1 = MathTools.distance(neighbor_left, check),
              d2 = MathTools.distance(neighbor_right, check);
        if( MathTools.abs(d1 - d2) > clip_threshold )
            return false;
        return true;
    }
    public float evaluateFitness(Vector pos) {
        // TODO: actually calculate
        return 0;
    }
    public Vector run() {
        int time_since_change = 0;
        int size = particles.size();
        while( time_since_change < 10 ) {
            ++time_since_change;
            for( int i=0; i < size;++i) {
                Particle a = particles.get(i);
                if( a.move() < g_best ) {
                    g_best_pos = a.p_best_pos;
                    g_best = a.p_best;
                    time_since_change = 0;
                }
            }
        }
        return g_best_pos;
    }
    
    public class Particle {
        Vector pos, vel, p_best_pos;
        float fitness, p_best;
        ParticleSwarm parent;
        
        public Particle(Vector s_p, Vector s_v) {
            pos = s_p;
            vel = s_v;
            p_best = Float.POSITIVE_INFINITY;
            p_best_pos = new Vector();
        }
        public float move() {
            vel.addEquals( getRandAccel() );
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
