package ovgu.creasy.ui;

import javafx.scene.canvas.Canvas;
import ovgu.creasy.origami.CreasePattern;

public class ResizableCanvas extends Canvas {

    private CreasePattern cp;

    public ResizableCanvas(double width, double height) {
        super(width, height);
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
}
