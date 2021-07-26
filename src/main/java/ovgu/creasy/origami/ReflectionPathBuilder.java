package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReflectionPathBuilder {
    private ArrayList<Crease> creases;
    private Point currentPoint;
    private Point startingPoint;
    private boolean done;

    public boolean isDone() {
        return done;
    }

    public List<Crease> getCreases() {
        return Collections.unmodifiableList(creases);
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public ReflectionPathBuilder(Crease crease, Point startingPoint) {
        this.creases = new ArrayList<>();
        this.creases.add(crease);
        currentPoint = startingPoint;
        this.startingPoint = startingPoint;
    }

    private ReflectionPathBuilder(List<Crease> creases, Point currentPoint) {
        this.creases = new ArrayList<>(creases);
        this.currentPoint = currentPoint;
    }

    public int size() {
        return creases.size();
    }

    public void addCrease(Crease crease) {
        creases.add(crease);
    }

    public ReflectionPathBuilder copy() {
        return new ReflectionPathBuilder(creases, currentPoint);
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }

    public Crease getLastCrease() {
        return creases.get(creases.size()-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectionPathBuilder that = (ReflectionPathBuilder) o;
        return Objects.equals(creases, that.creases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creases);
    }

    public ReflectionPath build() {
        return new ReflectionPath(creases, startingPoint, currentPoint);
    }
}
