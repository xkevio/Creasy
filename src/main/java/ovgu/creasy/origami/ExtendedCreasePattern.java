package ovgu.creasy.origami;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import ovgu.creasy.geom.Vertex;
import ovgu.creasy.origami.basic.Crease;
import ovgu.creasy.origami.basic.CreasePattern;
import ovgu.creasy.origami.basic.DiagramStep;
import ovgu.creasy.origami.oripa.OripaTypeConverter;

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
        // System.out.println("finding simple folds");
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

        for (SimplificationPattern pattern : KnownPatterns.allPatterns) {
            List<SimplificationPattern.Match> matches = pattern.matches(this);
            for (SimplificationPattern.Match match : matches) {
                newcps.add(pattern.simplify(this, match));
            }
        }

        FoldabilityChecker foldabilityChecker = new FoldabilityChecker();

        newcps.stream().distinct().forEach(cp -> {
            CreasePattern cp2 = new CreasePattern();
            cp.getCreases().forEach(cp2::addCrease);
            cp2.removeAllLinearPoints();
            CreasePatternInterface cpOripa = OripaTypeConverter.convertToOripaCp(cp2);
            OrigamiModel model = new OrigamiModelFactory().createOrigamiModel(cpOripa, cpOripa.getPaperSize());
            if (foldabilityChecker.testLocalFlatFoldability(model)) {
                steps.add(new DiagramStep(this.toCreasePattern(), cp2));
            }
        });
        return steps;
    }


    private Set<ExtendedReflectionPath> findSimpleFolds(Vertex vertex) {
        List<ExtendedCrease> outgoing = this.connections.get(vertex);
        return  outgoing.stream()
                .filter(crease -> crease.getType() != Crease.Type.EDGE)
                .filter(ExtendedCrease::isComplete)
                .map(ExtendedCrease::getExtendedReflectionPath).collect(Collectors.toSet());
    }
}
