package ovgu.creasy.ui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.origami.OrigamiModel;
import ovgu.creasy.origami.oripa.OripaFoldedModelWindow;

import java.io.File;

public class MainWindow {

    private final FileChooser openFileChooser;

    public Canvas mainCanvas;
    public MenuItem foldedModelMenuItem;
    public SplitPane splitpane;

    private OrigamiModel model;

    private String filePath = "";
    private String lastPath = "";
    private CreasePattern cp;

    @FXML
    private VBox vbox;

    public MainWindow() {
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open .cp File");
        openFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Crease Patterns", "*.cp"));
    }

    /**
     * Creates new canvases in the sidebar when necessary to show
     * the steps needed
     *
     * @param parent the VBox or HBox in which to add the Canvases to
     * @param amount the amount of Canvases needed
     * @param width  width of the Canvas (default = 263)
     * @param height height of the Canvas (default = 263)
     */
    private static void createCanvases(Parent parent, int amount, int width, int height) {
        for (int i = 0; i < amount; ++i) {
            ((Pane) parent).getChildren().add(new Canvas(width, height));
        }
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

                // should be called when the algorithm is executed, aka once the amount of steps is known
                createCanvases(vbox, 10, 263, 263);
                for (var c : vbox.getChildren()) {
                    cp.drawOnCanvas((Canvas) c, 0.5, 0.5);
                }

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
