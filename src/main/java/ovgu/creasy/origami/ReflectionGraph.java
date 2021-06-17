package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a group of creases that are connected through reflection creases. This class
 * does not do any Point merging, so only input Creases that have already been processed by a class
 * that does Point merging, like CreasePattern
 */
public class ReflectionGraph {
    private final CreasePattern cp;

    private final Set<Crease> creases;
    private final Set<Point> points;

    public ReflectionGraph(CreasePattern cp) {
        this.cp = cp;
        this.creases = new HashSet<>();
        this.points = new HashSet<>();
    }

    public Set<Crease> getCreases() {
        return Collections.unmodifiableSet(creases);
    }

    public Set<Point> getPoints() {
        return Collections.unmodifiableSet(points);
    }

    public Set<Point> getLeafNodes() {
        return getPoints().stream().filter(this::isLeafNode)
                .collect(Collectors.toSet());
    }

    public void addCrease(Crease crease) {
        this.creases.add(crease);
        points.add(crease.getLine().getEnd());
        points.add(crease.getLine().getStart());
    }

    public boolean isLeafNode(Point p) {
        List<Crease> adjacentCreases = cp.getAdjacentCreases(p);
        long adjacentLinesInReflectionGraph = adjacentCreases.stream().filter(getCreases()::contains).count();
        return adjacentLinesInReflectionGraph == 1;
    }

    public void addAllCreases(Collection<Crease> creases) {
        for (Crease crease : creases) {
            addCrease(crease);
        }
    }

    public CreasePattern getCp() {
        return cp;
    }

    public List<Crease> getAdjacentCreases(Point p) {
        return cp.getAdjacentCreases(p).stream().filter(creases::contains).collect(Collectors.toList());
    }
}
