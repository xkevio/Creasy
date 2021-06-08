package ovgu.creasy.origami;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of creases that, when folded, create an origami Model
 */
public class CreasePattern {
    /**
     * all creases in the Crease Pattern
     */
    private Set<Crease> creases;
    /**
     * all points in the Crease pattern.
     */
    private Set<Point> points;

    public CreasePattern(Set<Crease> creases, Set<Point> points) {
        this.creases = creases;
        this.points = points;
    }

    public CreasePattern() {
        this.creases = new HashSet<>();
        this.points = new HashSet<>();
    }

    public void addCrease(Crease crease){
        this.creases.add(crease);
    }

    public void addPoints(Point x, Point y){
        this.points.add(x);
        this.points.add(y);
    }

    public void addLine() {
        return;
    }

    public Set<Crease> getCreases() {
        return creases;
    }

    public Set<Point> getPoints() {
        return points;
    }

    /**
     * Draws the loaded Crease Pattern on the current canvas by
     * iterating over all Creases and choosing the colors based
     * on the type of Line
     * @param canvas the Canvas to draw the Crease Pattern on
     */
    public void drawOnCanvas(Canvas canvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        graphicsContext.setLineWidth(2);

        for (Crease crease : creases) {
            Color currentColor = switch (crease.getType()) {
                case EDGE -> Color.BLACK;
                case VALLEY -> Color.BLUE;
                case MOUNTAIN -> Color.RED;
            };

            graphicsContext.setStroke(currentColor);

            Point start = crease.getLine().getStart();
            Point end = crease.getLine().getEnd();
            graphicsContext.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
        }
    }

    public static CreasePattern createFromFile(File file) {
        CreasePattern cp = new CreasePattern();
        try {
            int token;
            Reader reader = new FileReader(file);
            StreamTokenizer st = new StreamTokenizer(reader);
            st.resetSyntax();
            st.wordChars('0', '9');
            st.wordChars('.', '.');
            st.wordChars('0', '\u00FF');
            st.wordChars('-', '-');
            st.wordChars('e', 'E');
            st.whitespaceChars(' ', ' ');
            st.whitespaceChars('\t', '\t');
            st.whitespaceChars('\n', '\n');
            st.whitespaceChars('\r', '\r');

            // while end of file is not reached:
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                Point point1 = new Point(0, 0);
                Point point2 = new Point(0, 0);
                Crease.Type type;

                // get line type - 1=edge, 2=mountain, 3=valley
                switch (Integer.parseInt(st.sval)) {
                    case 1:
                        type = Crease.Type.EDGE;
                        break;
                    case 2:
                        type = Crease.Type.MOUNTAIN;
                        break;
                    case 3:
                        type = Crease.Type.VALLEY;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + Integer.parseInt(st.sval));
                }

                // read data for point 1
                token = st.nextToken();
                point1.setX(Double.parseDouble(st.sval));
                token = st.nextToken();
                point1.setY(Double.parseDouble(st.sval));
                point1.toString();

                // read data for point 2
                token = st.nextToken();
                point2.setX(Double.parseDouble(st.sval));
                token = st.nextToken();
                point2.setY(Double.parseDouble(st.sval));
                point2.toString();

                // create line with data
                Line line = new Line(point1, point2);

                // create crease with collected data
                Crease crease = new Crease(line, type);

                // add created crease to crease pattern
                cp.addCrease(crease);
                cp.addPoints(point1, point2);

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return cp;
    }
}
