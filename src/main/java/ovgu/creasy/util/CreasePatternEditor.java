package ovgu.creasy.util;

import javafx.scene.paint.Color;
import ovgu.creasy.ui.ResizableCanvas;

public class CreasePatternEditor {

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
}
