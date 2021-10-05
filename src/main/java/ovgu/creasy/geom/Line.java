package ovgu.creasy.geom;

import java.util.*;

/**
 * 2d Line segment
 */
public class Line {
    private Point start;
    private Point end;

    private final List<Point> intersections;

    public Line() {
        this(new Point(0, 0), new Point(0, 0));
    }

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
        intersections = new ArrayList<>();
    }

    /**
     *  Returns a Point between the start and end point of the line
     *  at factor t
     * @param t a factor between 0 and 1 [0,1]
     * @return the Point at factor t between start and end of the line
     */
    public Point getPointAt(double t) {
        if (t < 0 || t > 1) throw new ArithmeticException("t should be between 0 and 1!");

        return new Point(start.getX() + t * getDir().getX(),
                         start.getY() + t * getDir().getY());
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public double getSlope() {
        return (getEnd().getX() - getStart().getX())/(getEnd().getY() - getStart().getY());
    }

    public Point getOppositePoint(Point startOrEnd) {
        if (startOrEnd.equals(this.start)) {
            return this.end;
        } else if (startOrEnd.equals(this.end)) {
            return this.start;
        }
        return null;
    }

    public boolean contains(Point p) {
        return contains(p, 0.00001);
    }

    public boolean contains(Point p, double eps) {
        return Math.abs(start.distance(p) + end.distance(p) - start.distance(end)) < eps;
    }

    public Point getDir() {
        return new Point(end.getX() - start.getX(), end.getY() - start.getY());
    }

    public double getClockwiseAngle() {
        return getStart().clockwiseAngle(getEnd());
    }

    public Optional<Point> intersection(Line other) {
        Point dirA = this.getDir();
        Point dirB = other.getDir();
        Point p = new Point(-dirA.getY(), dirA.getX());
        double h = (new Point(start.getX()-other.start.getX(), start.getY() - other.start.getY()).dot(p))/dirB.dot(p);
        if (h > -0.000001 && h < 1.00001){
            Point intersection;
            if (Math.abs(h) < 0.000001) {
                intersection = other.start;
            } else if (Math.abs(h-1) < 0.000001) {
                intersection = other.end;
            } else {
                intersection = other.getPointAt(h);
            }
            if (this.contains(intersection)) {
                return Optional.of(intersection);
            }
        }
        return Optional.empty();
    }

    public void addSplicePoints(Point splice) {
        intersections.add(splice);
    }

    public int getIntersectionSize() {
        return intersections.size();
    }

    // only call once per Line
    public List<Line> splicedLines() {
        if (intersections.size() < 1) {
            return List.of(this);
        } else {
            List<Line> lines = new ArrayList<>();
            Line copy = new Line(new Point(getStart()), new Point(getEnd()));


            // i might be a genius
            intersections.sort(Comparator.comparingDouble(p -> copy.getStart().distance(p)));

            intersections.forEach(point -> {
                Line line = new Line();
                line.setStart(copy.getStart());
                line.setEnd(point);

                lines.add(line);
                copy.setStart(point);
            });
            lines.add(copy);
            intersections.clear();
            return lines;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(start, line.start) && Objects.equals(end, line.end);
    }

    public boolean equalsOrReversed(Line other) {
        return this.equals(other) || this.equals(other.reversed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return """
               Line
               {
                   start: %s,
                   end: %s
               }
               """.formatted(start.toString().indent(4),
                             end.toString().indent(4));
    }

    public Line reversed() {
        return new Line(this.end, this.start);
    }
}
