package ovgu.creasy.origami;

import java.util.ArrayList;
import java.util.List;

public class KnownPatterns {
    public static final List<SimplificationPattern> allPatterns = new ArrayList<>();
    public static SimplificationPattern insideReverseFold = new SimplificationPattern(
        SimplificationPattern.VertexType.INTERNAL,
        SimplificationPattern.VertexType.BORDER,
        SimplificationPattern.VertexType.BORDER,
        SimplificationPattern.VertexType.BORDER,
        SimplificationPattern.VertexType.BORDER);
    static {
        insideReverseFold.addPatternEdge(0, 1, Crease.Type.MOUNTAIN);
        insideReverseFold.addPatternEdge(0, 2, Crease.Type.VALLEY);
        insideReverseFold.addPatternEdge(0, 3, Crease.Type.MOUNTAIN);
        insideReverseFold.addSimplifiedEdge(0,4, Crease.Type.MOUNTAIN);
        allPatterns.add(insideReverseFold);
    }
    public static final SimplificationPattern outsideReverseFold = new SimplificationPattern(
            SimplificationPattern.VertexType.INTERNAL,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER);

    static {
        outsideReverseFold.addPatternEdge(0, 1, Crease.Type.MOUNTAIN);
        outsideReverseFold.addPatternEdge(0, 2, Crease.Type.MOUNTAIN);
        outsideReverseFold.addPatternEdge(0, 3, Crease.Type.MOUNTAIN);
        outsideReverseFold.addSimplifiedEdge(0, 4, Crease.Type.VALLEY);
        allPatterns.add(outsideReverseFold);
    }
    public static final SimplificationPattern swivelFold1 = new SimplificationPattern(
            SimplificationPattern.VertexType.INTERNAL,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER
    );

    static {
        swivelFold1.addPatternEdge(0, 1, Crease.Type.MOUNTAIN);
        swivelFold1.addPatternEdge(0, 2, Crease.Type.MOUNTAIN);
        swivelFold1.addPatternEdge(0, 3, Crease.Type.VALLEY);
        swivelFold1.addSimplifiedEdge(0, 4, Crease.Type.MOUNTAIN);
        allPatterns.add(swivelFold1);
    }
    public static final SimplificationPattern swivelFold2 = new SimplificationPattern(
            SimplificationPattern.VertexType.INTERNAL,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER,
            SimplificationPattern.VertexType.BORDER
    );

    static {
        swivelFold2.addPatternEdge(0, 1, Crease.Type.VALLEY);
        swivelFold2.addPatternEdge(0, 2, Crease.Type.MOUNTAIN);
        swivelFold2.addPatternEdge(0, 3, Crease.Type.MOUNTAIN);
        swivelFold2.addSimplifiedEdge(0, 4, Crease.Type.MOUNTAIN);
        allPatterns.add(swivelFold2);
    }
}
