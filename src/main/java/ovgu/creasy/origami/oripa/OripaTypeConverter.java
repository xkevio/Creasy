package ovgu.creasy.origami.oripa;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.value.OriLine;
import ovgu.creasy.geom.Point;
import ovgu.creasy.origami.Crease;
import ovgu.creasy.origami.CreasePattern;

import javax.vecmath.Vector2d;
import java.util.stream.Collectors;

public class OripaTypeConverter {
    public CreasePatternInterface convertToOripaCp(CreasePattern cp) {
        return new CreasePatternFactory().createCreasePattern(
                cp.getCreases().stream()
                        .map(this::convertToOripaOriLine)
                        .collect(Collectors.toList())
        );
    }

    public OriLine convertToOripaOriLine(Crease crease) {
        return new OriLine(
                convertToOripaVector(crease.getLine().getStart()),
                convertToOripaVector(crease.getLine().getEnd()),
                convertToOripaType(crease.getType())
        );
    }

    public OriLine.Type convertToOripaType(Crease.Type type) {
        return switch (type) {
            case MOUNTAIN -> OriLine.Type.MOUNTAIN;
            case EDGE -> OriLine.Type.CUT;
            case VALLEY -> OriLine.Type.VALLEY;
        };
    }

    public Vector2d convertToOripaVector(Point point) {
        return new Vector2d(point.getX(), point.getY());
    }
}
