package TestSketch.Math;

import java.util.ArrayList;
import java.util.Random;

public abstract class ParticleSwarm {
    public ArrayList<Particle> particles = new ArrayList<Particle>();
    public Vector g_best_pos;
    public float phi_individual, phi_global, g_best;
    public Random gen = new Random();
    public boolean disable_clipping = true;
    public int size;
    
    public ParticleSwarm(int s, float p_i, float p_g) {
        size = s;
        phi_individual = p_i;
        phi_global = p_g;
    }
    
    public abstract void initialize();
    public abstract Particle buildParticle(int index);
    public abstract boolean insideRange(Vector check);
    public abstract float evaluateFitness(Vector pos);

    public Vector run() {

        g_best_pos = new Vector();
        g_best = Float.POSITIVE_INFINITY;

        initialize();

        particles.clear();
        for( int i = 0; i < size; ++i )
            particles.add( buildParticle(i) );

        int time_since_change = 0;
        int size = particles.size();
        while( time_since_change < 10 ) {
            ++time_since_change;
            for( int i = 0; i < size; ++i ) {
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
        
        public Particle(Vector s_p, Vector s_v, float start_fit, ParticleSwarm par) {
            pos = new Vector(s_p);
            vel = new Vector(s_v);
            p_best_pos = new Vector(s_p);
            p_best = start_fit;
            fitness = start_fit;
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
                p_best_pos = new Vector(pos);
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
