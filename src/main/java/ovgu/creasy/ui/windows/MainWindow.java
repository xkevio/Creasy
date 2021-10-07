package ovgu.creasy.ui.windows;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ovgu.creasy.Main;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;
import ovgu.creasy.origami.ExtendedCreasePattern;
import ovgu.creasy.origami.ExtendedCreasePatternFactory;
import ovgu.creasy.origami.basic.Crease;
import ovgu.creasy.origami.basic.CreasePattern;
import ovgu.creasy.origami.basic.DiagramStep;
import ovgu.creasy.origami.basic.OrigamiModel;
import ovgu.creasy.origami.oripa.OripaFoldedModelWindow;
import ovgu.creasy.ui.elements.CreasePatternCanvas;
import ovgu.creasy.ui.elements.ResizableCanvas;
import ovgu.creasy.util.CreasePatternEditor;
import ovgu.creasy.util.TextLogger;
import ovgu.creasy.util.exporter.cp.PDFCreasePatternExporter;
import ovgu.creasy.util.exporter.cp.PNGCreasePatternExporter;
import ovgu.creasy.util.exporter.cp.SVGCreasePatternExporter;
import ovgu.creasy.util.exporter.history.PDFHistoryExporter;
import ovgu.creasy.util.exporter.history.SVGHistoryExporter;

import java.io.*;
import java.util.*;

import static ovgu.creasy.ui.elements.ResizableCanvas.CANVAS_HEIGHT;
import static ovgu.creasy.ui.elements.ResizableCanvas.CANVAS_WIDTH;

public class MainWindow {

    public static HashMap<String, String> EXAMPLES = new HashMap<>(Map.of(
            "Bird", "example/bird.cp",
            "Penguin", "example/penguin_hideo_komatsu.cp",
            "Crane", "example/crane.cp"
    ));

    @FXML
    private ToggleGroup edit;
    @FXML
    public ToggleGroup line;

    private CreasePatternCanvas activeHistory;
    private Crease highlightedCrease;
    private String filePath;

    @FXML
    private ScrollPane canvasHolder;
    @FXML
    private VBox window;
    @FXML
    private Label historyLabel;
    @FXML
    private Button reloadButton;

    private HostServices hostServices;
    private OrigamiModel model;
    private CreasePattern cp;

    private List<CreasePatternCanvas> historyCanvasList;
    private List<CreasePatternCanvas> stepsCanvasList;
    private HashMap<CreasePatternCanvas, Separator> pairs;

    private boolean wasSaved = false;
    private boolean adding = false;
    private Line addedCreaseLine;

    public CreasePatternCanvas mainCanvas;
    public ResizableCanvas gridCanvas;
    public ResizableCanvas.Grid grid;

    @FXML
    private CheckBox showPointsCheck;
    @FXML
    private CheckBox snapToGrid;
    @FXML
    private MenuItem foldedModelMenuItem;
    @FXML
    private MenuItem zoomInMenuItem;
    @FXML
    private MenuItem zoomOutMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private Menu exportMenu;

    @FXML
    private TextArea log;
    @FXML
    private VBox history;
    @FXML
    private VBox steps;

    @FXML
    private HBox creaseEditor;
    @FXML
    private GridPane boxes;

    @FXML
    private ColumnConstraints left;
    @FXML
    private ColumnConstraints right;

    private CreasePatternEditor.EditSetting editSetting = CreasePatternEditor.EditSetting.NONE;
    private boolean randomizeEcpPaths = true; // TODO: make UI element for this

    @FXML
    public void initialize() {
        mainCanvas = new CreasePatternCanvas(2000, 2000);
        mainCanvas.setId("main");
        mainCanvas.setManaged(false);

        mainCanvas.getGraphicsContext2D().setFill(Color.TRANSPARENT);
        mainCanvas.getGraphicsContext2D().clearRect(0, 0, 2000,2000);

        gridCanvas = new ResizableCanvas(2000, 2000);
        gridCanvas.setManaged(false);

        gridCanvas.getGraphicsContext2D().setFill(Color.WHITE);
        gridCanvas.getGraphicsContext2D().fillRect(0, 0, 2000,2000);
        gridCanvas.getGraphicsContext2D().setImageSmoothing(false);

        grid = new ResizableCanvas.Grid(gridCanvas,50);
        grid.drawGrid();

        canvasHolder.setContent(new Group(gridCanvas, mainCanvas));

        canvasHolder.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            if (mainCanvas.getCp() != null) {
                if (scrollEvent.getDeltaY() < 0) {
                    this.onZoomOutMenuItem();
                }  else {
                    this.onZoomInMenuItem();
                }
                scrollEvent.consume();
            }
        });

        /*
        resizes the left and right sidebars when the window is maximized
        by adding the appropriate listeners when the Scene and Stage are initialized,
        also adds the closing window event
         */
        window.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        newWindow.setOnCloseRequest(windowEvent -> {
                            if (!historyCanvasList.isEmpty() && !wasSaved) {
                                ClosingWindow.open(historyCanvasList, filePath);
                                windowEvent.consume();
                            }
                        });
                        ((Stage) newWindow).maximizedProperty().addListener((o, oldBoolean, maximized) -> {
                            if (maximized) {
                                left.setPrefWidth(300);
                                right.setPrefWidth(300);
                            } else {
                                left.setPrefWidth(200);
                                right.setPrefWidth(200);
                            }
                        });
                    }
                });
            }
        });

        /*
        resizes the left and right sidebars when the window width is manually being changed
        by dragging it out or in
         */
        window.widthProperty().addListener((o, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                if (left.getPrefWidth() < 300) {
                    left.setPrefWidth(left.getPrefWidth() + 1);
                }
                if (right.getPrefWidth() < 300) {
                    right.setPrefWidth(right.getPrefWidth() + 1);
                }
            } else {
                if (left.getPrefWidth() > 200) {
                    left.setPrefWidth(left.getPrefWidth() - 2);
                }
                if (right.getPrefWidth() > 200) {
                    right.setPrefWidth(right.getPrefWidth() - 2);
                }
            }
        });

        mainCanvas.setOnMouseMoved(mouseEvent -> {
            if (mainCanvas.getCp() != null) {
                CreasePattern main = mainCanvas.getCp();
                Point scale = new Point(mainCanvas.getCpScaleX(), mainCanvas.getCpScaleY());

                Point mousePos = Point.fromPoint2D(mainCanvas.sceneToLocal(
                        mouseEvent.getSceneX() - mainCanvas.getWidth() / 2,
                        mouseEvent.getSceneY() - mainCanvas.getHeight() / 2)
                );

                switch (editSetting) {
                    case ADD -> {
                        for (Point point : main.getPoints()) {
                            if (point.multiply(scale).distance(mousePos) < 5) {
                                point.setHighlighted(true);
                                main.drawOnCanvas(mainCanvas);
                                break;
                            } else {
                                if (point.isHighlighted()) {
                                    point.setHighlighted(false);
                                    main.drawOnCanvas(mainCanvas);
                                    break;
                                }
                            }
                        }
                    }
                    case CHANGE, REMOVE -> {
                        for (Crease crease : main.getCreases()) {
                            Point start = crease.getLine().getStart().multiply(scale);
                            Point end = crease.getLine().getEnd().multiply(scale);

                            Line scaledLine = new Line(start, end);

                            if (scaledLine.contains(mousePos, 0.1)) {
                                CreasePatternEditor.highlightCrease(mainCanvas, crease);
                                highlightedCrease = crease;
                                break;
                            } else {
                                if (crease.isHighlighted()) {
                                    main.drawOnCanvas(mainCanvas);
                                    crease.setHighlighted(false);
                                    highlightedCrease = null;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });

        mainCanvas.setOnMouseClicked(mouseEvent -> {
            if (mainCanvas.getCp() != null) {
                CreasePattern main = mainCanvas.getCp();
                switch (editSetting) {
                    case ADD -> {
                        Point mousePos = Point.fromPoint2D(mainCanvas.sceneToLocal(
                                mouseEvent.getSceneX() - mainCanvas.getWidth() / 2,
                                mouseEvent.getSceneY() - mainCanvas.getHeight() / 2)
                        );

                        Point scale = new Point(mainCanvas.getCpScaleX(), mainCanvas.getCpScaleY());

                        if (!adding) {
                            CreasePatternEditor.returnPointNearMouse(main, mousePos, scale)
                                    .ifPresentOrElse(point -> {
                                        addedCreaseLine = new Line();
                                        addedCreaseLine.setStart(point);
                                        adding = true;
                                    }, () -> {
                                        if (snapToGrid.isSelected()) {
                                            if (CreasePatternEditor.isMousePosOnGrid(mousePos, scale, grid.getCurrentCellSize())) {
                                                Point gridPoint = CreasePatternEditor.alignOnGrid(mousePos, scale, grid.getCurrentCellSize());

                                                addedCreaseLine = new Line();
                                                addedCreaseLine.setStart(gridPoint);
                                                adding = true;
                                            } else {
                                                mouseEvent.consume();
                                            }
                                        } else {
                                            mouseEvent.consume();
                                        }
                                    });

                        } else {
                            Crease.Type type = Crease.Type.fromString(((RadioButton) line.getSelectedToggle()).getText());
                            CreasePatternEditor.returnPointNearMouse(main, mousePos, scale)
                                    .ifPresentOrElse(point -> {
                                        addedCreaseLine.setEnd(point);
                                        CreasePatternEditor.addCrease(main, addedCreaseLine, type);

                                        main.drawOnCanvas(mainCanvas);
                                        adding = false;

                                        if (reloadButton.isDisabled()) {
                                            this.onReloadCP();
                                        }
                                        TextLogger.logText("Added crease of type " + type, log);
                            }, () -> {
                                        if (snapToGrid.isSelected()) {
                                            if (CreasePatternEditor.isMousePosOnGrid(mousePos, scale, grid.getCurrentCellSize())) {
                                                Point gridPoint = CreasePatternEditor.alignOnGrid(mousePos, scale, grid.getCurrentCellSize());
                                                addedCreaseLine.setEnd(gridPoint);

                                                CreasePatternEditor.addCrease(main, addedCreaseLine, type);

                                                main.drawOnCanvas(mainCanvas);
                                                adding = false;

                                                if (reloadButton.isDisabled()) {
                                                    this.onReloadCP();
                                                }
                                                TextLogger.logText("Added crease of type " + type, log);
                                            } else {
                                                mouseEvent.consume();
                                            }
                                        } else {
                                            mouseEvent.consume();
                                        }
                                    });
                        }
                    }
                    case CHANGE -> {
                        if (highlightedCrease != null) {
                            Crease.Type change = Crease.Type.fromString(((RadioButton) line.getSelectedToggle()).getText());
                            CreasePatternEditor.changeCreaseType(mainCanvas, highlightedCrease, change);

                            if (reloadButton.isDisabled()) {
                                this.onReloadCP();
                            }

                            TextLogger.logText("Changed crease type to " + change, log);
                        }
                    }
                    case REMOVE -> {
                        if (highlightedCrease != null) {
                            CreasePatternEditor.removeCrease(mainCanvas, highlightedCrease);

                            if (reloadButton.isDisabled()) {
                                this.onReloadCP();
                            }

                            TextLogger.logText("Removed crease of type " + highlightedCrease.getType(), log);
                        }
                    }
                }
            }
        });

        edit.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (newToggle != null) {
                switch (((ToggleButton) newToggle).getId()) {
                    case "add" -> {
                        editSetting = CreasePatternEditor.EditSetting.ADD;
                        TextLogger.logText("Activate: Add crease tool", log);
                    }
                    case "remove" -> {
                        editSetting = CreasePatternEditor.EditSetting.REMOVE;
                        TextLogger.logText("Activate: Remove crease tool", log);
                    }
                    case "change" -> {
                        editSetting = CreasePatternEditor.EditSetting.CHANGE;
                        TextLogger.logText("Activate: Change crease type tool", log);
                    }
                }
            } else {
                editSetting = CreasePatternEditor.EditSetting.NONE;
            }
        });

        historyCanvasList = new ArrayList<>();
        stepsCanvasList = new ArrayList<>();
        pairs = new HashMap<>();

        TextLogger.logText("Starting up... Welcome to " + Main.APPLICATION_TITLE + " " + Main.VERSION + "!", log);
    }

    /**
     * Opens a file explorer dialogue which lets the user select
     * .cp files and upon loading them, calls setupUI() -- drawing the
     * pattern to the screen.
     */
    @FXML
    private void onMenuImportAction() {
        FileChooser openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open .cp file");
        openFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Crease Patterns", "*.cp"));

        File file = openFileChooser.showOpenDialog(mainCanvas.getScene().getWindow());
        this.filePath = file == null ? "" : file.getPath();

        if (file != null && file.exists()) {
            resetGUI();
            TextLogger.logText("Import: " + filePath, log);
            try {
                setupUI(new FileInputStream(file), filePath);
            } catch (FileNotFoundException e) {
                TextLogger.logText("File not found or invalid!", log);
            }
        } else {
            TextLogger.logText("No file selected or path is invalid!", log);
        }
    }

    /**
     * Opens a file explorer dialogue which lets the user export
     * the history to either pdf or svg
     */
    @FXML
    private void onMenuExportPDFAction() {
        PDFHistoryExporter pdfHistoryExporter = new PDFHistoryExporter(historyCanvasList);
        var file = pdfHistoryExporter.open(mainCanvas.getScene().getRoot());

        if (file.isPresent()) {
            if (pdfHistoryExporter.export(file.get())) {
                TextLogger.logText("Saved " + file.get().getName() + " successfully", log);
                wasSaved = true;

                String title = ((Stage) mainCanvas.getScene().getWindow()).getTitle();
                ((Stage) mainCanvas.getScene().getWindow()).setTitle(title.replace("*", ""));
            } else {
                TextLogger.logText("Error while exporting to PDF", log);
            }
        }
    }

    @FXML
    private void onMenuExportSVGAction() {
        SVGHistoryExporter svgHistoryExporter = new SVGHistoryExporter(historyCanvasList);
        var file = svgHistoryExporter.open(mainCanvas.getScene().getRoot());

        if (file.isPresent()) {
            if (svgHistoryExporter.export(file.get())) {
                TextLogger.logText("Saved " + file.get().getName() + " successfully", log);
                wasSaved = true;

                String title = ((Stage) mainCanvas.getScene().getWindow()).getTitle();
                ((Stage) mainCanvas.getScene().getWindow()).setTitle(title.replace("*", ""));
            } else {
                TextLogger.logText("Error while exporting to SVG", log);
            }
        }
    }

    /**
     * Opens Oripa with the folded 3d model,
     * calls foldModel() method.
     *
     * Opens an Alert in case of an error while folding the model.
     */
    @FXML
    private void onShowFoldedModelAction() throws IOException {
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
                TextLogger.logText("Crease Pattern is invalid, see window for more information", log);
            }
        }
    }

    // -------------------------
    // Handling different kinds of zoom
    @FXML
    private void onZoomInMenuItem() {
        mainCanvas.zoomIn();
        grid.zoomIn();
    }

    @FXML
    private void onZoomOutMenuItem() {
        mainCanvas.zoomOut();
        grid.zoomOut();
    }

    @FXML
    private void onGridIncreaseAction() {
        grid.drawGrid(grid.getCurrentCellSize() * 2);
        TextLogger.logText("Increased grid size (x2), new grid cell size: " + grid.getCurrentCellSize(), log);
    }

    @FXML
    private void onGridDecreaseAction() {
        grid.drawGrid(grid.getCurrentCellSize() / 2);
        TextLogger.logText("Decreased grid size (x0.5), new grid cell size: " + grid.getCurrentCellSize(), log);
    }

    @FXML
    private void onGridCustomAction() {
        CustomGridSizeWindow.open(grid);
        TextLogger.logText("New grid cell size: " + grid.getCurrentCellSize(), log);
    }
    // -------------------------

    @FXML
    private void onMenuResetAction() {
        resetGUI();
        TextLogger.logText("Reset: UI cleared!", log);
    }

    // -------------------------
    // Loading example files
    @FXML
    private void onLoadExample(ActionEvent event) {
        resetGUI();

        String example = ((MenuItem) event.getSource()).getText();
        String filePath = EXAMPLES.get(example);

        TextLogger.logText("Import: " + filePath, log);
        InputStream is = Main.class.getResourceAsStream(filePath);
        setupUI(is, filePath);
    }
    // -------------------------

    /**
     * Opens an "about" dialogue which displays information about Creasy
     * and its developers
     */
    @FXML
    private void onHelpAbout() {
        AboutWindow.open(this.hostServices);
    }

    /**
     * Opens a help dialogue explaining what crease patterns are, what they
     * are used for and explaining the different types of folds
     */
    @FXML
    private void onHelpCP() {
        CreasePatternHelpWindow.open();
    }


    // TODO probably shouldn't be in this class
    private List<ExtendedCreasePattern> createEcps(CreasePattern cp, boolean randomized) {
        List<ExtendedCreasePattern> ecps;
        if (randomized) {
            ecps = new ExtendedCreasePatternFactory().createRandomizedEcps(cp, 10);
        } else {
            ecps = new ArrayList<>();
            ecps.add(new ExtendedCreasePatternFactory().createExtendedCreasePattern(cp));
        }
        return ecps;
    }

    // TODO probably shouldn't be in this class either
    private List<DiagramStep> getSteps(List<ExtendedCreasePattern> ecps) {
        Set<DiagramStep> possibleSteps = new HashSet<>();
        ecps.forEach(cp -> possibleSteps.addAll(cp.possibleSteps()));
        return possibleSteps.stream().toList();
    }

    /**
     * Loads a Crease Pattern, displays it on the canvases and
     * initializes variables
     * @param is the InputStream that is the crease pattern file
     * @param filePath what is displayed in the title bar of the window
     */
    private void setupUI(InputStream is, String filePath) {

        // initialize filePath and window title

        this.filePath = filePath;
        ((Stage) mainCanvas.getScene().getWindow()).setTitle(filePath + "* - " + Main.APPLICATION_TITLE);

        // ----------------------------------
        // draw imported crease pattern on main canvas

        if (is != null) cp = CreasePattern.createFromFile(is);

        if (cp != null) {
            cp.drawOnCanvas(mainCanvas, 1, 1);
            TextLogger.logText("Crease Pattern successfully loaded!", log);
        }

        // ----------------------------------
        // initialize OrigamiModel and execute algorithm -- generate extended crease patterns / folding sequences

        model = new OrigamiModel(cp);

        long start = System.nanoTime();

        List<ExtendedCreasePattern> eCps = createEcps(cp, randomizeEcpPaths);
        List<DiagramStep> possibleSteps = getSteps(eCps);

        long end = System.nanoTime() - start;

        TextLogger.logText(possibleSteps.size() + " possible step(s) were calculated in " + (end / 1e6) + " ms", log);

        // ----------------------------------
        // create needed canvases and draw the steps on them
        // should be called when the algorithm is executed, aka once the amount of steps is known

        createCPCanvases(stepsCanvasList, steps, possibleSteps.size());

        drawSteps(possibleSteps);
        drawHistory(cp, history, historyLabel);

        // ----------------------------------
        // after reading the file, if the file is valid:
        // enable all the previously disabled functions

        foldedModelMenuItem.setDisable(false);
        zoomInMenuItem.setDisable(false);
        zoomOutMenuItem.setDisable(false);
        exportMenu.setDisable(false);
        saveMenuItem.setDisable(false);

        creaseEditor.setDisable(false);
        boxes.setDisable(false);
    }

    private void drawSteps(List<DiagramStep> steps) {
        for (int i = 0; i < steps.size(); i++) {
            DiagramStep step = steps.get(i);
            step.to.drawOnCanvas(stepsCanvasList.get(i),
                    0.4, 0.4);
        }
    }

    private void drawHistory(CreasePattern cp, VBox history, Label historyLabel) {
        createCPCanvases(historyCanvasList, history, 1);
        historyCanvasList.forEach(c -> {
            if (c.getCp() == null) {
                cp.drawOnCanvas(c, 0.4, 0.4);
            }
        });
        historyLabel.setText("History (" + historyCanvasList.size() + " steps)");
    }

    /**
     * Creates new canvases in the sidebar when necessary to show
     * the steps needed
     *
     * @param list the list to add the Canvases to
     * @param sidebar the VBox in which to add the Canvases to
     * @param amount the amount of Canvases needed
     */
    private void createCPCanvases(List<CreasePatternCanvas> list, VBox sidebar, int amount) {
        for (int i = 0; i < amount; ++i) {
            CreasePatternCanvas canvas = new CreasePatternCanvas(CANVAS_WIDTH, CANVAS_HEIGHT);
            Separator separator = new Separator();

            setupMouseEvents(canvas);

            sidebar.getChildren().add(canvas);
            sidebar.getChildren().add(separator);
            pairs.put(canvas, separator);

            list.add(canvas);
        }
    }

    private void setupMouseEvents(CreasePatternCanvas c) {
        GraphicsContext graphicsContext = c.getGraphicsContext2D();
        c.setOnMouseEntered(mouseEvent -> {
            if (!c.equals(activeHistory)) {
                graphicsContext.setFill(Color.color(0.2, 0.2, 0.2, 0.2));
                graphicsContext.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            }
            c.setCursor(Cursor.HAND);

            CreasePattern diff = mainCanvas.getCp().getDifference(c.getCp());
            diff.drawOverCanvas(mainCanvas, mainCanvas.getCpScaleX(), mainCanvas.getCpScaleY());
        });

        c.setOnMouseExited(mouseEvent -> {
            if (!c.equals(activeHistory)) {
                graphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                c.getCp().drawOnCanvas(c, 0.4, 0.4);
            }

            c.setCursor(Cursor.DEFAULT);
            mainCanvas.getCp().drawOnCanvas(mainCanvas);
        });

        c.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (c.getParent().equals(history)) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem delete = new MenuItem("Delete");

                    delete.setOnAction(actionEvent -> {
                        history.getChildren().remove(pairs.get(c));
                        history.getChildren().remove(c);

                        historyCanvasList.remove(c);
                        historyLabel.setText("History (" + historyCanvasList.size() + " steps)");
                        pairs.remove(c);

                        TextLogger.logText("1 step in history successfully deleted", log);
                    });
                    contextMenu.getItems().add(delete);

                    c.setOnContextMenuRequested(contextMenuEvent -> {
                        contextMenu.show(c, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                    });
                }
            } else {
                CreasePattern currentStep = c.getCp();

                currentStep.drawOnCanvas(mainCanvas);
                List<ExtendedCreasePattern> eCps = createEcps(currentStep, randomizeEcpPaths);
                List<DiagramStep> possibleSteps = getSteps(eCps);

                if (c.getParent().equals(steps)) {
                    drawHistory(currentStep, history, historyLabel);
                    historyCanvasList.stream().filter(node -> node.getCp().equals(currentStep))
                            .forEach(node -> {
                                if (activeHistory != null) {
                                    activeHistory.getGraphicsContext2D().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                                    activeHistory.getCp().drawOnCanvas(activeHistory, 0.4, 0.4);
                                    activeHistory.setSelected(false);
                                }

                                activeHistory = node;
                                activeHistory.markAsCurrentlySelected();
                            }
                    );
                    TextLogger.logText("Pick this step and add to history... " + possibleSteps.size() + " new option(s) were calculated", log);
                }
                if (c.getParent().equals(history)) {
                    if (activeHistory != null) {
                        activeHistory.getGraphicsContext2D().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                        activeHistory.getCp().drawOnCanvas(activeHistory, 0.4, 0.4);
                        activeHistory.setSelected(false);
                    }

                    activeHistory = c;
                    activeHistory.setSelected(true);
                }

                steps.getChildren().clear();
                stepsCanvasList.clear();

                createCPCanvases(stepsCanvasList, steps, possibleSteps.size());
                drawSteps(possibleSteps);
            }
        });
    }

    @FXML
    private void reverseHistory() {
        List<CreasePatternCanvas> reverseList = new ArrayList<>();
        for (int i = historyCanvasList.size() - 1; i >= 0; i--) {
            reverseList.add(new CreasePatternCanvas(historyCanvasList.get(i)));
        }

        if (activeHistory != null) activeHistory.setSelected(false);

        for (int i = 0, reverseListSize = reverseList.size(); i < reverseListSize; i++) {
            CreasePatternCanvas canvas = reverseList.get(i);
            canvas.getCp().drawOnCanvas(historyCanvasList.get(i), 0.4, 0.4);

            if (canvas.isSelected()) {
                activeHistory = historyCanvasList.get(i);
                activeHistory.markAsCurrentlySelected();
            }
        }
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onSaveMainCP() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save crease pattern as...");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"),
                                                 new FileChooser.ExtensionFilter("Scalable Vector Graphics", "*.svg"),
                                                 new FileChooser.ExtensionFilter("PNG Image", "*.png"));

        File file = fileChooser.showSaveDialog(mainCanvas.getScene().getWindow());
        if (file != null) {
            switch (fileChooser.getSelectedExtensionFilter().getDescription()) {
                case "PDF Document" -> new PDFCreasePatternExporter(cp).export(file);
                case "Scalable Vector Graphics" -> new SVGCreasePatternExporter(cp).export(file);
                case "PNG Image" -> new PNGCreasePatternExporter(mainCanvas).export(file);
            }
        }
    }

    @FXML
    private void onShowPoints() {
        if (showPointsCheck.isSelected()) {
            TextLogger.logText("Rendering all points in the crease pattern", log);
            mainCanvas.setShowPoints(true);
        } else {
            TextLogger.logText("Disabling render of points in crease pattern", log);
            mainCanvas.setShowPoints(false);
        }
        mainCanvas.getCp().drawOnCanvas(mainCanvas);
    }

    @FXML
    private void onReloadCP() {
        cp.removeAllLinearPoints();
        cp.drawOnCanvas(mainCanvas);

        steps.getChildren().clear();
        stepsCanvasList.clear();

        List<ExtendedCreasePattern> eCps = createEcps(cp, randomizeEcpPaths);
        List<DiagramStep> possibleSteps = getSteps(eCps);

        createCPCanvases(stepsCanvasList, steps, possibleSteps.size());
        drawSteps(possibleSteps);

        TextLogger.logText("-----------------", log);
        TextLogger.logText("Reloading simplification algorithm with modified crease pattern...", log);
    }

    /**
     * Clears all the canvases and disables menu options that would only work
     * with a loaded file.
     * Useful for resetting state.
     */
    private void resetGUI() {
        mainCanvas.setCp(null);
        mainCanvas.getGraphicsContext2D().clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
        ((Stage) mainCanvas.getScene().getWindow()).setTitle(Main.APPLICATION_TITLE);

        historyLabel.setText("History (0 steps)");

        steps.getChildren().clear();
        history.getChildren().clear();

        mainCanvas.setShowPoints(false);

        foldedModelMenuItem.setDisable(true);
        zoomInMenuItem.setDisable(true);
        zoomOutMenuItem.setDisable(true);
        exportMenu.setDisable(true);
        saveMenuItem.setDisable(true);

        creaseEditor.setDisable(true);
        boxes.setDisable(true);
        showPointsCheck.setSelected(false);

        stepsCanvasList.clear();
        historyCanvasList.clear();

        cp = null;
        wasSaved = false;
        pairs.clear();

        grid.reset();

        TextLogger.logText("-----------------", log);
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private void onLiveReload() {
        reloadButton.setDisable(!reloadButton.isDisabled());
    }
}
