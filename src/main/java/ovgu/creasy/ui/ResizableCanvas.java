package ovgu.creasy.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import ovgu.creasy.origami.CreasePattern;

public class ResizableCanvas extends Canvas {

    public static final int CANVAS_WIDTH = 200;
    public static final int CANVAS_HEIGHT = 200;

    private CreasePattern cp;

    private double cpScaleX;
    private double cpScaleY;

    private int currentCellSize;
    private boolean isSelected = false;

    public ResizableCanvas(double width, double height) {
        super(width, height);
        this.cpScaleX = 1;
        this.cpScaleY = 1;
        this.currentCellSize = 50;
    }

    public ResizableCanvas(ResizableCanvas clone) {
        this(clone.getWidth(), clone.getHeight());
        this.setCp(clone.getCp());
        this.isSelected = clone.isSelected();
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

    public void scaleGridUp() {
        // TODO...
        this.drawGrid();
//        this.setScaleX(this.getScaleX() + 0.1);
//        this.setScaleY(this.getScaleY() + 0.1);
    }

    public void scaleGridDown() {
        // TODO...
//        this.getGraphicsContext2D().getTransform().setMxx(this.getGraphicsContext2D().getTransform().getMxx() - 0.1);
//        this.getGraphicsContext2D().getTransform().setMyy(this.getGraphicsContext2D().getTransform().getMyy() - 0.1);

        this.drawGrid();
//        this.setScaleX(this.getScaleX() - 0.1);
//        this.setScaleY(this.getScaleY() - 0.1);
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

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
