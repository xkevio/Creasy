package ovgu.creasy.ui.elements;

import javafx.scene.paint.Color;
import ovgu.creasy.origami.basic.CreasePattern;

/**
 * A canvas specifically tailored for rendering crease patterns.
 * Should not be used for other purposes.
 */
public class CreasePatternCanvas extends ResizableCanvas {

    private boolean showPoints = false;
    private CreasePattern cp;

    private double cpScaleX;
    private double cpScaleY;

    private boolean isSelected = false;

    public CreasePatternCanvas(double width, double height) {
        super(width, height);
        this.cpScaleX = 1;
        this.cpScaleY = 1;
    }

    public CreasePatternCanvas(CreasePatternCanvas clone) {
        super(clone);
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
