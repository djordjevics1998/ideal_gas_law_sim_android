package edu.etf.bg.ac.rs.idealgaslawsimulation.geometry;

public class Particle2D extends PhObject {
    protected int id;
    protected double r, m, vx, vy;
    protected Point2D c;

    public Particle2D(int id, Point2D c, double r, double m, double vx, double vy) {
        this.id = id;
        this.c = c;
        this.r = r;
        this.m = m;
        this.vx = vx;
        this.vy = vy;
    }

    public int getId() { return id; }

    public double getRadius() { return r; }

    public double getMass() { return m; }

    public Point2D getCenter() { return c; }

    @Override
    public TYPE getType() {
        return TYPE.PARTICLE_2D;
    }

    @Override
    public String toString() {
        return "Particle2D{" +
                "id=" + id +
                ", r=" + r +
                ", m=" + m +
                ", vx=" + vx +
                ", vy=" + vy +
                ", c=" + c +
                '}';
    }
}
