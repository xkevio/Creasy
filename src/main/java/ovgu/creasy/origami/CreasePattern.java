package ovgu.creasy.origami;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;

import java.io.*;
import java.util.*;

/**
 * A collection of creases that, when folded, create an origami Model
 */
public class CreasePattern {
    private double EPS = 0.000001;
    /**
     * all creases in the Crease Pattern
     */
    private Set<Crease> creases;
    /**
     * all points in the Crease pattern.
     */
    private Set<Point> points;

    /**
     * for any Point p in points, this contains a List of creases
     * that end or start in p
     */
    private HashMap<Point, List<Crease>> adjacentCreases;

    public CreasePattern(Set<Crease> creases, Set<Point> points) {
        this.creases = creases;
        this.points = points;
    }

    public CreasePattern() {
        this.creases = new HashSet<>();
        this.points = new HashSet<>();
    }

    public void addCrease(Crease crease){
        addAndRoundPoints(crease);
        this.creases.add(crease);
        addToAdjacentCreases(crease);
    }

    private void addToAdjacentCreases(Crease crease) {
        this.adjacentCreases.get(crease.getLine().getStart()).add(crease);
        this.adjacentCreases.get(crease.getLine().getEnd()).add(crease);
    }

    /**
     * adds both points of the Crease or replaces them with very close points (distance <= EPS)
     * if possible
     */
    private void addAndRoundPoints(Crease crease) {
        crease.getLine().setEnd(addPoint(crease.getLine().getEnd()));
        crease.getLine().setStart(addPoint(crease.getLine().getStart()));
    }

    /**
     * returns a very near Point if one exists (distance <= EPS). If none exists,
     * adds p to points and returns p
     */
    private Point addPoint(Point p) {
        Optional<Point> nearPoint = points.stream().filter(point -> point.distance(p) <= EPS).findAny();
        if (nearPoint.isPresent()) {
            return nearPoint.get();
        }
        points.add(p);
        adjacentCreases.put(p, new ArrayList<>());
        return p;
    }

    public Set<Crease> getCreases() {
        return Collections.unmodifiableSet(creases);
    }

    public Set<Point> getPoints() {
        return Collections.unmodifiableSet(points);
    }

    /**
     * Draws the loaded Crease Pattern on the current canvas by
     * iterating over all Creases and choosing the colors based
     * on the type of Line
     * @param canvas the Canvas to draw the Crease Pattern on
     */
    public void drawOnCanvas(Canvas canvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

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

        graphicsContext.translate(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
    }

    public static CreasePattern createFromFile(File file) {
        CreasePattern cp = new CreasePattern();
        try {
            Reader reader = new FileReader(file);
            StreamTokenizer st = new StreamTokenizer(reader);
            st.resetSyntax();

            st.wordChars('0', '9');
            st.wordChars('.', '.');
            st.wordChars('-', '-');
            st.wordChars('E', 'E');

            st.whitespaceChars(' ', ' ');
            st.whitespaceChars('\t', '\t');
            st.whitespaceChars('\n', '\n');
            st.whitespaceChars('\r', '\r');

            // while end of file is not reached:
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                Point point1 = new Point(0, 0);
                Point point2 = new Point(0, 0);

                Crease.Type type = switch (Integer.parseInt(st.sval)) {
                    case 1 -> Crease.Type.EDGE;
                    case 2 -> Crease.Type.MOUNTAIN;
                    case 3 -> Crease.Type.VALLEY;
                    default -> throw new IllegalStateException("Unexpected value: " + Integer.parseInt(st.sval));
                };

                // get line type - 1=edge, 2=mountain, 3=valley

                // read data for point 1
                st.nextToken();
                point1.setX(Double.parseDouble(st.sval));
                st.nextToken();
                point1.setY(Double.parseDouble(st.sval));
                // point1.toString();

                // read data for point 2
                st.nextToken();
                point2.setX(Double.parseDouble(st.sval));
                st.nextToken();
                point2.setY(Double.parseDouble(st.sval));
                // point2.toString();

                // create line with data
                Line line = new Line(point1, point2);

                // create crease with collected data
                Crease crease = new Crease(line, type);

                // add created crease to crease pattern
                cp.addCrease(crease);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return cp;
    }
}
