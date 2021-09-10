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
import oripa.util.gui.ChildFrameManager;
import oripa.view.estimation.EstimationResultFrame;
import oripa.view.foldability.FoldabilityCheckFrameFactory;
import ovgu.creasy.origami.CreasePattern;

import javax.swing.*;

public class OripaFoldedModelWindow {

    private final CreasePatternInterface cp;
    private OrigamiModel model;
    private FoldedModel foldedModel;

    // TODO: modify to work with JavaFX
    private JFrame window;
    private final FoldabilityChecker foldabilityChecker;
    private final Folder folder;

    public OripaFoldedModelWindow(CreasePattern cp) {
        // temporarily disabled as it does not work on certain Linux distros
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
            return solutionNum >= 1;
        }
        return false;
    }

    /**
     * shows Oripa's Folded Model Frame. Should only be called
     * after foldModel() has been called and succeeded (returned true)
     */
    public void show() {
        EstimationResultFrame frame = new EstimationResultFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setModel(foldedModel);
        if (window != null) {
            window.dispose();
        }
        frame.repaint();
        frame.setVisible(true);
        window = frame;
    }

    // Temporary, useful for debugging
    public void showError() {
        JFrame f = new FoldabilityCheckFrameFactory(new ChildFrameManager()).createFrame(null, model, cp, true);
        f.setVisible(true);
    }
}
