package ovgu.creasy.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ovgu.creasy.Main;

public class CustomGridSizeWindow {

    public static void open(ResizableCanvas canvas) {
        Alert customSlider = new Alert(Alert.AlertType.CONFIRMATION);
        customSlider.setTitle("Select a cell size");
        customSlider.setHeaderText("Change grid cell size to custom size");
        ((Stage) customSlider.getDialogPane().getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);

        int oldSize = canvas.getCurrentCellSize();

        Slider slider = new Slider(0, 200, canvas.getCurrentCellSize());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        VBox wrapper = new VBox();
        HBox labelAndTextField = new HBox();

        Label label = new Label("New size: ");
        TextField currentValue = new TextField(String.valueOf(canvas.getCurrentCellSize()));
        currentValue.setPrefColumnCount(5);

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            currentValue.setText(String.valueOf(newValue.intValue()));
            canvas.drawGrid(newValue.intValue());
        });

        currentValue.setOnKeyTyped(keyEvent -> slider.setValue(Double.parseDouble(currentValue.getText())));

        labelAndTextField.getChildren().addAll(label, currentValue);
        labelAndTextField.setAlignment(Pos.CENTER);
        wrapper.getChildren().addAll(slider, labelAndTextField);

        customSlider.getDialogPane().setContent(wrapper);

        ButtonType apply = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        customSlider.getButtonTypes().setAll(apply, cancel);
        var result = customSlider.showAndWait();

        if (result.isPresent() && result.get() == apply) {
            canvas.drawGrid(Integer.parseInt(currentValue.getText()));
        } else {
            canvas.drawGrid(oldSize);
        }
    }
}
