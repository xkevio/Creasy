package ovgu.creasy.ui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.stage.FileChooser;
import ovgu.creasy.origami.OrigamiModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow  {
    public Canvas mainCanvas;
    private OrigamiModel model;

    private final FileChooser openFileChooser;
    private String filePath = "";
    private String lastPath = "";

    public MainWindow() {
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open .cp File");
        openFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Crease Patterns", "*.cp"));
    }

    @FXML
    public void onMenuImportAction() {
        File file = openFileChooser.showOpenDialog(mainCanvas.getScene().getWindow());
        if (file.exists()) {
            // TODO read file and check if its a valid .cp
            // Perhaps make a static createFromFile(File file) method in CreasePattern
        } else {
            System.err.println("No file selected!");
        }
    }

    // probably not needed
    public static void write(String lastPath) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("lastPath.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(lastPath + "\n");
            bw.close();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void initialize() {}
}
