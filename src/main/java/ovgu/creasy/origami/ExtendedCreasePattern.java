package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;
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
    private Set<List<Vertex>> xL;

    /**
     * @param xV is the set of extended vertices
     * @param xC is the set of directed edges of the extended graph. Each xC ist called extended creases
     * @param xL is a set of ordered, circular lists of edges parting from each vertex xV
     */
    public ExtendedCreasePattern(Set<Vertex> xV, Set<ExtendedCrease> xC, Set<List<Vertex>> xL) {
        this.xV = xV;
        this.xC = xC;
        this.xL = xL;
    }

    public ExtendedCreasePattern() {
    }

    public Set<Vertex> getVertices() {
        return Collections.unmodifiableSet(xV);
    }

    public Set<ExtendedCrease> getCreases() {
        return Collections.unmodifiableSet(xC);
    }

    public Set<List<Vertex>> getListOfVertices() { return Collections.unmodifiableSet(xL); }

    public ExtendedCreasePattern buildExtendedGraph(CreasePattern cp) {
        ReflectionGraphFactory reflGraph = new ReflectionGraphFactory(cp);
        Point defaultPoint = new Point(0,0);

        // copy each Vertex (from given CP) to extended Vertex
        Set<Vertex> vertices = copyVertices(cp);

        // From each Crease (from given CP) construct extended Crease
        Set<ExtendedCrease> extendedCreases = createExtendedCreases(cp, false);
        Set<ExtendedCrease> extendedCreasesReversed = createExtendedCreases(cp, true);

        // construct a set of ordered circular lists of edges parting from each vertex xV
        Set<List<Vertex>> xL = createList(vertices);

        // set of all reflection graphs
        Collection<ReflectionGraph> reflectionGraphs = reflGraph.getAllReflectionGraphs();

        for (ReflectionGraph reflectionGraph : reflectionGraphs) {
            // set of local maximum reflection paths in reflectionGraph
            Collection<ReflectionPath> A = reflGraph.getLocalMaxima(reflectionGraph);
            // global maximum in A
            ReflectionPath y = getGlobalMaxima(A);
            // terminal vertices of y TODO
            Vertex x1, x2;
            // Extended Crease in y with xC1.getStartVertex() == xV1 TODO
            ExtendedCrease xC1 = null;
            // Extended Crease in y with xC2.getStartVertex() == xV2 TODO
            ExtendedCrease xC2 = null;
            // activate xC1 and xC2
            xC1.setActive(true);
            xC2.setActive(true);

            Vertex xV1 = null, xV2 = null;

            if (xV1.getPoint() == xV2.getPoint() && xC1.getType() != xC2.getType()) {
                Vertex newVertex = new Vertex(defaultPoint, Vertex.Type.VIRTUAL);
                xC1.setEndVertex(newVertex);
                ExtendedCrease new_xC1 = new ExtendedCrease(newVertex, xV1, xC1.getType(), true);
                xC2.setEndVertex(newVertex);
                ExtendedCrease new_xC2 = new ExtendedCrease(newVertex, xV2, xC1.getType(), true);
                vertices.add(newVertex);
                xC.add(new_xC1);
                xC.add(new_xC2);
            } else {
                xC1.setEndVertex(xV2);
                xC2.setEndVertex(xV1);
            }
        }
        ExtendedCreasePattern xCP = new ExtendedCreasePattern(xV, xC, xL);
        return xCP;
    }
    // TODO
    private Set<List<Vertex>> createList(Set<Vertex> vertices) {
        Set<List<Vertex>> lists = new HashSet<>();
        return lists;
    }

    // TODO
    private Set<ExtendedCrease> createExtendedCreases(CreasePattern cp, boolean reverse) {
        Set<ExtendedCrease> xC = new HashSet<>();

        // if reverse == false --> xC = (v1, v2, a, false)
        // if reverse == true --> xC = (v2, v1, a, false)

        return xC;
    }

    // TODO
    private Set<Vertex> copyVertices(CreasePattern cp) {
        Set<Vertex> vertices = new HashSet<>();

        // TODO figure out correct vertex type
        cp.getPoints().forEach(point -> vertices.add(new Vertex(point, Vertex.Type.VIRTUAL)));

        return vertices;
    }

    // TODO
    private ReflectionPath getGlobalMaxima(Collection<ReflectionPath> reflectionPath) {
        ReflectionPath globalMaxima = null;
        return globalMaxima;
    }
}
