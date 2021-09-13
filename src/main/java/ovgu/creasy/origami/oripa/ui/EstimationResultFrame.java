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
    private OverlapRelationList overlapRelationList = null;

    @FXML
    private TextArea oripaLog;
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
        screen.setLogger(oripaLog);
        this.overlapRelationList = foldedModel.getOverlapRelationList();

        updateIndexLabel();
        TextLogger.logText("Generated all " + foldedModel.getFoldablePatternCount() + " possible models", oripaLog);
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

        TextLogger.logText("Model "
                + (overlapRelationList.getCurrentORmatIndex() + 1) + "/"
                + overlapRelationList.getFoldablePatternCount() + " loaded", oripaLog);
    }

    @FXML
    private void onNextAction() {
        overlapRelationList.setNextIndex();
        screen.redrawOrigami();
        updateIndexLabel();

        TextLogger.logText("Model "
                + (overlapRelationList.getCurrentORmatIndex() + 1) + "/"
                + overlapRelationList.getFoldablePatternCount() + " loaded", oripaLog);
    }

    @FXML
    private void onFlipAction() {
        screen.flipFaces(flip.isSelected());
        TextLogger.logText("Flip model", oripaLog);
    }

    @FXML
    private void onUseColorAction() {
        screen.setUseColor(useColor.isSelected());
        TextLogger.logText(useColor.isSelected() ? "Colors added to model" : "Colors removed from model", oripaLog);
    }

    @FXML
    private void onFillFaceAction() {
        screen.setFillFace(fillFace.isSelected());
        TextLogger.logText(fillFace.isSelected() ? "Faces of the model filled" : "Faces of the model cleared", oripaLog);
    }

    @FXML
    private void onShadeAction() {
        screen.shadeFaces(shade.isSelected());
        TextLogger.logText(shade.isSelected() ? "Shadows added to model" : "Shadows removed from model", oripaLog);
    }

    @FXML
    private void onDrawEdgeAction() {
        screen.drawEdge(drawEdge.isSelected());
        TextLogger.logText(drawEdge.isSelected() ? "Edges added to model" : "Edges removed from model", oripaLog);
    }
}
