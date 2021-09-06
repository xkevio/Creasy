package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;
import ovgu.creasy.geom.Vertex;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Extended Crease Pattern, adds information about Reflection Creases
 * to a crease Pattern and is used for the step generation
 * Explained in chapter 4.2 of the paper (pages 28 - 36)
 */
public class ExtendedCreasePattern {
    private Set<Vertex> vertices;
    private Map<Point, Vertex> vertexMap;
    private Set<ExtendedCrease> creases;
    private Set<ExtendedReflectionPath> reflectionPaths;
    private Map<Vertex, List<ExtendedCrease>> connections;
    private CreasePattern cp;
    /**
     * @param vertices is the set of extended vertices
     * @param creases is the set of directed edges of the extended graph.
     * @param connections is a set of ordered, circular lists of edges parting from each vertex in vertices
     */
    public ExtendedCreasePattern(Set<Vertex> vertices,
                                 Set<ExtendedCrease> creases,
                                 Map<Vertex, List<ExtendedCrease>> connections,
                                 CreasePattern cp,
                                 Map<Point, Vertex> vertexMap) {
        this.vertices = vertices;
        this.creases = creases;
        this.connections = connections;
        this.cp = cp;
        this.reflectionPaths = new HashSet<>();
        for (ExtendedCrease crease : creases) {
            reflectionPaths.add(crease.getExtendedReflectionPath());
        }
        this.vertexMap = vertexMap;
    }

    public CreasePattern toCreasePattern() {
        return this.cp;
    }

    public Set<Vertex> getVertices() {
        return Collections.unmodifiableSet(vertices);
    }

    public Set<ExtendedCrease> getCreases() {
        return Collections.unmodifiableSet(creases);
    }

    public Map<Vertex, List<ExtendedCrease>> getAdjacencyLists() { return Collections.unmodifiableMap(connections); }

    @Override
    public String toString() {
        return "ExtendedCreasePattern{" +
            "xC=" + creases +
            '}';
    }

    public List<DiagramStep> possibleSteps() {
        List<DiagramStep> steps = new ArrayList<>();
        HashSet<List<ExtendedReflectionPath>> removableCreases = new HashSet<>();
        for (Vertex vertex : vertices) {

            removableCreases.addAll(findSimpleFolds(vertex).stream().map(Collections::singletonList)
                    .collect(Collectors.toList()));
            removableCreases.addAll(findReverseFolds(vertex));
        }
        List<CreasePattern> newcps = new ArrayList<>();
        for (List<ExtendedReflectionPath> removablePathList : removableCreases) {
            CreasePattern newcp = this.cp.copy();
            removablePathList.forEach(p -> p.getCreases().forEach(newcp::removeCrease));
            newcp.removeLinearPoints();
            newcps.add(newcp);
        }
        newcps.stream().distinct().forEach(cp -> {
            ExtendedCreasePattern next = new ExtendedCreasePatternFactory().createExtendedCreasePattern(cp);
            steps.add(new DiagramStep(this, next));
        });
        return steps;
    }

    private double calculateNewAngle(Vertex vertex) {
        List<Double> angles = new ArrayList<>();
        List<ExtendedCrease> extendedCreases = connections.get(vertex);
        // double biggestAngle = 0.0;
        int sum = 0;

        for (int i = 0; i < extendedCreases.size() - 1; i++) {
            // TODO
            ExtendedCrease crease = extendedCreases.get(i);
            Point a = crease.getStartVertex().getPoint();
            Point b = crease.getEndVertex().getPoint();

            double angle = Math.acos((a.dot(b)) / ((a.distance(a)) * (b.distance(b))));
            angles.add(angle);
        }

        double currentMax = 0;
        int index = 0;
        for (int i = 0; i < angles.size(); i++) {
            if (angles.get(i) > currentMax) {
                currentMax = angles.get(i);
                index = i;
            }
        }

        // biggestAngle = currentMax;
        if (index % 2 == 0) {
            for (int i = 2; i < index; i += 2) {
                sum += angles.get(i);
            }
        } else {
            for (int i = 1; i < index; i += 2) {
                sum += angles.get(i);
            }
        }

        // c_k.angle?
        return ((ExtendedCrease) creases.toArray()[index]).getClockwiseAngle() + (Math.PI - sum);
    }

    private HashSet<List<ExtendedReflectionPath>> findReverseFolds(Vertex vertex) {
        List<ExtendedCrease> outgoingCreases = getAdjacencyLists().get(vertex);
        HashSet<List<ExtendedReflectionPath>> paths = new HashSet<>();
        //outgoingCreases = outgoingCreases.stream()
                //.filter(crease -> crease.getExtendedReflectionPath().getStart().equals(vertex))
                //.collect(Collectors.toList());
        List<List<ExtendedCrease>> viableCombinations = findViableCombinations(outgoingCreases);
        List<List<ExtendedCrease>> validCombinations = viableCombinations.stream().filter(combination -> {
            Vertex p = combination.get(0).getEndVertex();
            boolean refPoint = true;
            boolean border = true;
            for (ExtendedCrease extendedCrease : combination) {
                if (extendedCrease.getExtendedReflectionPath().getEnd() != p){
                    refPoint = false;
                }
                if (extendedCrease.getExtendedReflectionPath().getEnd().getType() != Vertex.Type.BORDER) {
                    border = false;
                }
                if (!border && !refPoint) {
                    break;
                }
            }
            return border || refPoint;
        }).collect(Collectors.toList());
        for (List<ExtendedCrease> validCombination : validCombinations) {
            paths.add(validCombination.stream().map(ExtendedCrease::getExtendedReflectionPath).collect(Collectors.toList()));
        }
        return paths;
    }

    private List<List<ExtendedCrease>> findViableCombinations(List<ExtendedCrease> outgoingCreases) {
        List<List<ExtendedCrease>> combinations = new ArrayList<>();
        for (int i = 0; i < outgoingCreases.size(); i++) {
            Crease.Type mainType = outgoingCreases.get(i).getType();
            if (mainType == Crease.Type.EDGE) {
                continue;
            }
            ExtendedCrease middle = outgoingCreases.get(i);
            ExtendedCrease left = outgoingCreases.get((i-1+outgoingCreases.size())%outgoingCreases.size());
            ExtendedCrease right = outgoingCreases.get((i+1)%outgoingCreases.size());
            if (left.getType() == mainType.opposite()
                    && right.getType() == mainType.opposite()
                    && left.getExtendedReflectionPath().getStart() == left.getStartVertex()
                    && right.getExtendedReflectionPath().getStart() == right.getStartVertex()
                    && middle.getExtendedReflectionPath().getStart() == middle.getStartVertex()) {

                combinations.add(Arrays.asList(left, middle, right));
            }
        }
        return combinations;
    }

    private Set<ExtendedReflectionPath> findSimpleFolds(Vertex vertex) {
        List<ExtendedCrease> outgoing = this.connections.get(vertex);
        return  outgoing.stream()
                .filter(crease -> crease.getType() != Crease.Type.EDGE)
                .filter(
                        crease -> crease.getExtendedReflectionPath().getEnd().getType() == Vertex.Type.BORDER
                                && crease.getExtendedReflectionPath().getStart().getType() == Vertex.Type.BORDER
                                || (crease.getExtendedReflectionPath().getStart() == crease.getExtendedReflectionPath().getEnd()
                        ))
                .map(ExtendedCrease::getExtendedReflectionPath).filter(distinctOrReverse()).collect(Collectors.toSet());

    }

    private Predicate<ExtendedReflectionPath> distinctOrReverse() {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return p -> {
            boolean b = seen.add(p);
            seen.add(new ExtendedReflectionPath(p.getEnd(), p.getStart(), p.getCreases()));
            return b;
        };
    }
}
