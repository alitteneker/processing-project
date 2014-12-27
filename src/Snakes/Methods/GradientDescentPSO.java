package Snakes.Methods;

import processing.core.PApplet;
import Snakes.Snake;
import TestSketch.Math.MathTools;
import TestSketch.Math.ParticleSwarm;
import TestSketch.Math.Vector;

public class GradientDescentPSO extends SnakeMethod {
    
    public float gamma_seed, phi_individual, phi_global;
    public int size;

    public GradientDescentPSO(float gamma_seed, int size, float phi_individual, float phi_global, Snake sn) {
        super(sn);
        this.size = size;
        this.gamma_seed = gamma_seed;
        this.phi_individual = phi_individual;
        this.phi_global = phi_global;
    }

    public void runMethod(PApplet applet) {
        boolean silent = true;
        GammaParticleSwarm swarm = new GammaParticleSwarm(gamma_seed, snake.size, size, phi_individual, phi_global);
        final int max_iterations = 10000;
        for( int iteration = 0; iteration < max_iterations; ++iteration ) {
            long start = System.currentTimeMillis();
            float last_energy = snake.getScalarEnergy();
            
            Vector gammas = swarm.run();
            snake.followGradientDescent(gammas);
            applet.redraw();
            
            float next_energy = snake.getScalarEnergy();
            
            if( !silent )
            System.out.println("Gamma PSO iteration "+iteration+" took "+(System.currentTimeMillis()-start)+" ms with dEnergy "+(last_energy-next_energy));

            if( MathTools.abs( last_energy - next_energy ) < 0.00001f )
                break;
        }
    }
    
    public class GammaParticleSwarm extends ParticleSwarm {
        int snake_size;
        Vector memory;

        public GammaParticleSwarm(float seed_gamma, int s_s, int p_s, float p_i, float p_g) {
            super(p_s, p_i, p_g);
            snake_size = s_s;
            memory = new Vector(s_s);
            stabilization_threshold = 100;
            for( int i = 0; i < s_s; ++i )
                memory.setComponent(seed_gamma, i);
        }

        public void initialize() {
            g_best_pos = new Vector(memory);
        }

        public Particle buildParticle(int index) {
            Vector vel = new Vector(snake_size);
            for( int i = 0; i < snake_size; ++i )
                vel.setComponent(memory.getComponent(i)*(float)gen.nextGaussian(), i);
            return new Particle(memory, vel, snake.getScalarEnergy(), this);
        }

        public boolean insideRange(Vector check) {
            for( int i = 0; i < snake_size; ++i )
                if( check.getComponent(i) < 0 )
                    return false;
            return true;
        }

        public float evaluateFitness(Vector pos) {
            return snake.calcScalarEnergyIfDescendedBy(pos);
        }
        
    }

}
