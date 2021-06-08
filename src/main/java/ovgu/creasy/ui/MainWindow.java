package ovgu.creasy.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.origami.OrigamiModel;
import ovgu.creasy.origami.oripa.OripaFoldedModelWindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow {
    public Canvas mainCanvas;
    public MenuItem foldedModelMenuItem;
    private OrigamiModel model;

    private final FileChooser openFileChooser;
    private String filePath = "";
    private String lastPath = "";
    private CreasePattern cp;

    public MainWindow() {
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open .cp File");
        openFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Crease Patterns", "*.cp"));
    }

    @FXML
    public void onMenuImportAction() {
        File file = openFileChooser.showOpenDialog(mainCanvas.getScene().getWindow());
        filePath = file.getPath();

        if (file.exists()) {
            try {
                cp = CreasePattern.createFromFile(file);
                ((Stage) mainCanvas.getScene().getWindow()).setTitle(filePath + " - Creasy");

                System.out.println(cp.getPoints());
                System.out.println(cp.getCreases());
                model = new OrigamiModel(cp);

                cp.drawOnCanvas(mainCanvas);

                // after reading the file, if the file is valid:
                foldedModelMenuItem.setDisable(false);
            } catch (Exception e) {
                System.err.println("Error loading File!");
            }

        } else {
            System.err.println("No file selected or path is invalid!");
        }
    }


    // probably not needed
    // writes last filepath to a .txt
    public static void write(String lastPath) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("lastPath.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(lastPath + "\n");
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
    }

    public void onShowFoldedModelAction(ActionEvent actionEvent) {
        if (model == null) {
            System.err.println("No Model to fold");
        } else {
            OripaFoldedModelWindow foldedModelWindow = new OripaFoldedModelWindow(model.getFinishedCp());
            if (foldedModelWindow.foldModel()) {
                foldedModelWindow.show();
            } else {
                foldedModelWindow.showError();
                System.err.println("Crease Pattern is invalid");
            }
        }
    }
}
