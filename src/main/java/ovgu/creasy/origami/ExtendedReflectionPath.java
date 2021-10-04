package ovgu.creasy.origami;

import ovgu.creasy.geom.Vertex;
import ovgu.creasy.origami.basic.Crease;

import java.util.List;
import java.util.Objects;

public class ExtendedReflectionPath {
    private Vertex start;
    private Vertex end;
    List<Crease> creases;

    public ExtendedReflectionPath(Vertex start, Vertex end, List<Crease> creases) {
        this.start = start;
        this.end = end;
        this.creases = creases;
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public List<Crease> getCreases() {
        return creases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedReflectionPath that = (ExtendedReflectionPath) o;
        return Objects.equals(start, that.start)
                && Objects.equals(end, that.end)
                && Objects.equals(creases, that.creases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, creases);
    }
}
