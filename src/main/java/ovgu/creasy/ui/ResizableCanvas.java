package ovgu.creasy.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ovgu.creasy.origami.CreasePattern;

public class ResizableCanvas extends Canvas {

    private CreasePattern cp;

    private double cpScaleX;
    private double cpScaleY;

    private int currentCellSize;

    public ResizableCanvas(double width, double height) {
        super(width, height);
        this.cpScaleX = 1;
        this.cpScaleY = 1;
        this.currentCellSize = 50;
    }

    public void drawGrid() {
        drawGrid(currentCellSize);
    }

    public void drawGrid(int cellSize) {
        if (cellSize <= 0) return;

        this.currentCellSize = cellSize;
        GraphicsContext graphicsContext = this.getGraphicsContext2D();
        graphicsContext.fillRect(0, 0, getWidth(), getHeight());

        graphicsContext.setStroke(Color.GRAY);
        graphicsContext.setLineWidth(0.5);

        for (int i = 0; i < getWidth(); i += cellSize) {
            graphicsContext.strokeLine(i, 0, i, getHeight());
        }

        for (int i = 0; i < getHeight(); i += cellSize) {
            graphicsContext.strokeLine(0, i, getWidth(), i);
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

    public int getCurrentCellSize() {
        return currentCellSize;
    }

    public void setCurrentCellSize(int currentCellSize) {
        this.currentCellSize = currentCellSize;
    }
}
