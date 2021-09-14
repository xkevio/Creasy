package ovgu.creasy.origami;

import oripa.domain.cptool.LineAdder;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.Folder;
import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.domain.fold.subface.FacesToCreasePatternConverter;
import oripa.domain.fold.subface.ParentFacesCollector;
import oripa.domain.fold.subface.SplitFacesToSubFacesConverter;
import oripa.domain.fold.subface.SubFacesFactory;
import ovgu.creasy.geom.Point;
import ovgu.creasy.geom.Vertex;
import ovgu.creasy.origami.oripa.OripaTypeConverter;

import javax.vecmath.Vector2d;
import java.util.*;
import java.util.stream.Collectors;

public class ExtendedCreasePatternFactory {

    public ExtendedCreasePattern createExtendedCreasePattern(CreasePattern cp) {
        ReflectionGraphFactory reflGraphFactory = new ReflectionGraphFactory(cp);
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
        Collection<ReflectionGraph> reflectionGraphs = reflGraphFactory.getAllReflectionGraphs();

        for (ReflectionGraph reflectionGraph : reflectionGraphs) {
            // set of local maximum reflection paths in reflectionGraph
            Collection<ReflectionPath> A = reflectionGraph.getLocalMaxima();
            if (A.isEmpty()) {
                continue;
            }
            // global maximum in A
            ReflectionPath refPath = getGlobalMaximum(A);
            // terminal vertices of refPath
            Vertex vertex1 = vertexMap.get(refPath.getStartingPoint());
            Vertex vertex2 = vertexMap.get(refPath.getEndPoint());
            List<Crease> creases = refPath.getCreases();
            ExtendedReflectionPath ex = new ExtendedReflectionPath(vertex1, vertex2, creases);
            ExtendedReflectionPath exR = new ExtendedReflectionPath(vertex2, vertex1, creases);
            // Extended Crease in refPath with exCrease1.getStartVertex() == vertex1
            // TODO: maybe make more efficient
            ExtendedCrease exCrease1 = inactiveExtendedCreases.stream()
                                                        .filter(c -> c.getStartVertex().equals(vertex1))
                                                        .filter(c -> refPath.getPoints().contains(c.getEndVertex().getPoint()))
                                                        .findFirst().get();
            // Extended Crease in refPath with exCrease2.getStartVertex() == vertex2
            ExtendedCrease exCrease2 = inactiveExtendedCreases.stream()
                                                        .filter(c -> c.getStartVertex().equals(vertex2))
                                                        .filter(c -> refPath.getPoints().contains(c.getEndVertex().getPoint()))
                                                        .findFirst().get();
            // activate exCrease1 and exCrease2
            exCrease1.setActive(true);
            exCrease1.setExtendedReflectionPath(ex);
            exCrease2.setActive(true);
            exCrease2.setExtendedReflectionPath(exR);
            inactiveExtendedCreases.remove(exCrease1);
            inactiveExtendedCreases.remove(exCrease2);
            processedExtendedCreases.add(exCrease1);
            processedExtendedCreases.add(exCrease2);

            if (vertex1.getPositionAfterFolding().distance(vertex2.getPositionAfterFolding()) <= 0.0000001
                    && exCrease1.getType() != exCrease2.getType()
                    && vertex1.getType() != Vertex.Type.BORDER && vertex2.getType() != Vertex.Type.BORDER) {
                Vertex newVertex = new Vertex(defaultPoint, defaultPoint, Vertex.Type.VIRTUAL);
                exCrease1.setEndVertex(newVertex);
                ExtendedCrease new_xC1 = new ExtendedCrease(newVertex, vertex1, exCrease1.getType(), true);
                insertCreaseIntoAdjacencyList(adjacencyLists, new_xC1);
                exCrease2.setEndVertex(newVertex);
                ExtendedCrease new_xC2 = new ExtendedCrease(newVertex, vertex2, exCrease1.getType(), true);
                insertCreaseIntoAdjacencyList(adjacencyLists, new_xC2);
                vertices.add(newVertex);
                new_xC1.setExtendedReflectionPath(ex);
                new_xC2.setExtendedReflectionPath(exR);
                processedExtendedCreases.add(new_xC1);
                processedExtendedCreases.add(new_xC2);
            } else {
                exCrease1.setEndVertex(vertex2);
                exCrease2.setEndVertex(vertex1);
            }
        }
        return new ExtendedCreasePattern(vertices, processedExtendedCreases, adjacencyLists, cp);
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
        return extendedCreases.stream()
                              .map(c -> new ExtendedCrease(
                                  c.getEndVertex(),
                                  c.getStartVertex(),
                                  c.getType(),
                                  c.getActive()))
                              .collect(Collectors.toSet());
    }

    private Map<Point, Vertex> copyVertices(CreasePattern cp) {
        HashMap<Point, Vertex> vertices = new HashMap<>();
        Folder folder = new Folder(
                new SubFacesFactory(
                        new FacesToCreasePatternConverter(
                                new CreasePatternFactory(),
                                new LineAdder()),
                        new OrigamiModelFactory(),
                        new SplitFacesToSubFacesConverter(),
                        new ParentFacesCollector()));
        CreasePatternInterface oripaCp = OripaTypeConverter.convertToOripaCp(cp);
        OrigamiModel model = new OrigamiModelFactory().createOrigamiModel(oripaCp, oripaCp.getPaperSize());
        FoldedModel foldedModel = folder.fold(model, false);
        foldedModel.getOrigamiModel().getVertices().forEach(oriVertex ->  {
            Vertex.Type type;
            if (oriVertex.edgeStream().anyMatch(OriEdge::isBoundary)) {
                type = Vertex.Type.BORDER;
            } else {
                type = Vertex.Type.INTERNAL;
            }
            Vector2d posbf = oriVertex.getPositionBeforeFolding();
            Vector2d pos = oriVertex.getPosition();
            Point pbf = new Point(posbf.x, posbf.y);
            Point posPoint = new Point(pos.x, pos.y);
            vertices.put(pbf, new Vertex(pbf, posPoint, type));
        });

        return vertices;
    }

    private ReflectionPath getGlobalMaximum(Collection<ReflectionPath> reflectionPaths) {
        return reflectionPaths.stream().max(Comparator.comparingInt(ReflectionPath::length)).orElse(null);
    }
}
