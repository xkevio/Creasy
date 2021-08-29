package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;
import ovgu.creasy.geom.Vertex;

import java.util.*;
import java.util.stream.Collectors;

public class ExtendedCreasePatternFactory {

    public ExtendedCreasePattern createExtendedCreasePattern(CreasePattern cp) {
        ReflectionGraphFactory reflGraph = new ReflectionGraphFactory(cp);
        Point defaultPoint = new Point(0, 0);

        Set<ExtendedCrease> processedExtendedCreases = new HashSet<>();

        // copy each Vertex (from given CP) to extended Vertex
        Map<Point, Vertex> vertexMap = copyVertices(cp);
        Set<Vertex> vertices = new HashSet<>(vertexMap.values());


        // From each Crease (from given CP) construct extended Crease
        Set<ExtendedCrease> extendedCreases = createExtendedCreases(cp, vertexMap);
        Set<ExtendedCrease> extendedCreasesReversed = createReversedExtendedCreases(extendedCreases);
        Set<ExtendedCrease> inactiveExtendedCreases = new HashSet<>();
        inactiveExtendedCreases.addAll(extendedCreases);
        inactiveExtendedCreases.addAll(extendedCreasesReversed);

        // construct a set of ordered circular lists of edges parting from each vertex xV
        Map<Vertex, List<ExtendedCrease>> adjacencyLists = createAdjacencyLists(inactiveExtendedCreases);

        // set of all reflection graphs
        Collection<ReflectionGraph> reflectionGraphs = reflGraph.getAllReflectionGraphs();

        for (ReflectionGraph reflectionGraph : reflectionGraphs) {
            // set of local maximum reflection paths in reflectionGraph
            Collection<ReflectionPath> A = reflGraph.getLocalMaxima(reflectionGraph);
            if (A.isEmpty()) {
                continue;
            }
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
            processedExtendedCreases.add(xC1);
            processedExtendedCreases.add(xC2);

            if (xV1.getPoint() == xV2.getPoint() && xC1.getType() != xC2.getType()) {
                Vertex newVertex = new Vertex(defaultPoint, Vertex.Type.VIRTUAL);
                xC1.setEndVertex(newVertex);
                ExtendedCrease new_xC1 = new ExtendedCrease(newVertex, xV1, xC1.getType(), true);
                insertCreaseIntoAdjacencyList(adjacencyLists, new_xC1);
                xC2.setEndVertex(newVertex);
                ExtendedCrease new_xC2 = new ExtendedCrease(newVertex, xV2, xC1.getType(), true);
                insertCreaseIntoAdjacencyList(adjacencyLists, new_xC2);
                vertices.add(newVertex);
                processedExtendedCreases.add(new_xC1);
                processedExtendedCreases.add(new_xC2);
            } else {
                xC1.setEndVertex(xV2);
                xC2.setEndVertex(xV1);
            }
        }
        return new ExtendedCreasePattern(vertices, processedExtendedCreases, adjacencyLists);
    }

    private void insertCreaseIntoAdjacencyList(Map<Vertex, List<ExtendedCrease>> adjacencyLists, ExtendedCrease c) {
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

    private Map<Vertex, List<ExtendedCrease>> createAdjacencyLists(Set<ExtendedCrease> creases) {
        Map<Vertex, List<ExtendedCrease>> lists = new HashMap<>();
        for (ExtendedCrease crease : creases) {
            insertCreaseIntoAdjacencyList(lists, crease);
        }
        return lists;
    }

    private Set<ExtendedCrease> createExtendedCreases(CreasePattern cp, Map<Point, Vertex> extendedVertices) {
        Set<ExtendedCrease> xC = new HashSet<>();

        // if reverse == false --> xC = (v1, v2, a, false)
        // if reverse == true --> xC = (v2, v1, a, false)
        return cp.getCreases().stream().map(c -> {
            Point start = c.getLine().getStart();
            Point end = c.getLine().getEnd();
            Crease.Type type = c.getType();
            return new ExtendedCrease(extendedVertices.get(start), extendedVertices.get(end), type, false);
        }).collect(Collectors.toSet());
    }

    private Set<ExtendedCrease> createReversedExtendedCreases(Set<ExtendedCrease> extendedCreases) {
        return extendedCreases.stream().map(c -> {
            ExtendedCrease cn = new ExtendedCrease(c.getEndVertex(), c.getStartVertex(), c.getType(), c.getActive());
            cn.setOpposite(c);
            c.setOpposite(cn);
            return cn;
        }).collect(Collectors.toSet());
    }

    private Map<Point, Vertex> copyVertices(CreasePattern cp) {
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

    private ReflectionPath getGlobalMaximum(Collection<ReflectionPath> reflectionPaths) {
        return reflectionPaths.stream().max(Comparator.comparingInt(ReflectionPath::length)).orElse(null);
    }
}
