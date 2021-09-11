package ovgu.creasy.geom;

/**
 * Type that stores position and vertex type
 */
public class Vertex {
    private Point point;
    private Point positionAfterFolding;
    private Type type;

    public enum Type {
        INTERNAL,

        BORDER,

        VIRTUAL
    }

    /**
     * @param positionBeforeFolding is a point in the x-y plane representing the position of the vertex after the model is folded
     * @param type  is the type of the vertex
     */
    public Vertex(Point positionBeforeFolding, Point position, Type type) {
        this.point = positionBeforeFolding;
        this.positionAfterFolding = position;
        this.type = type;
    }

    public Point getPoint() {
        return point;
    }

    public Type getType() {
        return type;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Point getPositionAfterFolding() {
        return positionAfterFolding;
    }

    @Override
    public String toString() {
        return """
               Vertex
               {
                   %s,
                   type: %s
               }
               """.formatted(point, type);
    }


}
