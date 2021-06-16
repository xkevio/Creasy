package ovgu.creasy.origami;

import ovgu.creasy.geom.*;
import ovgu.creasy.origami.*;

import java.util.*;

/**
 * Extended Crease Pattern, adds information about Reflection Creases
 * to a crease Pattern and is used for the step generation
 * Explained in chapter 4.2 of the paper (pages 28 - 36)
 */
public class ExtendedCreasePattern {
    private Set<Vertices> xV;
    private Set<ExtendedCrease> xC;
    private Set<List> xL;

    /**
     * @param xV is the set of extended vertices
     * @param xC is the set of directed edges of the extended graph. Each xC ist called extended creases.
     * @param xL is a set of ordered, circular lists of edges parting from each vertex xV
     */
    public ExtendedCreasePattern(Set<Vertices> xV, Set<ExtendedCrease> xC, Set<List> xL) {
        this.xV = xV;
        this.xC = xC;
        this.xL = xL;
    }

    public ExtendedCreasePattern() {
    }

    public ExtendedCreasePattern buildExtendedGraph(CreasePattern cp) {
        ExtendedCreasePattern xCP = new ExtendedCreasePattern();
        ReflectionGraph reflGraph = new ReflectionGraph(cp);
        /**
         * TODO:
         * copy each Vertex (from given CP) to extended Vertex
         * From each Crease (from given CP) construct xC1 & xC2 with default active=false
         * construct the set of lists xL so that it reflects the same topology as in the given CP
         * initialize set of all reflection graphs
         */

        return xCP;
    }
}
