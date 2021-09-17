package ovgu.creasy.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import ovgu.creasy.Main;
import ovgu.creasy.util.AbstractExporter;

public class ClosingWindow {

    public static void open(String modelName, AbstractExporter exporter) {
        Alert closing = new Alert(Alert.AlertType.WARNING);

        closing.setTitle(Main.APPLICATION_TITLE);
        closing.setHeaderText("You have some unsaved changes");
        closing.setContentText("Save the instructions for " + modelName + " before closing?");
        ((Stage) closing.getDialogPane().getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        closing.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        closing.getButtonTypes().addAll(yes, no, cancel);

        closing.getDialogPane().getStylesheets().add(Main.STYLESHEET);

        var result = closing.showAndWait();
        if (result.isPresent()) {
            switch (result.get().getButtonData()) {
                // case YES -> exporter.export();
                case NO -> Platform.exit();
                default -> closing.close();
            }
        }
    }
}

