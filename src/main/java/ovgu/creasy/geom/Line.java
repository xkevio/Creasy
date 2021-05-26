package ovgu.creasy.geom;

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

        Point at = new Point(0, 0);
        at.setX(start.getX() + t * end.getX());
        at.setY(start.getY() + t * end.getY());

        return at;
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
}
