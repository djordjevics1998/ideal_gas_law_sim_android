package edu.etf.bg.ac.rs.idealgaslawsimulation.geometry;

public class Line2D extends PhObject {
    protected Point2D p1, p2;

    public Line2D(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public TYPE getType() {
        return TYPE.LINE_2D;
    }

    public Point2D getP1() {
        return p1;
    }

    public Point2D getP2() {
        return p2;
    }

    @Override
    public String toString() {
        return "Line2D{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                '}';
    }
}
