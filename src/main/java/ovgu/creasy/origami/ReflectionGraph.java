package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Explained in chapter 3.1 of the paper (pages 18 - 20)
 */
public class ReflectionGraph {
    private static final double EPS = 0.0000001;
    private final Map<Crease, Collection<Crease>> reflectionCreases;
    private CreasePattern cp;

    public ReflectionGraph(CreasePattern cp) {
        this.cp = cp;
        this.reflectionCreases = new HashMap<>();
        for (Point point : cp.getPoints()) {
            findReflectionCreases(cp.getAdjacentCreases(point), point);
        }
        System.out.println(reflectionCreases);
    }

    /**
     * Reflection creases are explained in chapter 3.1.1
     * <p>
     * Finds all reflection crease pairs in adjacentCreases around commonPoint and
     * puts them into reflectionCreases
     * <p>
     * adjacentCreases is assumed to be sorted by angle
     */
    private void findReflectionCreases(List<Crease> adjacentCreases, Point commonPoint) {
        for (int i = 0; i < adjacentCreases.size(); i++) {
            if (adjacentCreases.get(i).getType() == Crease.Type.EDGE) {
                continue;
            }
            double alternatingAngle = 0;
            int j = (i + 1) % adjacentCreases.size();
            // iterate over all creases but adjacentCreases[i], starting at i+1 and wrapping
            // around to the beginning when the end of the array is reached
            while (j != i) {
                double angle = getAngle(
                        getPrevious(j, adjacentCreases),
                        adjacentCreases.get(j),
                        commonPoint);
                if (j % 2 == 0) {
                    alternatingAngle += angle;
                } else {
                    alternatingAngle -= angle;
                }
                System.out.println(alternatingAngle);
                // see 3.1.1 for definition of reflection creases
                if (Math.abs(alternatingAngle) <= EPS
                        && adjacentCreases.get(i).getType() == adjacentCreases.get(j).getType().opposite()) {
                    addReflectionCrease(adjacentCreases.get(i), adjacentCreases.get(j));
                }
                j++;
                j %= adjacentCreases.size();
            }
        }
    }

    /**
     *
     * @return A collection of all connected subgraphs of the Reflection graph
     */
    public Collection<Collection<Crease>> getAllSubgraphs() {
        Collection<Collection<Crease>> subGraphs = new HashSet<>();
        Set<Crease> done = new HashSet<>();

        for (Crease crease : cp.getCreases()) {
            if (crease.getType()== Crease.Type.EDGE || done.contains(crease)) {
                continue;
            }
            Set<Crease> connected = new HashSet<>();
            connected.add(crease);
            boolean allDone = false;
            while (!allDone) {
                allDone = true;
                for (Crease connectedCrease : connected) {
                    if (done.contains(connectedCrease)) {
                        continue;
                    }
                    allDone = false;
                    done.add(connectedCrease);
                    connected.addAll(getReflectionCreases(connectedCrease));
                }
            }
            subGraphs.add(connected);
        }
        return subGraphs;
    }

    /**
     *
     * @param subgraph a connected subgraph, can be obtained using getAllSubgraphs
     * @return A Collection of all locally maximal Reflection paths in the subgraph (see page 29 for explanation of local maxima)
     */
    public Collection<ReflectionPath> getLocalMaxima(Collection<Crease> subgraph) {
        List<Point> leafNodes = subgraph.stream()
                .map(crease -> Arrays.asList(crease.getLine().getEnd(), crease.getLine().getStart()))
                .flatMap(Collection::stream)
                .filter(point -> isLeafNode(point, subgraph))
                .collect(Collectors.toList());
        Point start = leafNodes.get(0);
        // because start is a leaf node, there is only one adjacent crease in the subgraph
        Crease startingCrease = getAdjacentCreasesInSubgraph(start, subgraph).get(0);
        List<ReflectionPathBuilder> paths = new ArrayList<>();

        paths.add(new ReflectionPathBuilder(startingCrease, start));
        while (paths.stream().anyMatch(path -> !path.isDone())) {
            for (int i = 0; i < paths.size(); i++) {
                ReflectionPathBuilder path = paths.get(i);
                Crease lastCrease = path.getLastCrease();
                path.setCurrentPoint(lastCrease.getLine().getOppositePoint(path.getCurrentPoint()));
                List<Crease> nextCreases = getAdjacentCreasesInSubgraph(path.getCurrentPoint(), subgraph).stream()
                        .filter(crease -> !path.getCreases().contains(crease))
                        .collect(Collectors.toList());
                if (nextCreases.size() == 0) {
                    path.setDone(true);
                    continue;
                }
                ReflectionPathBuilder tmpPath = path.copy();
                path.addCrease(nextCreases.get(0));
                for (int j = 1; j < nextCreases.size(); j++) {
                    ReflectionPathBuilder newPath = tmpPath.copy();
                    newPath.addCrease(nextCreases.get(j));
                }
            }
        }
        return paths.stream().map(ReflectionPathBuilder::build).collect(Collectors.toList());
    }

    private List<Crease> getAdjacentCreasesInSubgraph(Point p, Collection<Crease> subgraph) {
        return cp.getAdjacentCreases(p).stream()
                .filter(subgraph::contains).collect(Collectors.toList());
    }

    private boolean isLeafNode(Point p, Collection<Crease> subgraph) {
        List<Crease> adjacentCreases = cp.getAdjacentCreases(p);
        long adjacentLinesInSubgraph = adjacentCreases.stream().filter(subgraph::contains).count();
        return adjacentLinesInSubgraph == 1;
    }

    /**
     * @return the (index-1)-th element of creases, wrapping around if index == 0
     */
    private Crease getPrevious(int index, List<Crease> creases) {
        return creases.get((index + creases.size() - 1) % creases.size());
    }

    private double getAngle(Crease crease1, Crease crease2, Point commonPoint) {
        Point p2 = crease1.getLine().getEnd().equals(commonPoint) ? crease1.getLine().getStart() : crease1.getLine().getEnd();
        Point p3 = crease2.getLine().getEnd().equals(commonPoint) ? crease2.getLine().getStart() : crease2.getLine().getEnd();
        double x1 = commonPoint.getX() - p2.getX();
        double y1 = commonPoint.getY() - p2.getY();
        double x2 = commonPoint.getX() - p3.getX();
        double y2 = commonPoint.getY() - p3.getY();
        return Math.acos(
                (x1 * x2 + y1 * y2) /
                        (Math.sqrt(x1 * x1 + y1 * y1) * Math.sqrt(x2 * x2 + y2 * y2))
        );
    }

    private void addReflectionCrease(Crease crease1, Crease crease2) {
        if (!reflectionCreases.containsKey(crease1)) {
            reflectionCreases.put(crease1, new HashSet<>());
        }
        reflectionCreases.get(crease1).add(crease2);
        if (!reflectionCreases.containsKey(crease2)) {
            reflectionCreases.put(crease2, new HashSet<>());
        }
        reflectionCreases.get(crease2).add(crease1);
    }

    private Collection<Crease> getReflectionCreases(Crease crease) {
        if (reflectionCreases.containsKey(crease)) {
            return reflectionCreases.get(crease);
        }
        return new HashSet<>();
    }
}
