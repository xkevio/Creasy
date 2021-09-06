package ovgu.creasy.origami;

import ovgu.creasy.geom.Vertex;

import java.util.*;

/**
 * Extended Crease Pattern, adds information about Reflection Creases
 * to a crease Pattern and is used for the step generation
 * Explained in chapter 4.2 of the paper (pages 28 - 36)
 */
public class ExtendedCreasePattern {
    private Set<Vertex> xV;
    private Set<ExtendedCrease> xC;
    private Map<Vertex, List<ExtendedCrease>> xL;
    private CreasePattern cp;
    /**
     * @param xV is the set of extended vertices
     * @param xC is the set of directed edges of the extended graph. Each xC ist called extended creases
     * @param xL is a set of ordered, circular lists of edges parting from each vertex xV
     */
    public ExtendedCreasePattern(Set<Vertex> xV, Set<ExtendedCrease> xC, Map<Vertex, List<ExtendedCrease>> xL, CreasePattern cp) {
        this.xV = xV;
        this.xC = xC;
        this.xL = xL;
        this.cp = cp;
    }

    public ExtendedCreasePattern() {
    }

    public CreasePattern toCreasePattern() {
        return this.cp;
    }

    public Set<Vertex> getVertices() {
        return Collections.unmodifiableSet(xV);
    }

    public Set<ExtendedCrease> getCreases() {
        return Collections.unmodifiableSet(xC);
    }

    public Map<Vertex, List<ExtendedCrease>> getAdjacencyLists() { return Collections.unmodifiableMap(xL); }

    @Override
    public String toString() {
        return "ExtendedCreasePattern{" +
            "xC=" + xC +
            '}';
    }

    public List<DiagramStep> possibleSteps() {
        List<DiagramStep> steps = new ArrayList<>();
        HashSet<ReflectionPath> removableCreases = new HashSet<>();
        Map<Vertex, List<ExtendedCrease>> possiblyRemovableCreases = new HashMap<>();
        for (Vertex vertex : xV) {
            if (vertex.getType() == Vertex.Type.BORDER) {
                List<ExtendedCrease> outgoing = this.xL.get(vertex);
                for (ExtendedCrease outgoingCrease : outgoing) {
                    if (outgoingCrease.getType() == Crease.Type.EDGE) {
                        continue;
                    }
                    if (outgoingCrease.getEndVertex().getType() == Vertex.Type.BORDER
                        && outgoingCrease.getType()!= Crease.Type.EDGE) {
                        removableCreases.add(outgoingCrease.getReflectionPath());
                    } else if (outgoingCrease.getEndVertex().getType() == Vertex.Type.VIRTUAL) {
                        List<ExtendedCrease> creases = new ArrayList<>();
                        creases.add(outgoingCrease);
                        ExtendedCrease currentCrease = outgoingCrease;
                        while (xL.containsKey(currentCrease.getEndVertex())) {
                            var next = xL.get(currentCrease.getEndVertex());
                            currentCrease = next.get(next.size()-1);
                            creases.add(currentCrease);
                        }
                        Vertex middle = currentCrease.getEndVertex();
                        if (possiblyRemovableCreases.containsKey(middle)) {
                            possiblyRemovableCreases.get(middle).addAll(creases);
                            removableCreases.add(currentCrease.getReflectionPath());
                        } else {
                            possiblyRemovableCreases.put(middle, creases);
                        }
                    }
                }
            }
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
}
