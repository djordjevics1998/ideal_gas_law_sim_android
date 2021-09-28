package edu.etf.bg.ac.rs.idealgaslawsimulation.geometry;

public abstract class PhObject {
    public enum TYPE {LINE_2D, PARTICLE_2D, TRIANGLE, PARTICLE_3D};

    public abstract TYPE getType();
}
