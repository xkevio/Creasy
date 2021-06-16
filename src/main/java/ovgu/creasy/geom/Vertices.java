package ovgu.creasy.geom;

import ovgu.creasy.origami.*;
import ovgu.creasy.geom.*;

import java.util.Objects;

public class Vertices {
    private final Point point;
    private final Type type;

    public enum Type {
        INTERNAL,

        BORDER,

        VIRTUAL;
    }

    /**
     * @param point is a point in the x-y plane representing the position of the vertex after the model is folded
     * @param type  is the type of the vertex
     */
    public Vertices(Point point, Type type) {
        this.point = point;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "Point=" + point.getX() + point.getY() +
                ", type=" + type +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(point.getX(), point.getY());
    }
}
