package ovgu.creasy.origami.oripa.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oripa.application.estimation.EstimationResultFileAccess;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.OverlapRelationList;
import oripa.persistent.entity.FoldedModelDAO;
import oripa.persistent.entity.FoldedModelFilterSelector;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

public class EstimationResultUI extends Pane {

    private static final Logger logger = LoggerFactory.getLogger(EstimationResultUI.class);
    private static final long serialVersionUID = 1L;

    private Button jButtonNextAnswer = null;
    private Button jButtonPrevAnswer = null;
    private CheckBox jCheckBoxOrder = null;
    private CheckBox jCheckBoxShadow = null;
    private Label indexLabel = null;
    private FoldedModelScreen screen;
    private CheckBox jCheckBoxUseColor = null;
    private CheckBox jCheckBoxEdge = null;
    private CheckBox jCheckBoxFillFace = null;
    private Button jButtonExport = null;

    private final ResourceHolder resources = ResourceHolder.getInstance();
    private String lastFilePath = null;

    public EstimationResultUI() {
        initialize();
    }

    public void setScreen(final FoldedModelScreen s) {
        screen = s;
    }

    private void initialize() {
        indexLabel = new Label();
        // indexLabel.setBounds(new Rectangle(15, 45, 181, 16));
        // this.setLayout(null);
        this.setPrefSize(216, 256);
        this.getChildren().addAll(
                getJButtonPrevAnswer(),
                getJCheckBoxOrder(),
                getJButtonNextAnswer(),
                getJCheckBoxShadow(),
                indexLabel,
                getJCheckBoxUseColor(),
                getJCheckBoxEdge(),
                getJCheckBoxFillFace(),
                getJButtonExport()
        );
        updateIndexLabel();
    }

    private FoldedModel foldedModel;
    private OverlapRelationList overlapRelationList = null;

    public void setModel(final FoldedModel foldedModel) {
        this.foldedModel = foldedModel;
        this.overlapRelationList = foldedModel.getOverlapRelationList();
    }

    public void updateIndexLabel() {

        if (overlapRelationList == null) {
            return;
        }

        indexLabel.setText("Folded model ["
                + (overlapRelationList.getCurrentORmatIndex() + 1) + "/"
                + overlapRelationList.getFoldablePatternCount() + "]");

    }

    /**
     * This method initializes jButtonNextAnswer
     *
     * @return Button
     */
    private Button getJButtonNextAnswer() {
        if (jButtonNextAnswer != null) {
            return jButtonNextAnswer;
        }

        jButtonNextAnswer = new Button();
        jButtonNextAnswer.setText("Next");
        // jButtonNextAnswer.setBounds(new Rectangle(109, 4, 87, 27));

        jButtonNextAnswer.setOnAction(e -> {
            overlapRelationList.setNextIndex();
            screen.redrawOrigami();
            updateIndexLabel();
        });

        return jButtonNextAnswer;
    }

    /**
     * This method initializes jButtonPrevAnswer
     *
     * @return Button
     */
    private Button getJButtonPrevAnswer() {
        if (jButtonPrevAnswer != null) {
            return jButtonPrevAnswer;
        }

        jButtonPrevAnswer = new Button();
        jButtonPrevAnswer.setText("Prev");
        // jButtonPrevAnswer.setBounds(new Rectangle(15, 4, 89, 27));

        jButtonPrevAnswer.setOnAction(e -> {
            overlapRelationList.setPrevIndex();
            screen.redrawOrigami();
            updateIndexLabel();
        });

        return jButtonPrevAnswer;
    }

    /**
     * This method initializes jCheckBoxOrder
     *
     * @return javax.swing.JCheckBox
     */
    private CheckBox getJCheckBoxOrder() {
        if (jCheckBoxOrder != null) {
            return jCheckBoxOrder;
        }

        jCheckBoxOrder = new CheckBox();
        // jCheckBoxOrder.setBounds(new Rectangle(15, 75, 91, 31));
        jCheckBoxOrder.setText("Flip");
        jCheckBoxOrder.setOnAction(e -> screen.flipFaces(jCheckBoxOrder.isSelected()));

        return jCheckBoxOrder;
    }

    /**
     * This method initializes jCheckBoxShadow
     *
     * @return javax.swing.JCheckBox
     */
    private CheckBox getJCheckBoxShadow() {
        if (jCheckBoxShadow != null) {
            return jCheckBoxShadow;
        }

        jCheckBoxShadow = new CheckBox();
        // jCheckBoxShadow.setBounds(new Rectangle(105, 75, 80, 31));
        jCheckBoxShadow.setText("Shade");
        jCheckBoxShadow.setOnAction(e -> screen.shadeFaces(jCheckBoxShadow.isSelected()));

        return jCheckBoxShadow;
    }

    /**
     * This method initializes jCheckBoxUseColor
     *
     * @return javax.swing.JCheckBox
     */
    private CheckBox getJCheckBoxUseColor() {
        if (jCheckBoxUseColor != null) {
            return jCheckBoxUseColor;
        }

        jCheckBoxUseColor = new CheckBox();
        // jCheckBoxUseColor.setBounds(new Rectangle(15, 120, 80, 31));
        jCheckBoxUseColor.setSelected(true);
        jCheckBoxUseColor.setText("Use Color");

        jCheckBoxUseColor.setOnAction(e -> screen.setUseColor(jCheckBoxUseColor.isSelected()));

        return jCheckBoxUseColor;
    }

    /**
     * This method initializes jCheckBoxEdge
     *
     * @return javax.swing.JCheckBox
     */
    private CheckBox getJCheckBoxEdge() {
        if (jCheckBoxEdge != null) {
            return jCheckBoxEdge;
        }

        jCheckBoxEdge = new CheckBox();
        // jCheckBoxEdge.setBounds(new Rectangle(105, 120, 93, 31));
        jCheckBoxEdge.setSelected(true);
        jCheckBoxEdge.setText("Draw Edge");

        jCheckBoxEdge.setOnAction(e -> screen.drawEdge(jCheckBoxEdge.isSelected()));

        return jCheckBoxEdge;

    }

    /**
     * This method initializes jCheckBoxFillFace
     *
     * @return javax.swing.JCheckBox
     */
    private CheckBox getJCheckBoxFillFace() {
        if (jCheckBoxFillFace != null) {
            return jCheckBoxFillFace;
        }

        jCheckBoxFillFace = new CheckBox();
        // jCheckBoxFillFace.setBounds(new Rectangle(15, 165, 93, 21));
        jCheckBoxFillFace.setSelected(true);
        jCheckBoxFillFace.setText("FillFace");

        jCheckBoxFillFace.setOnAction(e -> screen.setFillFace(jCheckBoxFillFace.isSelected()));

        return jCheckBoxFillFace;
    }

    /**
     * This method initializes jButtonExport
     *
     * @return javax.swing.JButton
     */
    private Button getJButtonExport() {
        if (jButtonExport != null) {
            return jButtonExport;
        }

        jButtonExport = new Button();
        // jButtonExport.setBounds(new Rectangle(15, 206, 92, 26));
        jButtonExport.setText("Export");
        jButtonExport.setOnAction(e -> export());

        return jButtonExport;
    }

    private void export() {
        try {
            var filterSelector = new FoldedModelFilterSelector(screen.isFaceOrderFlipped());
            final FoldedModelDAO dao = new FoldedModelDAO(filterSelector);
            EstimationResultFileAccess fileAccess = new EstimationResultFileAccess(dao);
//            lastFilePath = fileAccess.saveFile(foldedModel, lastFilePath, this,
//                    filterSelector.getSavables());
        } catch (Exception ex) {
            logger.error("error: ", ex);

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle(resources.getString(ResourceKey.ERROR, StringID.Error.SAVE_FAILED_ID));
            error.setHeaderText("An error has occurred");
            error.setContentText(ex.getMessage());

            error.showAndWait();
        }
    }

}
