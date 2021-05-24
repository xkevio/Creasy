package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;

import java.util.Set;

/**
 * A collection of creases that, when folded, create an origami Model
 */
public class CreasePattern {
    /**
     * all creases in the Crease Pattern
     */
    private Set<Crease> creases;
    /**
     * all points in the Crease pattern.
     */
    private Set<Point> points;

    public CreasePattern(Set<Crease> creases, Set<Point> points) {
        this.creases = creases;
        this.points = points;
    }

    public Set<Crease> getCreases() {
        return creases;
    }

    public Set<Point> getPoints() {
        return points;
    }
}
