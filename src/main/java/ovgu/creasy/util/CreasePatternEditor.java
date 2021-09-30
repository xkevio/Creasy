package ovgu.creasy.util;

import javafx.scene.paint.Color;
import ovgu.creasy.origami.Crease;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.ui.ResizableCanvas;

import java.util.HashSet;
import java.util.Set;

/**
 * Some editor functions for crease patterns on a canvas
 * to streamline the function calls and reduce repetition
 */
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

    /**
     * Changes the type of the given crease to the given enum type and redraws the
     * changed crease pattern on the given canvas.
     * Removes old crease, changes type with setType() and re-adds it
     * as just changing the crease type would invalidate the hash.
     * @param canvas the canvas the crease is drawn on
     * @param crease the crease whose type shall be changed
     * @param type the new crease type
     */
    public static void changeCreaseType(ResizableCanvas canvas, Crease crease, Crease.Type type) {
        canvas.getCp().removeCrease(crease);

        crease.setType(type);
        canvas.getCp().addCrease(crease);

        canvas.getCp().drawOnCanvas(canvas);
    }

    /**
     * Removes the given crease and redraws the canvas
     * @param canvas the canvas to remove the crease on
     * @param crease the crease to remove
     */
    public static void removeCrease(ResizableCanvas canvas, Crease crease) {
        canvas.getCp().removeCrease(crease);
        canvas.getCp().drawOnCanvas(canvas);
    }

    public static void addCrease(ResizableCanvas canvas, Crease crease) {

    }
}
