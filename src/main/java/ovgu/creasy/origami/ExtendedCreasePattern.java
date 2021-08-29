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

    public Set<Vertex> getVertices() {
        return Collections.unmodifiableSet(xV);
    }

    public Set<ExtendedCrease> getCreases() {
        return Collections.unmodifiableSet(xC);
    }

    public Map<Vertex, List<ExtendedCrease>> getListOfVertices() { return Collections.unmodifiableMap(xL); }

    public static ExtendedCreasePattern buildExtendedGraph(CreasePattern cp) {
        ReflectionGraphFactory reflGraph = new ReflectionGraphFactory(cp);
        Point defaultPoint = new Point(0,0);


        Set<Vertex> xV = new HashSet<>();
        Set<ExtendedCrease> xC = new HashSet<>();

        // copy each Vertex (from given CP) to extended Vertex
        Map<Point, Vertex> vertexMap = copyVertices(cp);
        Set<Vertex> vertices = new HashSet<>(vertexMap.values());


        // From each Crease (from given CP) construct extended Crease
        Set<ExtendedCrease> extendedCreases = createExtendedCreases(cp, vertexMap, false);
        Set<ExtendedCrease> extendedCreasesReversed = createExtendedCreases(cp, vertexMap, true);
        Set<ExtendedCrease> inactiveExtendedCreases = new HashSet<>();
        inactiveExtendedCreases.addAll(extendedCreases);
        inactiveExtendedCreases.addAll(extendedCreasesReversed);

        // construct a set of ordered circular lists of edges parting from each vertex xV
        Map<Vertex, List<ExtendedCrease>> xL = createAdjacencyLists(inactiveExtendedCreases);

        // set of all reflection graphs
        Collection<ReflectionGraph> reflectionGraphs = reflGraph.getAllReflectionGraphs();

        for (ReflectionGraph reflectionGraph : reflectionGraphs) {
            // set of local maximum reflection paths in reflectionGraph
            Collection<ReflectionPath> A = reflGraph.getLocalMaxima(reflectionGraph);
            // global maximum in A
            ReflectionPath y = getGlobalMaximum(A);
            // terminal vertices of y
            Vertex xV1 = vertexMap.get(y.getStartingPoint());
            Vertex xV2 = vertexMap.get(y.getEndPoint());
            // Extended Crease in y with xC1.getStartVertex() == xV1
            // TODO: maybe make more efficient
            ExtendedCrease xC1 = inactiveExtendedCreases.stream()
                    .filter(c -> c.getStartVertex().equals(xV1))
                    .filter(c -> y.getPoints().contains(c.getEndVertex().getPoint()))
                    .findFirst().get();
            // Extended Crease in y with xC2.getStartVertex() == xV2
            ExtendedCrease xC2 = inactiveExtendedCreases.stream()
                    .filter(c -> c.getStartVertex().equals(xV2))
                    .filter(c -> y.getPoints().contains(c.getEndVertex().getPoint()))
                    .findFirst().get();
            // activate xC1 and xC2
            xC1.setActive(true);
            xC2.setActive(true);
            inactiveExtendedCreases.remove(xC1);
            inactiveExtendedCreases.remove(xC2);

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
        return new ExtendedCreasePattern(xV, xC, xL);
    }

    private static void insertCreaseIntoAdjacencyList(Map<Vertex, List<ExtendedCrease>> adjacencyLists, ExtendedCrease c) {
        Vertex v = c.getStartVertex();
        if (!adjacencyLists.containsKey(v)) {
            adjacencyLists.put(v, new ArrayList<>());
        }
        List<ExtendedCrease> outgoingCreases = adjacencyLists.get(v);
        int i = 0;
        while (i < outgoingCreases.size()) {
            ExtendedCrease line = outgoingCreases.get(i);
            if (line.getClockwiseAngle() > c.getClockwiseAngle()) {
                break;
            }
            i++;
        }
        outgoingCreases.add(i, c);
    }

    private static Map<Vertex, List<ExtendedCrease>> createAdjacencyLists(Set<ExtendedCrease> creases) {
        Map<Vertex, List<ExtendedCrease>> lists = new HashMap<>();
        for (ExtendedCrease crease : creases) {
            insertCreaseIntoAdjacencyList(lists, crease);
        }
        return lists;
    }

    private static Set<ExtendedCrease> createExtendedCreases(CreasePattern cp, Map<Point, Vertex> extendedVertices, boolean reverse) {
        Set<ExtendedCrease> xC = new HashSet<>();

        // if reverse == false --> xC = (v1, v2, a, false)
        // if reverse == true --> xC = (v2, v1, a, false)
       return cp.getCreases().stream().map(c -> {
            Point start = reverse? c.getLine().getEnd() : c.getLine().getStart();
            Point end = reverse? c.getLine().getStart() : c.getLine().getEnd();
            ExtendedCrease.Type type = switch (c.getType()) {
                case VALLEY -> ExtendedCrease.Type.VALLEY;
                case MOUNTAIN -> ExtendedCrease.Type.MOUNTAIN;
                default -> ExtendedCrease.Type.DONTCARE;
            };
            return new ExtendedCrease(extendedVertices.get(start), extendedVertices.get(end), type, false);
        }).collect(Collectors.toSet());
    }

    private static Map<Point, Vertex> copyVertices(CreasePattern cp) {
        HashMap<Point, Vertex> vertices = new HashMap<>();

        cp.getPoints().forEach(point -> {
            Vertex.Type type;
            if (cp.getAdjacentCreases(point).stream()
                    .anyMatch(c -> c.getType() == Crease.Type.EDGE)
            ) {
                type = Vertex.Type.BORDER;
            } else {
                type = Vertex.Type.INTERNAL;
            }
            vertices.put(point, new Vertex(point, type));
        });

        return vertices;
    }

    private static ReflectionPath getGlobalMaximum(Collection<ReflectionPath> reflectionPaths) {
        return reflectionPaths.stream().max(Comparator.comparingInt(ReflectionPath::length)).orElse(null);
    }
}
