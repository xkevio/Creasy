package ovgu.creasy.ui.windows;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ovgu.creasy.Main;
import ovgu.creasy.ui.elements.CreasePatternCanvas;
import ovgu.creasy.util.exporter.history.PDFHistoryExporter;
import ovgu.creasy.util.exporter.history.SVGHistoryExporter;

import java.io.File;
import java.util.List;

public class ClosingWindow {

    public static void open(List<CreasePatternCanvas> history, String modelName) {
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

        Window window = closing.getDialogPane().getScene().getWindow();

        var result = closing.showAndWait();
        if (result.isPresent()) {
            switch (result.get().getButtonData()) {
                 case YES -> {
                     FileChooser fileChooser = new FileChooser();
                     fileChooser.setTitle("Save as...");
                     fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"),
                                                              new FileChooser.ExtensionFilter("Scalable Vector Graphics", "*.svg"));

                     File file = fileChooser.showSaveDialog(window);
                     if (file != null) {
                         switch (fileChooser.getSelectedExtensionFilter().getDescription()) {
                             case "PDF Document" -> new PDFHistoryExporter(history).export(file);
                             case "Scalable Vector Graphics" -> new SVGHistoryExporter(history).export(file);
                         }
                         Platform.exit();
                     } else {
                         closing.close();
                     }
                 }
                case NO -> Platform.exit();
                default -> closing.close();
            }
        }
    }
}

