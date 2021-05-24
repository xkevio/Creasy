package ovgu.creasy.origami;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * A collection of steps to get to a set crease pattern
 */
public class Diagram {
    /**
     * The sequence of steps that is currently selected,
     * starting with the initial step (from the fully folded cp).
     * The contents of this list should always form a Step sequence,
     * meaning that one Step's to field should be equal to the next Step's from field
     */
    private List<DiagramStep> selectedSteps;
    /**
     * all steps that have been discovered so far
     */
    private Collection<DiagramStep> allSteps;

    public Diagram() {
        this.selectedSteps = new ArrayList<>();
        this.allSteps = new HashSet<>();
    }
}
