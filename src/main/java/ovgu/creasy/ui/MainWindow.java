package ovgu.creasy.ui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
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
    public SplitPane splitpane;

    private OrigamiModel model;

    private final FileChooser openFileChooser;
    private String filePath = "";
    private String lastPath = "";
    private CreasePattern cp;

    @FXML
    private Canvas canvas1;
    @FXML
    private Canvas canvas2;
    @FXML
    private Canvas canvas3;

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

                cp.drawOnCanvas(mainCanvas, 1, 1);
                cp.drawOnCanvas(canvas1, 0.5, 0.5);
                cp.drawOnCanvas(canvas2, 0.5, 0.5);
                cp.drawOnCanvas(canvas3, 0.5, 0.5);

                // after reading the file, if the file is valid:
                foldedModelMenuItem.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading file " + filePath + "!");
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
    public void initialize() {
        SplitPane.Divider divider = splitpane.getDividers().get(0);
        divider.positionProperty().addListener((observable, oldvalue, newvalue) -> divider.setPosition(0.65));
    }

    @FXML
    public void onShowFoldedModelAction() {
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
