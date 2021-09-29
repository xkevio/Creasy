package ovgu.creasy.util;

import javafx.scene.paint.Color;
import ovgu.creasy.origami.Crease;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.ui.ResizableCanvas;

import java.util.HashSet;
import java.util.Set;

public class CreasePatternEditor {

    public enum EditSetting {
        ADD,
        REMOVE,
        CHANGE,
        NONE,
    }

    /**
     * Draws the points of the crease pattern contained in the canvas on the given canvas.
     * Iterates through all the points and draws a rectangle per point.
     * @param canvas the canvas which contains the crease pattern the points are based on
     */
    public static void showPoints(ResizableCanvas canvas) {
        canvas.getGraphicsContext2D().setFill(Color.GRAY);
        canvas.getGraphicsContext2D().translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.getCp().getPoints().forEach(point -> {
            canvas.getGraphicsContext2D().fillRect(point.getX() * canvas.getCpScaleX() - 5,
                    point.getY() * canvas.getCpScaleY() - 5, 10, 10);
        });
        canvas.getGraphicsContext2D().translate(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
    }

    /**
     * Draws the given Crease thicker over the current crease pattern
     * by constructing a crease pattern consisting of only this crease
     * and calling drawOverCanvas.
     * @param canvas the canvas to highlight the crease on
     * @param crease the crease to highlight, gets drawn thicker
     */
    public static void highlightCrease(ResizableCanvas canvas, Crease crease) {
        crease.setHighlighted(true);
        CreasePattern highlightedLine = new CreasePattern(Set.of(crease), new HashSet<>());
        highlightedLine.drawOverCanvas(canvas, canvas.getCpScaleX(), canvas.getCpScaleY());
    }
}
