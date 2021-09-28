package edu.etf.bg.ac.rs.idealgaslawsimulation.geometry;

public class Simulation2D {
    static {
        System.loadLibrary("idealgaslawsimulation");
    }
    private long nativeHandle;

    public Simulation2D(double kB, double T, double hfw, ParticleConfig pc1, ParticleConfig pc2, double rate, long sim_step, long sim_count, int N_offset, int N_real, int row, int col) {
        init(kB, T, hfw, pc1, pc2, rate, sim_step, sim_count, N_offset, N_real, row, col);
    }

    private native void init(double kB, double T, double hfw, ParticleConfig pc1, ParticleConfig pc2, double rate, long sim_step, long sim_count, int N_offset, int N_real, int row, int col);

    public native void setIOnSimulationListener(IOnSimulationListener listener);

    public native void run() throws InterruptedException;

    public native void destroy();
}
