package ovgu.creasy.origami;

import javafx.util.Pair;
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
    private final Set<Pair<Crease, Crease>> creasePairs;

    public ReflectionGraph(CreasePattern cp) {
        this.cp = cp;
        this.creases = new HashSet<>();
        this.creasePairs = new HashSet<>();
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

    public void addCreasePair(Pair<Crease, Crease> pair) {
        this.creasePairs.add(pair);
        this.creasePairs.add(new Pair<>(pair.getKey(), pair.getValue()));
    }

    public boolean isLeafNode(Point p) {
        List<Crease> adjacentCreases = cp.getAdjacentCreases(p);
        long adjacentLinesInReflectionGraph = adjacentCreases.stream().filter(getCreases()::contains).count();
        return adjacentLinesInReflectionGraph == 1;
    }

    public void addAllCreases(Collection<Crease> creases, Crease originateFrom) {
        for (Crease crease : creases) {
            addCrease(crease);
            addCreasePair(new Pair<>(originateFrom, crease));
        }
    }

    public CreasePattern getCp() {
        return cp;
    }

    public List<Crease> getAdjacentCreases(Point p) {
        return cp.getAdjacentCreases(p).stream().filter(creases::contains).collect(Collectors.toList());
    }

    /**
     * @return A Collection of all locally maximal Reflection paths in the reflectionGraph (see page 29 for explanation of local maxima)
     */
    public Collection<ReflectionPath> getLocalMaxima() {
        Set<Point> leafNodes = getLeafNodes();
        Set<ReflectionPath> reflectionPaths = new HashSet<>();
        for (Point leafNode : leafNodes) {
            // because the point is a leaf node, there is only one adjacent crease in the reflectionGraph
            Crease startingCrease = getAdjacentCreases(leafNode).get(0);
            List<ReflectionPathBuilder> pathBuilders = new ArrayList<>();

            pathBuilders.add(new ReflectionPathBuilder(startingCrease, leafNode));
            while (pathBuilders.stream().anyMatch(path -> !path.isDone())) {
                for (ReflectionPathBuilder pathBuilder : pathBuilders) {
                    if (pathBuilder.isDone()){
                        continue;
                    }
                    Crease lastCrease = pathBuilder.getLastCrease();
                    pathBuilder.setCurrentPoint(lastCrease.getLine().getOppositePoint(pathBuilder.getCurrentPoint()));
                    List<Crease> nextCreases = getAdjacentCreases(pathBuilder.getCurrentPoint()).stream()
                            .filter(crease -> !pathBuilder.getCreases().contains(crease))
                            .filter(crease -> creasePairs.contains(new Pair<>(crease, lastCrease)))
                            .collect(Collectors.toList());
                    if (nextCreases.size() == 0) {
                        pathBuilder.setDone(true);
                        continue;
                    }
                    ReflectionPathBuilder tmpPath = pathBuilder.copy();
                    pathBuilder.addCrease(nextCreases.get(0));
                    for (int j = 1; j < nextCreases.size(); j++) {
                        ReflectionPathBuilder newPath = tmpPath.copy();
                        newPath.addCrease(nextCreases.get(j));
                    }
                }
            }
            reflectionPaths.addAll(pathBuilders.stream().map(ReflectionPathBuilder::build).collect(Collectors.toList()));
        }

        return reflectionPaths;
    }
}
