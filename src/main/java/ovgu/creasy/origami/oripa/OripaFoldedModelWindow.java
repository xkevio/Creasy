package ovgu.creasy.origami.oripa;

import oripa.domain.cptool.LineAdder;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.Folder;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.domain.fold.subface.FacesToCreasePatternConverter;
import oripa.domain.fold.subface.ParentFacesCollector;
import oripa.domain.fold.subface.SplitFacesToSubFacesConverter;
import oripa.domain.fold.subface.SubFacesFactory;
import oripa.view.estimation.EstimationResultFrame;
import ovgu.creasy.origami.CreasePattern;

import javax.swing.*;

public class OripaFoldedModelWindow {
    private CreasePatternInterface cp;
    private OrigamiModel model;
    private FoldedModel foldedModel;
    // TODO: modify to work with JavaFX
    private JFrame window;
    private FoldabilityChecker foldabilityChecker;
    private Folder folder;

    public OripaFoldedModelWindow(CreasePattern cp) {
        this.cp = OripaTypeConverter.convertToOripaCp(cp);
        window = new EstimationResultFrame();
        folder = new Folder(
                new SubFacesFactory(
                        new FacesToCreasePatternConverter(
                                new CreasePatternFactory(),
                                new LineAdder()),
                        new OrigamiModelFactory(),
                        new SplitFacesToSubFacesConverter(),
                        new ParentFacesCollector()));
        foldabilityChecker = new FoldabilityChecker();
    }

    /**
     * Tries to use Oripa to determine the folded Model. Returns whether
     * it succeeded (= if the crease Pattern was globally flatfoldable)
     * @return true if folding succeeded, false otherwise
     */
    public boolean foldModel() {
        model = new OrigamiModelFactory().createOrigamiModel(cp, cp.getPaperSize());
        if (foldabilityChecker.testLocalFlatFoldability(model)) {
            foldedModel = folder.fold(model, true);
            int solutionNum = foldedModel.getFoldablePatternCount();
            if (solutionNum < 1) return false;
        }
        return false;
    }

    /**
     * shows Oripa's Folded Model Frame. Should only be called
     * after foldModel() has been called and succeeded (returned true)
     */
    public void show() {
        EstimationResultFrame frame = new EstimationResultFrame();
        frame.setModel(foldedModel);
        if (window != null) {
            window.dispose();
        }
        frame.repaint();
        frame.setVisible(true);
        window = frame;
    }
}
