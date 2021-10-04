package ovgu.creasy.ui.windows;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ovgu.creasy.Main;
import ovgu.creasy.ui.elements.ResizableCanvas;

public class CustomGridSizeWindow {

    public static void open(ResizableCanvas.Grid grid) {
        Alert customSlider = new Alert(Alert.AlertType.CONFIRMATION);
        customSlider.setTitle("Select a cell size");
        customSlider.setHeaderText("Change grid cell size to custom size");
        ((Stage) customSlider.getDialogPane().getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);

        int oldSize = grid.getCurrentCellSize();

        Slider slider = new Slider(0, 200, grid.getCurrentCellSize());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        VBox wrapper = new VBox();
        HBox labelAndTextField = new HBox();

        Label label = new Label("New size: ");
        Spinner<Integer> cV = new Spinner<>(0, 200, grid.getCurrentCellSize());
        cV.setEditable(true);
        cV.getEditor().setPrefColumnCount(4);

        slider.valueProperty().addListener((o, oV, newValue) -> {
            cV.getEditor().setText(String.valueOf(newValue.intValue()));
            cV.commitValue();
            grid.drawGrid(newValue.intValue());
        });

        cV.valueProperty().addListener((o, i, nV) -> {
            slider.setValue(cV.getValue());
            cV.commitValue();
        });

        cV.getEditor().textProperty().addListener((o, i, nV) -> {
            slider.setValue(Double.parseDouble(nV));
            cV.commitValue();
        });

        labelAndTextField.getChildren().addAll(label, cV);
        labelAndTextField.setAlignment(Pos.CENTER);
        wrapper.getChildren().addAll(slider, labelAndTextField);

        customSlider.getDialogPane().setContent(wrapper);

        ButtonType apply = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        customSlider.getButtonTypes().setAll(apply, cancel);
        customSlider.getDialogPane().getStylesheets().add(Main.STYLESHEET);
        var result = customSlider.showAndWait();

        if (result.isPresent() && result.get() == apply) {
            grid.drawGrid(cV.getValue());
        } else {
            grid.drawGrid(oldSize);
        }
    }
}
