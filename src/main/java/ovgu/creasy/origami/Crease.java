package ovgu.creasy.origami;

import ovgu.creasy.geom.Line;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Crease{" +
                "line=" + line +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crease crease = (Crease) o;
        return Objects.equals(line, crease.line) && type == crease.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, type);
    }
}
