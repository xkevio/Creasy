package ovgu.creasy.geom;

import javafx.geometry.Point2D;

import java.util.Objects;

/**
 * 2d Point with double precision floating point coordinates
 */
public class Point {
    private double x;
    private double y;

    // not a great solution as it violates the single-responsibility principle (SRP)
    private boolean highlighted;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.highlighted = false;
    }

    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
        this.highlighted = other.isHighlighted();
    }

    public static Point fromPoint2D(Point2D point2D) {
        return new Point(point2D.getX(), point2D.getY());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Point add(Point other) {
        return new Point(this.x + other.getX(), this.y + other.getY());
    }

    public Point multiply(Point other) {
        return new Point(this.x * other.getX(), this.y * other.getY());
    }

    public Point divide(Point other) {
        return new Point(this.x / other.getX(), this.y / other.getY());
    }

    public double distance(Point other) {
        return Math.sqrt(((other.x-x) * (other.x-x)) + ((other.y-y) * (other.y-y)));
    }

    /**
     * @param other Point to which the Angle is measured
     * @return clockwise angle of the line from this point to other, to a Line parallel to the y axis through this point
     */
    public double clockwiseAngle(Point other) {
        double dirAnchorYLine = Math.atan2(0, 1);
        double dirThisLine = Math.atan2(other.getY() - this.getY(), other.getX() - this.getX());
        double angle = -(dirAnchorYLine - dirThisLine);
        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        } else if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    public double dot(Point other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public String toString() {
        return """
               Point
               {
                   x: %f,
                   y: %f
               }
               """.formatted(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
