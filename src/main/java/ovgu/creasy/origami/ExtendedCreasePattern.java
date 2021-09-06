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

    public ExtendedCreasePattern() {
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

    private HashSet<List<ExtendedReflectionPath>> findReverseFolds(Vertex vertex) {
        List<ExtendedCrease> outgoingCreases = getAdjacencyLists().get(vertex);
        HashSet<List<ExtendedReflectionPath>> paths = new HashSet<>();
        outgoingCreases = outgoingCreases.stream()
                .filter(crease -> crease.getExtendedReflectionPath().getStart().equals(vertex))
                .collect(Collectors.toList());
        List<List<ExtendedCrease>> viableCombinations = findViableCombinations(outgoingCreases);
        return paths;
    }

    private List<List<ExtendedCrease>> findViableCombinations(List<ExtendedCrease> outgoingCreases) {
        List<List<ExtendedCrease>> combinations = new ArrayList<>();
        for (int i = 0; i < outgoingCreases.size(); i++) {
            Crease.Type mainType = outgoingCreases.get(0).getType();
            int j = (i+1) % outgoingCreases.size();

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
