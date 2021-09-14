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
    private Set<ExtendedCrease> creases;
    private Map<Vertex, List<ExtendedCrease>> connections;
    private List<DiagramStep> possibleSteps;
    private CreasePattern cp;
    /**
     * @param vertices is the set of extended vertices
     * @param creases is the set of directed edges of the extended graph.
     * @param connections is a set of ordered, circular lists of edges parting from each vertex in vertices
     */
    public ExtendedCreasePattern(Set<Vertex> vertices,
                                 Set<ExtendedCrease> creases,
                                 Map<Vertex, List<ExtendedCrease>> connections,
                                 CreasePattern cp) {
        this.vertices = vertices;
        this.creases = creases;
        this.connections = connections;
        this.cp = cp;
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
        if (this.possibleSteps == null) {
            this.possibleSteps = calculatePossibleSteps();
        }
        return possibleSteps;
    }

    private List<DiagramStep> calculatePossibleSteps() {
        List<DiagramStep> steps = new ArrayList<>();
        HashSet<List<ExtendedReflectionPath>> removableCreases = new HashSet<>();
        System.out.println("finding simple folds");
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

        List<SimplificationPattern> patterns = new ArrayList<>();
        SimplificationPattern insideReverseFold = new SimplificationPattern(
                SimplificationPattern.VertexType.INTERNAL,
                SimplificationPattern.VertexType.BORDER,
                SimplificationPattern.VertexType.BORDER,
                SimplificationPattern.VertexType.BORDER,
                SimplificationPattern.VertexType.BORDER);
        insideReverseFold.addPatternEdge(0, 1, Crease.Type.MOUNTAIN);
        insideReverseFold.addPatternEdge(0, 2, Crease.Type.VALLEY);
        insideReverseFold.addPatternEdge(0, 3, Crease.Type.MOUNTAIN);
        insideReverseFold.addSimplifiedEdge(0,4, Crease.Type.MOUNTAIN);
        patterns.add(insideReverseFold);
        SimplificationPattern outsideReverseFold = new SimplificationPattern(
                SimplificationPattern.VertexType.INTERNAL,
                SimplificationPattern.VertexType.BORDER,
                SimplificationPattern.VertexType.BORDER,
                SimplificationPattern.VertexType.BORDER,
                SimplificationPattern.VertexType.BORDER);
        outsideReverseFold.addPatternEdge(0, 1, Crease.Type.MOUNTAIN);
        outsideReverseFold.addPatternEdge(0, 2, Crease.Type.MOUNTAIN);
        outsideReverseFold.addPatternEdge(0, 3, Crease.Type.MOUNTAIN);
        outsideReverseFold.addSimplifiedEdge(0,4, Crease.Type.VALLEY);
        patterns.add(outsideReverseFold);

        for (SimplificationPattern pattern : patterns) {
            List<SimplificationPattern.Match> matches = pattern.matches(this);
            for (SimplificationPattern.Match match : matches) {
                newcps.add(pattern.simplify(this, match));
            }
        }
        newcps.stream().distinct().forEach(cp -> {
            ExtendedCreasePattern next = new ExtendedCreasePattern(null, new HashSet<>(), null, cp); //new ExtendedCreasePatternFactory().createExtendedCreasePattern(cp);
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

    private Set<ExtendedReflectionPath> findSimpleFolds(Vertex vertex) {
        List<ExtendedCrease> outgoing = this.connections.get(vertex);
        return  outgoing.stream()
                .filter(crease -> crease.getType() != Crease.Type.EDGE)
                .filter(ExtendedCrease::isComplete)
                .map(ExtendedCrease::getExtendedReflectionPath).collect(Collectors.toSet());
    }
}
