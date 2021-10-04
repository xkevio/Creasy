package ovgu.creasy.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import ovgu.creasy.origami.basic.CreasePattern;

/**
 * A canvas specifically tailored for rendering crease patterns.
 * Should not be used for other purposes.
 */
public class ResizableCanvas extends Canvas {

    public static class Grid {
        private int currentCellSize;
        private final ResizableCanvas gridCanvas;

        public Grid(ResizableCanvas gridCanvas, int currentCellSize) {
            this.gridCanvas = gridCanvas;
            this.currentCellSize = currentCellSize;
        }

        public void drawGrid() {
            this.drawGrid(currentCellSize);
        }

        public void drawGrid(int cellSize) {
            if (cellSize <= 0) return;

            this.currentCellSize = cellSize;

            GraphicsContext graphicsContext = gridCanvas.getGraphicsContext2D();
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(-10_000, -10_000, 20_000, 20_000);

            graphicsContext.setStroke(Color.GRAY);
            graphicsContext.setLineWidth(0.5);

            // draws vertical lines
            for (int i = -10_000; i < 20_000; i += cellSize) {
                graphicsContext.strokeLine(i, -10_000, i, 20_000);
            }

            // draws horizontal lines
            for (int i = -10_000; i < 20_000; i += cellSize) {
                graphicsContext.strokeLine(-10_000, i, 20_000, i);
            }
        }

        public void zoomIn() {
            // System.out.println("zoom in - trying really hard to get the grid zoom to work");

            gridCanvas.getGraphicsContext2D().fillRect(-10_000, -10_000, 20_000, 20_000);

            gridCanvas.getGraphicsContext2D().translate(gridCanvas.getWidth() / 2, gridCanvas.getHeight() / 2);
            gridCanvas.getGraphicsContext2D().setTransform(
                    gridCanvas.getGraphicsContext2D().getTransform().getMxx() + 0.1,
                    gridCanvas.getGraphicsContext2D().getTransform().getMyx(),
                    gridCanvas.getGraphicsContext2D().getTransform().getMxy(),
                    gridCanvas.getGraphicsContext2D().getTransform().getMyy() + 0.1,
                    gridCanvas.getGraphicsContext2D().getTransform().getTx(),
                    gridCanvas.getGraphicsContext2D().getTransform().getTy());
            gridCanvas.getGraphicsContext2D().translate(-gridCanvas.getWidth() / 2, -gridCanvas.getHeight() / 2);

            this.drawGrid();
        }

        public void zoomOut() {
            // System.out.println("zoom out - trying really hard to get the grid zoom to work");
            if (gridCanvas.getGraphicsContext2D().getTransform().getMxx() >= 0.1) {
                gridCanvas.getGraphicsContext2D().fillRect(-10_000, -10_000, 20_000, 20_000);

                gridCanvas.getGraphicsContext2D().translate(gridCanvas.getWidth() / 2, gridCanvas.getHeight() / 2);
                gridCanvas.getGraphicsContext2D().setTransform(
                        gridCanvas.getGraphicsContext2D().getTransform().getMxx() - 0.1,
                        gridCanvas.getGraphicsContext2D().getTransform().getMyx(),
                        gridCanvas.getGraphicsContext2D().getTransform().getMxy(),
                        gridCanvas.getGraphicsContext2D().getTransform().getMyy() - 0.1,
                        gridCanvas.getGraphicsContext2D().getTransform().getTx(),
                        gridCanvas.getGraphicsContext2D().getTransform().getTy());
                gridCanvas.getGraphicsContext2D().translate(-gridCanvas.getWidth() / 2, -gridCanvas.getHeight() / 2);

                this.drawGrid();
            }
        }

        public void reset() {
            gridCanvas.getGraphicsContext2D().setTransform(new Affine());
            this.drawGrid();
        }

        public int getCurrentCellSize() {
            return currentCellSize;
        }
    }

    public static final int CANVAS_WIDTH = 180;
    public static final int CANVAS_HEIGHT = 180;

    private CreasePattern cp;

    private double cpScaleX;
    private double cpScaleY;

    private boolean isSelected = false;
    private boolean showPoints = false;

    public ResizableCanvas(double width, double height) {
        super(width, height);
        this.cpScaleX = 1;
        this.cpScaleY = 1;
    }

    public ResizableCanvas(ResizableCanvas clone) {
        this(clone.getWidth(), clone.getHeight());
        this.setCp(clone.getCp());
        this.isSelected = clone.isSelected();
    }

    public void markAsCurrentlySelected() {
        isSelected = true;
        getGraphicsContext2D().setFill(Color.color(0.2, 0.2, 0.2, 0.2));
        getGraphicsContext2D().fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    public void zoomIn() {
        if (cp != null) {
            cp.drawOnCanvas(this, this.getCpScaleX() + 0.1, this.getCpScaleY() + 0.1);
        }
    }

    public void zoomOut() {
        if (cp != null) {
            if (this.getCpScaleX() >= 0.1) {
                cp.drawOnCanvas(this, this.getCpScaleX() - 0.1, this.getCpScaleY() - 0.1);
            }
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public CreasePattern getCp() {
        return cp;
    }

    public void setCp(CreasePattern cp) {
        this.cp = cp;
    }

    public double getCpScaleX() {
        return cpScaleX;
    }

    public double getCpScaleY() {
        return cpScaleY;
    }

    public void setCpScaleX(double cpScaleX) {
        this.cpScaleX = cpScaleX;
    }

    public void setCpScaleY(double cpScaleY) {
        this.cpScaleY = cpScaleY;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

    public boolean isShowPoints() {
        return showPoints;
    }
}
