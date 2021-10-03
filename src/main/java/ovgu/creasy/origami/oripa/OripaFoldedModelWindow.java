package ovgu.creasy.origami.oripa;

import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.Folder;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.util.gui.ChildFrameManager;
import oripa.view.foldability.FoldabilityCheckFrameFactory;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.origami.oripa.ui.EstimationResultLauncher;

import javax.swing.*;
import java.io.IOException;

public class OripaFoldedModelWindow {

    private final CreasePatternInterface cp;
    private OrigamiModel model;
    private FoldedModel foldedModel;

    private final FoldabilityChecker foldabilityChecker;
    private final Folder folder;

    public OripaFoldedModelWindow(CreasePattern cp) {
        this.cp = OripaTypeConverter.convertToOripaCp(cp);
        folder = OripaTypeConverter.createFolder();
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
    public void show() throws IOException {
        EstimationResultLauncher frame = new EstimationResultLauncher();
        frame.setModel(foldedModel);
        frame.show();
    }

    public void showError() {
        JFrame f = new FoldabilityCheckFrameFactory(new ChildFrameManager()).createFrame(null, model, cp, true);

        Stage stage = new Stage();

        SwingNode node = new SwingNode();
        node.setContent(f.getRootPane());

        stage.setScene(new Scene(new StackPane(node), 800, 800));
        stage.show();
    }
}
