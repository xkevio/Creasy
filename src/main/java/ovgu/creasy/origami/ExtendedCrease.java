package ovgu.creasy.origami;

import ovgu.creasy.geom.*;

import java.util.Objects;

/**
 * A single extended crease in a Extended Crease Pattern
 * Each extended crease represents the local maximum reflection that starts in 'start' and ends in 'end'
 * ('start' & 'end' must be a part of extended vertices.
 */
public class ExtendedCrease {
    private final Vertices start;
    private final Vertices end;
    private final Type type;
    private final Boolean active;

    public enum Type {
        MOUNTAIN,

        VALLEY,

        DONTCARE;
    }

    /**
     * @param start  is the start point for a reflection path
     * @param end    is the end point for a reflection path
     * @param type   is the crease assignment of the first crease in the reflection path
     * @param active is boolean indication whether the extended crease is active or not
     */
    public ExtendedCrease(Vertices start, Vertices end, Type type, Boolean active) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.active = active;
    }

    public Vertices getStartVertices() {
        return start;
    }

    public Vertices getEndVertices() {
        return end;
    }

    public Type getType() { return type; }

    public Boolean getActive() {
        return active;
    }

    @Override
    public String toString() {
        return "ExtendedCrease{" +
                "StartVertice=" + start +
                "EndVertice=" + end +
                "type=" + type +
                ", active?" + active +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, type, active);
    }
}
