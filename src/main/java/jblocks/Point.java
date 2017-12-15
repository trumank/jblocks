package jblocks;

import org.jsfml.system.Vector2f;

public class Point {
    public double x;
    public double y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f toVector() {
        return new Vector2f((float) x, (float) y);
    }
}
