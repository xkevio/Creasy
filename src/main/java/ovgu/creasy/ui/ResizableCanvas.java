package ovgu.creasy.ui;

import javafx.scene.canvas.Canvas;
import ovgu.creasy.origami.CreasePattern;

public class ResizableCanvas extends Canvas {

    private CreasePattern cp;
    private double cpScaleX;
    private double cpScaleY;

    public ResizableCanvas(double width, double height) {
        super(width, height);
        this.cpScaleX = 1;
        this.cpScaleY = 1;
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
}
