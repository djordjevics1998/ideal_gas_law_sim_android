package edu.etf.bg.ac.rs.idealgaslawsimulation.geometry;

public class Point2D {
    protected double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D point2D) {
        this.x = point2D.x;
        this.y = point2D.y;
    }

    public Point2D(Point2D point2D, double offsetX, double offsetY) {
        this(point2D);
        this.x += offsetX;
        this.y += offsetY;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
