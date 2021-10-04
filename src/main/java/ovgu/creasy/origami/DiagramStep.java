package ovgu.creasy.origami;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiagramStep that = (DiagramStep) o;
        return Objects.equals(from.toCreasePattern(), that.from.toCreasePattern())
                && Objects.equals(to.toCreasePattern(), that.to.toCreasePattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(from.toCreasePattern(), to.toCreasePattern());
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
