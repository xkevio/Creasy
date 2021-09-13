package ovgu.creasy.origami.oripa.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import oripa.domain.fold.FoldedModel;

public class EstimationResultFrame {

    private final FoldedModelScreen screen;
    // private final EstimationResultUI ui;

    @FXML
    private GridPane grid;

    public EstimationResultFrame() {
        this.screen = new FoldedModelScreen();

//        ui = new EstimationResultUI();
//        ui.setScreen(screen);
    }

    public void setModel(final FoldedModel foldedModel) {
        grid.add(screen, 1, 0);

        screen.setModel(foldedModel);
//        ui.setModel(foldedModel);
//        ui.updateIndexLabel();
    }
}
