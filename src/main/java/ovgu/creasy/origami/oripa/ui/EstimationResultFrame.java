package ovgu.creasy.origami.oripa.ui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jfree.svg.SVGGraphics2D;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.OverlapRelationList;
import oripa.domain.fold.halfedge.OriVertex;
import ovgu.creasy.util.TextLogger;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    private VBox oripaCanvasHolder;

    public EstimationResultFrame() {
        this.screen = new FoldedModelScreen();
    }

    public void setModel(final FoldedModel foldedModel) {
        oripaCanvasHolder.getChildren().add(screen);
        oripaCanvasHolder.toBack();

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

    @FXML
    private void onExportOripaAction() {
        FileChooser exportSVG = new FileChooser();
        exportSVG.setTitle("Save as .svg");
        exportSVG.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scalable Vector Graphics", "*.svg"));

        File file = exportSVG.showSaveDialog(screen.getScene().getWindow());

        if (file != null) {
            SVGGraphics2D svgGraphics2D = new SVGGraphics2D(1000, 1000);
            svgGraphics2D.setColor(Color.BLUE);
            svgGraphics2D.translate(500, 500);

            screen.getOrigamiModel().getEdges().forEach(oriEdge -> {
                OriVertex start = oriEdge.getStartVertex();
                OriVertex end = oriEdge.getEndVertex();

                svgGraphics2D.draw(new Line2D.Double(start.getPosition().x, start.getPosition().y,
                        end.getPosition().x, end.getPosition().y));
            });

            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(svgGraphics2D.getSVGDocument());
                fileWriter.close();

                TextLogger.logText("Saved " + file.getName() + " successfully", oripaLog);
            } catch (IOException e) {
                e.printStackTrace();
                TextLogger.logText("Error while saving " + file.getName(), oripaLog);
            }
        }
    }
}
