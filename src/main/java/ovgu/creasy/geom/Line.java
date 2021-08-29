package ovgu.creasy.geom;

import java.util.Objects;

/**
 * 2d Line segment
 */
public class Line {
    private Point start;
    private Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    /**
     *  Returns a Point between the start and end point of the line
     *  at factor t
     * @param t a factor between 0 and 1 [0,1]
     * @return the Point at factor t between start and end of the line
     */
    public Point getPointAt(double t) {
        if (t < 0 || t > 1) throw new ArithmeticException("t should be between 0 and 1!");

        return new Point(start.getX() + t * (end.getX() - start.getX()),
                         start.getY() + t * (end.getY() - start.getY()));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(start, line.start) && Objects.equals(end, line.end);
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
}
