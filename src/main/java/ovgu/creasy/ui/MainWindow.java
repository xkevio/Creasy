package ovgu.creasy.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import ovgu.creasy.origami.OrigamiModel;

public class MainWindow {
    public Canvas mainCanvas;
    private OrigamiModel model;

    @FXML
    public void onMenuImportAction(ActionEvent actionEvent) {
        System.out.println("Import");
        model = new OrigamiModel();
    }

    @FXML
    private void initialize() {

    }
}
