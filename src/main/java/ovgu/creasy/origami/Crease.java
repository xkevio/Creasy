package ovgu.creasy.origami;

/**
 * A single crease in a Crease Pattern
 */
public class Crease {
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
}
