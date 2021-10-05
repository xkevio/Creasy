package ovgu.creasy.ui.elements;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

/**
 * Base canvas class that is resizable,
 * useful for everything but crease pattern rendering
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

    public ResizableCanvas(double width, double height) {
        super(width, height);
    }

    public ResizableCanvas(ResizableCanvas clone) {
        this(clone.getWidth(), clone.getHeight());
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
}
