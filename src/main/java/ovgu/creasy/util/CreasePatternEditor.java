package ovgu.creasy.util;

import javafx.scene.paint.Color;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;
import ovgu.creasy.origami.basic.Crease;
import ovgu.creasy.origami.basic.CreasePattern;
import ovgu.creasy.ui.elements.CreasePatternCanvas;

import java.util.*;

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
    public static void showPoints(CreasePatternCanvas canvas) {
        canvas.getGraphicsContext2D().translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

        if (canvas.isShowPoints()) {
            canvas.getCp().getPoints().forEach(point -> {
                canvas.getGraphicsContext2D().setFill(point.isHighlighted() ? Color.LIGHTGREEN : Color.GRAY);
                canvas.getGraphicsContext2D().fillRect(point.getX() * canvas.getCpScaleX() - 5,
                        point.getY() * canvas.getCpScaleY() - 5, 10, 10);
            });
        } else {
            Optional<Point> toHighlight = canvas.getCp().getPoints()
                    .stream()
                    .filter(Point::isHighlighted)
                    .findAny();

            toHighlight.ifPresent(point -> {
                canvas.getGraphicsContext2D().setFill(Color.LIGHTGREEN);
                canvas.getGraphicsContext2D().fillRect(point.getX() * canvas.getCpScaleX() - 5,
                        point.getY() * canvas.getCpScaleY() - 5, 10, 10);
            });
        }

        canvas.getGraphicsContext2D().translate(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
    }

    /**
     * Draws the given Crease thicker over the current crease pattern
     * by constructing a crease pattern consisting of only this crease
     * and calling drawOverCanvas.
     * @param canvas the canvas to highlight the crease on
     * @param crease the crease to highlight, gets drawn thicker
     */
    public static void highlightCrease(CreasePatternCanvas canvas, Crease crease) {
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
    public static void changeCreaseType(CreasePatternCanvas canvas, Crease crease, Crease.Type type) {
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
    public static void removeCrease(CreasePatternCanvas canvas, Crease crease) {
        canvas.getCp().removeCrease(crease);
        canvas.getCp().drawOnCanvas(canvas);
    }

    /**
     * Checks if there is a crease pattern point near the mouse position and returns
     * an Optional with the point if there is one.
     * @param creasePattern the crease pattern whose set of points will be checked
     * @param mousePos the position of the mouse
     * @param scale the canvas scale
     * @return an Optional that is empty if there is no point near or contains the point
     */
    public static Optional<Point> returnPointNearMouse(CreasePattern creasePattern, Point mousePos, Point scale) {
        return creasePattern.getPoints()
                .stream()
                .filter(point -> point.multiply(scale).distance(mousePos) < 5)
                .findAny();
    }

    /**
     * Checks if the mouse pointer is on an intersection point of the grid.
     * @param mousePos the position of the mouse
     * @param scale the canvas scale
     * @param currentCellSize the current size of the grid cells
     * @return if the mouse position is on such an intersection
     */
    public static boolean isMousePosOnGrid(Point mousePos, Point scale, int currentCellSize) {
        return ((mousePos.divide(scale).getX() % currentCellSize <= 5 || mousePos.divide(scale).getX() % currentCellSize >= currentCellSize - 5) &&
                (mousePos.divide(scale).getY() % currentCellSize <= 5 || mousePos.divide(scale).getY() % currentCellSize >= currentCellSize - 5));
    }

    /**
     * Returns the closest grid intersection point from the mouse position.
     * @param mousePos the position of the mouse
     * @param scale the canvas scale
     * @param currentCellSize the current size of the grid cells
     * @return the new aligned point on an intersection point in local coordinates
     */
    public static Point alignOnGrid(Point mousePos, Point scale, int currentCellSize) {
        Point gridPoint = mousePos.divide(scale);
        double diffX = gridPoint.getX() % currentCellSize;
        double diffY = gridPoint.getY() % currentCellSize;

        if (diffX <= 5) {
            gridPoint.setX(gridPoint.getX() - diffX);
        }
        if (diffX >= currentCellSize - 5) {
            gridPoint.setX(gridPoint.getX() + (currentCellSize - diffX));
        }
        if (diffY <= 5) {
            gridPoint.setY(gridPoint.getY() - diffY);
        }
        if (diffY >= currentCellSize - 5) {
            gridPoint.setY(gridPoint.getY() + (currentCellSize - diffY));
        }
        return gridPoint;
    }

    public static void addCrease(CreasePattern creasePattern, Line addLine, Crease.Type type) {
        HashMap<Crease, List<Line>> lineStore = new HashMap<>();

        creasePattern.getCreases().forEach(crease -> {
            if (!(crease.getLine().contains(addLine.getStart(), 0.1) ||
                    crease.getLine().contains(addLine.getEnd(), 0.1))) {
                addLine.intersection(crease.getLine())
                        .ifPresent(intersection -> {
                            addLine.addSplicePoints(intersection);
                            crease.getLine().addSplicePoints(intersection);
                        });
            }

            if (crease.getLine().getIntersectionSize() > 0) {
                lineStore.put(crease, crease.getLine().splicedLines());
            }
        });

        List<Line> lines = addLine.splicedLines();
        lines.forEach(newLines -> {
            System.out.println(newLines);
            Crease newCrease = new Crease(newLines, type);
            creasePattern.addCrease(newCrease);
        });

        lineStore.forEach((crease, newLines) -> {
            creasePattern.removeCrease(crease);
            newLines.forEach(newLine -> {
                Crease newCrease = new Crease(newLine, crease.getType());
                creasePattern.addCrease(newCrease);
            });
        });
    }
}
