package ovgu.creasy.ui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ovgu.creasy.Main;
import ovgu.creasy.origami.*;
import ovgu.creasy.origami.oripa.OripaFoldedModelWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainWindow {

    private static final int CANVAS_WIDTH = 200;
    private static final int CANVAS_HEIGHT = 200;

    private final FileChooser openFileChooser;
    public ScrollPane canvasHolder;
    private HostServices hostServices;

    public ResizableCanvas mainCanvas;

    public MenuItem foldedModelMenuItem;
    public MenuItem zoomInMenuItem;
    public MenuItem zoomOutMenuItem;
    public MenuItem resetMenuItem;

    private OrigamiModel model;
    private CreasePattern cp;

    @FXML
    private HBox history;
    @FXML
    private VBox steps;

    public MainWindow() {
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open .cp File");
        openFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Crease Patterns", "*.cp"));
    }

    /* This makes sure that the window scales correctly
       by binding the canvas to the window size and redrawing if
       necessary.
     */
    @FXML
    public void initialize() {
        mainCanvas = new ResizableCanvas(canvasHolder.getWidth(), canvasHolder.getHeight());
        mainCanvas.setManaged(false);
        canvasHolder.setContent(mainCanvas);

        mainCanvas.widthProperty().bind(canvasHolder.widthProperty());
        mainCanvas.heightProperty().bind(canvasHolder.heightProperty());

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

    /**
     * Opens a file explorer dialogue which lets the user select
     * .cp files and upon loading them, calls setupGUI() -- drawing the
     * Pattern to the screen.
     */
    @FXML
    public void onMenuImportAction() {
        File file = openFileChooser.showOpenDialog(mainCanvas.getScene().getWindow());
        var filePath = file == null ? "" : file.getPath();

        if (file != null && file.exists()) {
            try {
                setupCreasePattern(new FileInputStream(file), filePath);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading file " + filePath + "!");
            }
        } else {
            System.err.println("No file selected or path is invalid!");
        }
    }

    /**
     * Opens Oripa with the folded 3d model,
     * calls foldModel() method.
     *
     * Opens an Alert in case of an error while folding the model.
     */
    @FXML
    public void onShowFoldedModelAction() {
        if (model == null) {
            Alert error = new Alert(Alert.AlertType.ERROR,
                    "There is no model to fold, perhaps it wasn't loaded correctly", ButtonType.OK);
            error.setTitle("Model is not loaded");
            error.setHeaderText("Error while folding model");
            error.showAndWait();
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
        cp.drawOnCanvas(mainCanvas, 1.1 * cp.getScaleX(), 1.1 * cp.getScaleY());
    }

    @FXML
    public void onZoomOutMenuItem() {
        cp.drawOnCanvas(mainCanvas, 0.9 * cp.getScaleX(), 0.9 * cp.getScaleY());
    }

    @FXML
    public void onMenuResetAction() {
        resetGUI();
    }

    // -------------------------
    // Loading example files
    @FXML
    public void onLoadExampleBird() {
        InputStream is = Main.class.getResourceAsStream("example/bird.cp");
        setupCreasePattern(is, "example/bird.cp");
    }

    @FXML
    public void onLoadExamplePenguin() {
        InputStream is = Main.class.getResourceAsStream("example/penguin_hideo_komatsu.cp");
        setupCreasePattern(is, "example/penguin_hideo_komatsu.cp");
    }

    @FXML
    public void onLoadExampleCrane() {
        InputStream is = Main.class.getResourceAsStream("example/crane.cp");
        setupCreasePattern(is, "example/crane.cp");
    }
    // -------------------------

    /**
     * Opens an "about" dialogue which displays information about Creasy
     * and its developers
     * @throws IOException when the fxml file does not exist
     */
    @FXML
    public void onHelpAbout() throws IOException {
        Stage stage = new Stage();

        FXMLLoader aboutWindow = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("about.fxml")));
        Scene initialScene = new Scene(aboutWindow.load());
        stage.setResizable(false);

        ((AboutWindow) aboutWindow.getController()).setHostServices(hostServices);

        stage.setScene(initialScene);
        stage.sizeToScene();
        stage.setTitle("About Creasy");
        stage.show();
    }

    @FXML
    public void onHelpCP() throws IOException {
        Stage stage = new Stage();

        FXMLLoader creasePatternWindow = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("crease_patterns.fxml")));
        Scene initialScene = new Scene(creasePatternWindow.load());
        stage.setResizable(false);

        ((CreasePatternHelpWindow) creasePatternWindow.getController()).setHostServices(hostServices);

        stage.setScene(initialScene);
        stage.sizeToScene();
        stage.setTitle("What are Crease Patterns");
        stage.show();
    }

    /**
     * Loads a Crease Pattern, displays it on the canvases and
     * initializes variables
     * @param is the InputStream that is the crease pattern file
     * @param filePath what is displayed in the title bar of the window
     */
    private void setupCreasePattern(InputStream is, String filePath) {
        cp = CreasePattern.createFromFile(is);
        ((Stage) mainCanvas.getScene().getWindow()).setTitle(filePath + " - " + Main.APPLICATION_TITLE);

        model = new OrigamiModel(cp);

        cp.drawOnCanvas(mainCanvas, 1, 1);

        ExtendedCreasePattern ecp = new ExtendedCreasePatternFactory().createExtendedCreasePattern(cp);
        System.out.println(ecp.possibleSteps().size());
        // should be called when the algorithm is executed, aka once the amount of steps is known
        createCanvases(steps, ecp.possibleSteps().size(), CANVAS_WIDTH, CANVAS_HEIGHT);
        createCanvases(history, 10, CANVAS_WIDTH, CANVAS_HEIGHT);

        setupMouseEvents(steps, history);
        drawSteps(ecp, steps);
        drawHistory(history);

        // after reading the file, if the file is valid:
        foldedModelMenuItem.setDisable(false);
        zoomInMenuItem.setDisable(false);
        zoomOutMenuItem.setDisable(false);
        resetMenuItem.setDisable(false);
    }

    private void drawSteps(ExtendedCreasePattern ecp, Parent steps) {
        for (int i = 0; i < ecp.possibleSteps().size(); i++) {
            DiagramStep step = ecp.possibleSteps().get(i);
            step.to.toCreasePattern().drawOnCanvas((ResizableCanvas) ((Pane) steps).getChildren().get(i),
                    0.45, 0.45);
        }
    }

    private void drawHistory(Parent history) {
        var copy = new CreasePattern(cp.getCreases(), cp.getPoints());
        ((Pane) history).getChildren().forEach(c -> {
            copy.drawOnCanvas((ResizableCanvas) c, 0.45, 0.45);
        });
    }

    private void setupMouseEvents(Parent... parents) {
        for (Parent parent : parents) {
            ((Pane) parent).getChildren().forEach(c -> {
                GraphicsContext graphicsContext = ((Canvas) c).getGraphicsContext2D();
                c.setOnMouseEntered(mouseEvent -> {
                    graphicsContext.setFill(Color.color(0.2, 0.2, 0.2, 0.2));
                    graphicsContext.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                    c.setCursor(Cursor.HAND);
                });

                c.setOnMouseExited(mouseEvent -> {
                    graphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                    ((ResizableCanvas)c).getCp().drawOnCanvas((ResizableCanvas) c, 0.45, 0.45);
                    c.setCursor(Cursor.DEFAULT);
                });
            });
        }
    }

    /**
     * Clears all the canvases and disables menu options that would only work
     * with a loaded file.
     * Useful for resetting state.
     */
    private void resetGUI() {
        mainCanvas.getGraphicsContext2D().clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
        ((Stage) mainCanvas.getScene().getWindow()).setTitle(Main.APPLICATION_TITLE);

        steps.getChildren().forEach(c -> ((Canvas) c).getGraphicsContext2D().
                clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT));

        history.getChildren().forEach(c -> ((Canvas) c).getGraphicsContext2D().
                clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT));

        steps.getChildren().clear();
        history.getChildren().clear();

        foldedModelMenuItem.setDisable(true);
        zoomInMenuItem.setDisable(true);
        zoomOutMenuItem.setDisable(true);
        resetMenuItem.setDisable(true);

        cp = null;
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
    private void createCanvases(Parent parent, int amount, int width, int height) {
        if (((Pane) parent).getChildren().isEmpty()) {
            for (int i = 0; i < amount; ++i) {
                ((Pane) parent).getChildren().add(new ResizableCanvas(width, height));
            }
        }
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
