package ovgu.creasy.origami.oripa.ui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.OverlapRelationList;
import ovgu.creasy.util.TextLogger;

public class EstimationResultFrame {

    private final FoldedModelScreen screen;
    public TextArea oripaLog;
    private OverlapRelationList overlapRelationList = null;

    @FXML
    private Label indexLabel;
    @FXML
    private CheckBox flip;
    @FXML
    private CheckBox useColor;
    @FXML
    private CheckBox fillFace;
    @FXML
    private CheckBox shade;
    @FXML
    private CheckBox drawEdge;
    @FXML
    private GridPane grid;

    public EstimationResultFrame() {
        this.screen = new FoldedModelScreen();
    }

    public void setModel(final FoldedModel foldedModel) {
        grid.add(screen, 1, 0);

        screen.setModel(foldedModel);
        this.overlapRelationList = foldedModel.getOverlapRelationList();

        updateIndexLabel();
        TextLogger.logText("Generated all the possible models...", oripaLog);
    }

    private void updateIndexLabel() {
        if (overlapRelationList == null) {
            return;
        }

        indexLabel.setText("Folded model ["
                + (overlapRelationList.getCurrentORmatIndex() + 1) + "/"
                + overlapRelationList.getFoldablePatternCount() + "]");
    }

    @FXML
    private void onPrevAction() {
        overlapRelationList.setPrevIndex();
        screen.redrawOrigami();
        updateIndexLabel();

        TextLogger.logText("Load previous model...", oripaLog);
    }

    @FXML
    private void onNextAction() {
        overlapRelationList.setNextIndex();
        screen.redrawOrigami();
        updateIndexLabel();

        TextLogger.logText("Load next model...", oripaLog);
    }

    @FXML
    private void onFlipAction() {
        screen.flipFaces(flip.isSelected());

        TextLogger.logText("Select flip action", oripaLog);
    }

    @FXML
    private void onUseColorAction() {
        screen.setUseColor(useColor.isSelected());

        TextLogger.logText("Coloring the model...", oripaLog);
    }

    @FXML
    private void onFillFaceAction() {
        screen.setFillFace(fillFace.isSelected());

        TextLogger.logText("Filling the faces of the model...", oripaLog);
    }

    @FXML
    private void onShadeAction() {
        screen.shadeFaces(shade.isSelected());

        TextLogger.logText("Activating shadows...", oripaLog);
    }

    @FXML
    private void onDrawEdgeAction() {
        screen.drawEdge(drawEdge.isSelected());

        TextLogger.logText("Drawing the edges...", oripaLog);
    }
}
