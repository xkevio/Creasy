package ovgu.creasy.origami;

import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Vertex;

import java.util.Collections;
import java.util.Objects;

/**
 * A single extended crease in an Extended Crease Pattern
 * Each extended crease represents the local maximum reflection that starts in 'start' and ends in 'end'
 * ('start' & 'end' must be a part of extended vertices)
 */
public class ExtendedCrease {
    private Vertex start;
    private Vertex end;
    private final Crease.Type type;
    private boolean active;
    private ExtendedReflectionPath extendedReflectionPath;

    /**
     * @param start  is the start point for a reflection path
     * @param end    is the end point for a reflection path
     * @param type   is the crease assignment of the first crease in the reflection path
     * @param active is a boolean indication whether the extended crease is active or not
     */
    public ExtendedCrease(Vertex start, Vertex end, Crease.Type type, boolean active) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.active = active;
        this.extendedReflectionPath = new ExtendedReflectionPath(start, end, Collections.singletonList(new Crease(new Line(start.getPoint(), end.getPoint()), type)));
    }

    public void setExtendedReflectionPath(ExtendedReflectionPath extendedReflectionPath) {
        this.extendedReflectionPath = extendedReflectionPath;
    }

    public ExtendedReflectionPath getExtendedReflectionPath() {
        return extendedReflectionPath;
    }

    public boolean isComplete() {
        return getExtendedReflectionPath().getStart().getType() == Vertex.Type.BORDER && getExtendedReflectionPath().getEnd().getType() == Vertex.Type.BORDER;
    }

    public Vertex getStartVertex() {
        return start;
    }

    public Vertex getEndVertex() {
        return end;
    }

    public Crease.Type getType() { return type; }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) { this.active = true; }

    public void setStartVertex(Vertex vertex){ this.start = vertex; }

    public void setEndVertex(Vertex vertex){ this.end = vertex; }

    public Line asLine() {
        return new Line(start.getPoint(), end.getPoint());
    }

    public double getClockwiseAngle() {
        return getStartVertex().getPoint().clockwiseAngle(getEndVertex().getPoint());
    }

    @Override
    public String toString() {
        return "ExtendedCrease{" +
                "StartVertex=" + start +
                "EndVertex=" + end +
                "type=" + type +
                ", active?" + active +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, type, active);
    }
}
