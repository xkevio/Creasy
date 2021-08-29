package ovgu.creasy.origami;

import java.util.Collection;
import java.util.HashSet;

/**
 * A single step that transforms one crease pattern into a slightly
 * different one using some folding technique, equivalent to an edge in the
 * Step sequence graph
 */
public class DiagramStep {
    public DiagramStep(ExtendedCreasePattern from, ExtendedCreasePattern to) {
        this.from = from;
        this.to = to;
    }



    /**
     * The cp before the simplification step is applied (usually more complex than to)
     */
    public ExtendedCreasePattern from;
    /**
     * the cp after the simplification step is applied (usually less complex than from)
     */
    public ExtendedCreasePattern to;
}
