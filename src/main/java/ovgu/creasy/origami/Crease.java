package ovgu.creasy.origami;

import ovgu.creasy.geom.Line;

import java.util.Locale;
import java.util.Objects;

/**
 * A single crease in a Crease Pattern
 */
public class Crease {
    private Line line;
    private Type type;

    private boolean highlighted;

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
        EDGE;

        public Type opposite()  {
            return switch (this) {
                case VALLEY -> MOUNTAIN;
                case MOUNTAIN -> VALLEY;
                default -> null;
            };
        }

        public static Type fromString(String type) {
            return switch (type.toLowerCase()) {
                case "mountain" -> MOUNTAIN;
                case "edge" -> EDGE;
                case "valley" -> VALLEY;
                default -> null;
            };
        }
    }

    public Crease(Line line, Type type) {
        this.line = line;
        this.type = type;
        this.highlighted = false;
    }

    public Line getLine() {
        return line;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public String toString() {
        return """
               Crease
               {
               %s,
                   type: %s
               }
               """.formatted(line.toString().indent(4), type);
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
