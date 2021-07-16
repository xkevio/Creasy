package ovgu.creasy.ui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.origami.OrigamiModel;
import ovgu.creasy.origami.oripa.OripaFoldedModelWindow;

import java.io.File;

public class MainWindow {

    private final FileChooser openFileChooser;

    public ResizableCanvas mainCanvas;
    public MenuItem foldedModelMenuItem;
    public MenuItem zoomInMenuItem;
    public MenuItem zoomOutMenuItem;

    private OrigamiModel model;

    private String filePath = "";
    private String lastPath = "";
    private CreasePattern cp;

    @FXML
    private VBox vbox;
    @FXML
    private VBox canvasVBox;
    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane canvasGrid;

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
     * @param width  width of the Canvas
     * @param height height of the Canvas
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
                createCanvases(vbox, 10, 250, 250);
                for (var c : vbox.getChildren()) {
                    cp.drawOnCanvas((Canvas) c, 0.5, 0.5);
                }

                // after reading the file, if the file is valid:
                foldedModelMenuItem.setDisable(false);
                zoomInMenuItem.setDisable(false);
                zoomOutMenuItem.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading file " + filePath + "!");
            }

        } else {
            System.err.println("No file selected or path is invalid!");
        }
    }

    @FXML
    public void onShowFoldedModelAction() throws Exception {
        if (model == null) {
            Alert error = new Alert(Alert.AlertType.ERROR, "There is no model to fold, perhaps it wasn't loaded correctly", ButtonType.OK);
            error.setTitle("Model is not loaded");
            error.setHeaderText("Error while folding model");
            error.showAndWait();

            // System.err.println("No Model to fold");
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

    @FXML
    public void onZoomInMenuItem() {
        cp.drawOnCanvas(mainCanvas, mainCanvas.getScaleX() * 1.1, mainCanvas.getScaleY() * 1.1);
    }

    @FXML
    public void onZoomOutMenuItem() {
        cp.drawOnCanvas(mainCanvas, mainCanvas.getScaleX() * 0.9, mainCanvas.getScaleY() * 0.9);
    }

    @FXML
    public void initialize() {
        mainCanvas = new ResizableCanvas(canvasVBox.getWidth(), canvasVBox.getHeight());
        mainCanvas.setManaged(false);
        canvasVBox.getChildren().add(mainCanvas);

        mainCanvas.widthProperty().bind(canvasVBox.widthProperty());
        mainCanvas.heightProperty().bind(canvasVBox.heightProperty());

        mainCanvas.widthProperty().addListener((observableValue, number, t1) -> {
            if (cp != null) {
                cp.drawOnCanvas(mainCanvas, 1, 1);
            }
        });

        mainCanvas.heightProperty().addListener((observableValue, number, t1) -> {
            if (cp != null) {
                cp.drawOnCanvas(mainCanvas, 1, 1);
            }
        });
    }
}
