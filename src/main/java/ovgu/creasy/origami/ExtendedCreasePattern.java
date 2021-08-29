package ovgu.creasy.origami;

import ovgu.creasy.geom.Line;
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
    private Set<Vertex> xV;
    private Set<ExtendedCrease> xC;
    private Map<Vertex, List<ExtendedCrease>> xL;

    /**
     * @param xV is the set of extended vertices
     * @param xC is the set of directed edges of the extended graph. Each xC ist called extended creases
     * @param xL is a set of ordered, circular lists of edges parting from each vertex xV
     */
    public ExtendedCreasePattern(Set<Vertex> xV, Set<ExtendedCrease> xC, Map<Vertex, List<ExtendedCrease>> xL) {
        this.xV = xV;
        this.xC = xC;
        this.xL = xL;
    }

    public ExtendedCreasePattern() {
    }

    public CreasePattern toCreasePattern() {
        Set<Line> addedLines = new HashSet<>();
        CreasePattern cp = new CreasePattern();
        for (ExtendedCrease extendedCrease : xC) {
            if (extendedCrease.getStartVertex().getType() == Vertex.Type.VIRTUAL
                || extendedCrease.getEndVertex().getType() == Vertex.Type.VIRTUAL) {
                continue;
            }
            Line line = new Line(
                extendedCrease.getStartVertex().getPoint(),
                extendedCrease.getEndVertex().getPoint());
            if (addedLines.contains(new Line(line.getEnd(), line.getStart()))) { // extended Creases can go in both directions,
                continue;                                                        // but we only need one
            }
            addedLines.add(line);
            cp.addCrease(new Crease(
                line,
                extendedCrease.getType()));
        }
        return cp;
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

    public Collection<DiagramStep> possibleSteps() {
        List<DiagramStep> steps = new ArrayList<>();
        return steps;
    }
}
