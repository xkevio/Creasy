package ovgu.creasy.origami;

import ovgu.creasy.geom.Vertex;

import java.util.*;

/**
 * Extended Crease Pattern, adds information about Reflection Creases
 * to a crease Pattern and is used for the step generation
 * Explained in chapter 4.2 of the paper (pages 28 - 36)
 */
public class ExtendedCreasePattern {
    private Set<Vertex> vertices;
    private Set<ExtendedCrease> creases;
    private Map<Vertex, List<ExtendedCrease>> connections;
    private CreasePattern cp;
    /**
     * @param vertices is the set of extended vertices
     * @param creases is the set of directed edges of the extended graph.
     * @param connections is a set of ordered, circular lists of edges parting from each vertex in vertices
     */
    public ExtendedCreasePattern(Set<Vertex> vertices, Set<ExtendedCrease> creases, Map<Vertex, List<ExtendedCrease>> connections, CreasePattern cp) {
        this.vertices = vertices;
        this.creases = creases;
        this.connections = connections;
        this.cp = cp;
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
        HashSet<ReflectionPath> removableCreases = new HashSet<>();
        for (Vertex vertex : vertices) {
            removableCreases.addAll(findSimpleFolds(vertex));
        }
        for (ReflectionPath removableCreaseList : removableCreases) {
            CreasePattern newcp = this.cp.copy();
            removableCreaseList.getCreases().forEach(newcp::removeCrease);
            newcp.removeLinearPoints();
            ExtendedCreasePattern next = new ExtendedCreasePatternFactory().createExtendedCreasePattern(newcp);
            steps.add(new DiagramStep(this, next));
        }
        return steps;
    }

    private HashSet<ReflectionPath> findSimpleFolds(Vertex vertex) {
        Map<Vertex, ReflectionPath> possiblySimpleFolds = new HashMap<>();
        HashSet<ReflectionPath> simpleFolds = new HashSet<>();
        if (vertex.getType() == Vertex.Type.BORDER) {
            List<ExtendedCrease> outgoing = this.connections.get(vertex);
            for (ExtendedCrease outgoingCrease : outgoing) {
                if (outgoingCrease.getType() == Crease.Type.EDGE) {
                    continue;
                }
                if (outgoingCrease.getEndVertex().getType() == Vertex.Type.BORDER
                    && outgoingCrease.getType()!= Crease.Type.EDGE) {
                    simpleFolds.add(outgoingCrease.getReflectionPath());
                } else if (outgoingCrease.getEndVertex().getType() == Vertex.Type.VIRTUAL) {
                    List<ExtendedCrease> creases = new ArrayList<>();
                    creases.add(outgoingCrease);
                    ExtendedCrease currentCrease = outgoingCrease;
                    while (connections.containsKey(currentCrease.getEndVertex())) {
                        var next = connections.get(currentCrease.getEndVertex());
                        currentCrease = next.get(next.size()-1);
                        creases.add(currentCrease);
                    }
                    Vertex middle = currentCrease.getStartVertex();
                    if (possiblySimpleFolds.containsKey(middle)) {
                        simpleFolds.add(currentCrease.getReflectionPath());
                    } else {
                        possiblySimpleFolds.put(middle, outgoingCrease.getReflectionPath());
                    }
                }
            }
        }
        return simpleFolds;
    }
}
