package ovgu.creasy.origami.basic;

import java.util.Objects;

/**
 * A single step that transforms one crease pattern into a slightly
 * different one using some folding technique, equivalent to an edge in the
 * Step sequence graph
 */
public class DiagramStep {
    public DiagramStep(CreasePattern from, CreasePattern to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiagramStep that = (DiagramStep) o;
        return Objects.equals(from, that.from)
                && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    /**
     * The cp before the simplification step is applied (usually more complex than to)
     */
    public CreasePattern from;
    /**
     * the cp after the simplification step is applied (usually less complex than from)
     */
    public CreasePattern to;
}
