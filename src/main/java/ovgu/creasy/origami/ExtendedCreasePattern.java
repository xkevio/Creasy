package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;
import ovgu.creasy.geom.Vertex;

import java.util.*;
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
            removableCreases.addAll(findSimpleFolds(vertex).stream().map(Collections::singletonList).collect(Collectors.toList()));
        }
        for (List<ExtendedReflectionPath> removablePathList : removableCreases) {

            for (ExtendedReflectionPath path : removablePathList) {
                System.out.println("ab");
                System.out.println(path.getStart());
                System.out.println(path.getEnd());
            }
            CreasePattern newcp = this.cp.copy();
            removablePathList.forEach(p -> p.getCreases().forEach(newcp::removeCrease));
            newcp.removeLinearPoints();
            ExtendedCreasePattern next = new ExtendedCreasePatternFactory().createExtendedCreasePattern(newcp);
            steps.add(new DiagramStep(this, next));
        }
        return steps;
    }

    //private HashSet<List<ReflectionPath>> findReverseFolds(Vertex vertex) {
    //    List<ExtendedCrease> outgoingCreases = getAdjacencyLists().get(vertex);

    //}

    private Set<ExtendedReflectionPath> findSimpleFolds(Vertex vertex) {
        List<ExtendedCrease> outgoing = this.connections.get(vertex);
        return  outgoing.stream()
                .filter(crease -> crease.getType() != Crease.Type.EDGE)
                .filter(
                        crease -> crease.getExtendedReflectionPath().getEnd().getType() == Vertex.Type.BORDER
                                && crease.getExtendedReflectionPath().getStart().getType() == Vertex.Type.BORDER
                                || (crease.getExtendedReflectionPath().getStart() == crease.getExtendedReflectionPath().getEnd()
                        ))
                .map(ExtendedCrease::getExtendedReflectionPath).collect(Collectors.toSet());

    }
}
