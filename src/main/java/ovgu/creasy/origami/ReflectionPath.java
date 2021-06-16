package ovgu.creasy.origami;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReflectionPath {
    private List<Crease> creases;

    public ReflectionPath(List<Crease> creases) {
        this.creases = creases;
    }

    public List<Crease> getCreases() {
        return Collections.unmodifiableList(creases);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectionPath that = (ReflectionPath) o;
        return Objects.equals(creases, that.creases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creases);
    }

    @Override
    public String toString() {
        return "ReflectionPath{" +
                "creases=" + creases +
                '}';
    }
}
