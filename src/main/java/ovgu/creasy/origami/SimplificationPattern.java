package ovgu.creasy.origami;

import ovgu.creasy.geom.Vertex;

import java.util.*;

public class SimplificationPattern {
    private final List<Edge> pattern = new ArrayList<>();
    private final List<Edge> simplified = new ArrayList<>();
    private final Map<Integer, List<Edge>> patternOutgoingEdges = new HashMap<>();
    private final Map<Integer, List<Edge>> simplifiedOutgoingEdges = new HashMap<>();
    private final List<VertexType> vertexTypes;

    enum VertexType {
        INTERNAL, BORDER
    }
    class Edge {
        int start;
        int end;
        Crease.Type type;

        public Edge(int start, int end, Crease.Type type) {
            this.start = start;
            this.end = end;
            this.type = type;
        }
    }

    public SimplificationPattern(VertexType... vertexTypes) {
        this.vertexTypes = Arrays.asList(vertexTypes);
    }

    public void addPatternEdge(int from, int to, Crease.Type type) {
        addEdge(pattern, patternOutgoingEdges, from, to, type);
    }

    public void addSimplifiedEdge(int from, int to, Crease.Type type) {
        addEdge(simplified, simplifiedOutgoingEdges, from, to, type);
    }

    private void addEdge(List<Edge> edgeList, Map<Integer, List<Edge>> outgoingEdges, int from, int to, Crease.Type type) {
        assert type == Crease.Type.VALLEY || type == Crease.Type.MOUNTAIN;
        Edge e = new Edge(from, to, type);
        Edge e2 = new Edge(to, from, type);
        edgeList.add(e);
        edgeList.add(e2);
        insertOutgoingEdge(outgoingEdges, from, e);
        insertOutgoingEdge(outgoingEdges, to, e2);
    }

    private void insertOutgoingEdge(Map<Integer, List<Edge>> outgoingEdges,int i, Edge e) {
        if (!outgoingEdges.containsKey(i)) {
            outgoingEdges.put(i, new ArrayList<>());
        }
        outgoingEdges.get(i).add(e);
    }

    public CreasePattern simplify(ExtendedCreasePattern ecp, Map<Integer, Vertex> match) {
        CreasePattern simplified = new CreasePattern();
        Set<Crease> omittedLines = new HashSet<>();
        for (Edge edge : pattern) {
            for (ExtendedCrease crease : ecp.getCreases()) {
                if ((crease.getStartVertex() == match.get(edge.start) && crease.getEndVertex() == match.get(edge.end))
                        || (crease.getEndVertex() == match.get(edge.start) && crease.getStartVertex() == match.get(edge.end))) {
                    omittedLines.addAll(crease.getExtendedReflectionPath().getCreases());
                }
            }
        }
        for (Crease crease : ecp.toCreasePattern().getCreases()  ) {
            if (!omittedLines.contains(crease)){
                simplified.addCrease(crease);
            }
        }
        return simplified;
    }

    public List<Map<Integer, Vertex>> matches(ExtendedCreasePattern ecp) {
        List<Map<Integer, Vertex>> matches = new ArrayList<>();
        for (Vertex vertex : ecp.getVertices()) {
            Set<Edge> validating = new HashSet<>();
            Set<ExtendedCrease> validatingCreases = new HashSet<>();
            matches.addAll(findMatches(vertex, 0, validating, validatingCreases, ecp, false));
            validatingCreases.clear();
            validating.clear();
            matches.addAll(findMatches(vertex, 0, validating, validatingCreases, ecp, true));
        }
        return matches;
    }

    private List<Map<Integer, Vertex>> findMatches(Vertex vertex, int point, Set<Edge> validating, Set<ExtendedCrease> validatingCreases, ExtendedCreasePattern ecp, boolean inverted) {
        if (vertexTypes.get(point) == VertexType.BORDER) {
            if (vertex.getType() == Vertex.Type.BORDER || vertex.getType() == Vertex.Type.VIRTUAL) {
                HashMap<Integer, Vertex> map = new HashMap<>();
                map.put(point, vertex);
                return Collections.singletonList(map);
            } else {
                return Collections.emptyList();
            }
        } else {
            List<Map<Integer, Vertex>> mappings = new ArrayList<>();
            List<Edge> outgoingEdges = patternOutgoingEdges.get(point);
            List<ExtendedCrease> outgoingCreases = ecp.getAdjacencyLists().get(vertex);
            for (int i = 0; i < outgoingCreases.size(); i++) {
                boolean valid = true;
                ExtendedCrease outgoingCrease = outgoingCreases.get(i);
                if (validatingCreases.contains(outgoingCrease) || !checkMV(outgoingEdges.get(0), outgoingCrease, inverted)) {
                    continue;
                }
                Map<Integer, Vertex> currentMapping = new HashMap<>();
                currentMapping.put(point, vertex);
                List<Map<Integer, Vertex>> currentMappings = new ArrayList<>();
                currentMappings.add(currentMapping);
                Set<Edge> vEdges = new HashSet<>();
                Set<ExtendedCrease> vCreases = new HashSet<>();
                for (int j = 0; j < outgoingEdges.size(); j++) {
                    Edge currentEdge = outgoingEdges.get(j);
                    if (validating.contains(currentEdge)) {
                        continue;
                    }
                    ExtendedCrease currentCrease = outgoingCreases.get((i+j)%outgoingCreases.size());
                    if (!checkMV(currentEdge, currentCrease, inverted) || validatingCreases.contains(currentCrease)) {
                        valid = false;
                        break;
                    }
                    validating.add(currentEdge);
                    vEdges.add(currentEdge);
                    validatingCreases.add(currentCrease);
                    vCreases.add(currentCrease);
                    List<Map<Integer, Vertex>> newMappings = findMatches(currentCrease.getEndVertex(),
                            currentEdge.end, validating, validatingCreases, ecp, inverted);
                    currentMappings = mergeMaps(currentMappings, newMappings);
                }
                validating.remove(outgoingEdges.get(0));
                validatingCreases.remove(outgoingCrease);
                validating.removeAll(vEdges);
                validatingCreases.removeAll(vCreases);
                if (valid) {
                    mappings.addAll(currentMappings);
                }
            }
            return mappings;
        }
    }

    private List<Map<Integer, Vertex>> mergeMaps(List<Map<Integer, Vertex>> maps1, List<Map<Integer, Vertex>> maps2) {
        List<Map<Integer, Vertex>> fullNewMappings = new ArrayList<>();
        for (Map<Integer, Vertex> map1 : maps1) {
            for (Map<Integer, Vertex> map2 : maps2) {
                Map<Integer, Vertex> mergedMap = new HashMap<>(map1);
                mergedMap.putAll(map2);
                fullNewMappings.add(mergedMap);
            }
        }
        return fullNewMappings;
    }

    private boolean checkMV(Edge edge, ExtendedCrease crease, boolean inverted) {
        if (inverted) {
            return edge.type.opposite() == crease.getType();
        }
        return edge.type == crease.getType();
    }
}
