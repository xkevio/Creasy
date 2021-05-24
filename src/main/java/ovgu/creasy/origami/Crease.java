package ovgu.creasy.origami;

import ovgu.creasy.geom.Line;

/**
 * A single crease in a Crease Pattern
 */
public class Crease {
    private final Line line;
    private final Type type;

    public enum Type {
        /**
         * Mountain fold, usually shown in red
         */
        MOUNTAIN,

        /**
         * Valley fold, usually shown in blue
         */
        VALLEY,

        /**
         * Edge of the paper/cut line, usually shown in black
         */
        EDGE
    }

    public Crease(Line line, Type type) {
        this.line = line;
        this.type = type;
    }

    public Line getLine() {
        return line;
    }

    public Type getType() {
        return type;
    }
}
