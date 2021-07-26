package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;

import java.util.*;

public class ReflectionPath {
    private List<Crease> creases;
    private List<Crease> reversedCreases;
    private Set<Point> points;
    private Point startingPoint;
    private Point endPoint;

    public ReflectionPath(ArrayList<Crease> creases, Point startingPoint, Point endPoint) {
        this.creases = creases;
        this.reversedCreases = new ArrayList<>(creases);
        Collections.reverse(this.reversedCreases);
        this.startingPoint = startingPoint;
        this.endPoint = endPoint;
        points = new HashSet<>();
        creases.forEach(c -> {
            points.add(c.getLine().getStart());
            points.add(c.getLine().getEnd());
        });
        points = Collections.unmodifiableSet(points);
    }

    public Set<Point> getPoints() {
        return points;
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public List<Crease> getCreases() {
        return Collections.unmodifiableList(creases);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectionPath that = (ReflectionPath) o;
        if (creases.size() != that.creases.size()) {
            return false;
        }
        return Objects.equals(creases, that.creases) || Objects.equals(creases, that.reversedCreases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creases) + Objects.hash(reversedCreases);
    }

    @Override
    public String toString() {
        return "ReflectionPath{" +
                "creases=" + creases +
                '}';
    }

    public int length() {
        return creases.size();
    }
}
