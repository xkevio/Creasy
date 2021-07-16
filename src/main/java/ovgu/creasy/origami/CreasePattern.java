package ovgu.creasy.origami;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;

import java.io.*;
import java.util.*;

/**
 * A collection of creases that, when folded, create an origami Model.
 * Merges points closer than EPS, automatically updating new creases
 * that are added
 */
public class CreasePattern {
    private static final double EPS = 0.000001;
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
     * that end or start in p, sorted by angle
     */
    private HashMap<Point, List<Crease>> adjacentCreases;

    public CreasePattern(Set<Crease> creases, Set<Point> points) {
        this.creases = creases;
        this.points = points;
        this.adjacentCreases = new HashMap<>();
    }

    public CreasePattern() {
        this(new HashSet<>(), new HashSet<>());
    }

    public void addCrease(Crease crease){
        addOrMergePoints(crease);
        this.creases.add(crease);
        addToAdjacentCreases(crease);
    }

    public List<Crease> getAdjacentCreases(Point p) {
        if (!adjacentCreases.containsKey(p)) {
            return new ArrayList<>();
        }
        return Collections.unmodifiableList(adjacentCreases.get(p));
    }

    private void addToAdjacentCreases(Crease crease) {
        addToAdjacentCreases(crease.getLine().getStart(), crease);
        addToAdjacentCreases(crease.getLine().getEnd(), crease);
    }

    /**
     * Adds newCrease to the adjacentCreases entry of startOrEnd (which is either the start or end point of
     * newCrease), keeping the sorting order intact
     */
    private void addToAdjacentCreases(Point startOrEnd, Crease newCrease) {
        List<Crease> adjCreasesStart = adjacentCreases.get(startOrEnd);
        double newAngle = getAngle(startOrEnd, newCrease);
        double currAngle;
        boolean done = false;
        // finds the first crease with an angle smaller than that of the new crease,
        // then inserts the new crease right before that crease
        for (int i = 0; i < adjCreasesStart.size(); i++) {
            Crease existingCrease = adjCreasesStart.get(i);
            currAngle = getAngle(startOrEnd, existingCrease);
            if (newAngle > currAngle) {
                adjCreasesStart.add(i, newCrease);
                done = true;
                break;
            }
        }
        // if no crease with a smaller angle exists, insert at the end
        if (!done) {
            adjCreasesStart.add(newCrease);
        }
    }

    private double getAngle(Point startOrEnd, Crease crease) {
        if (crease.getLine().getStart().equals(startOrEnd)) {
            return crease.getLine().getStart().clockwiseAngle(crease.getLine().getEnd());
        } else {
            return crease.getLine().getEnd().clockwiseAngle(crease.getLine().getStart());
        }
    }

    /**
     * Adds both points of the Crease or replaces them with very close points (distance <= EPS)
     * if possible
     * @param crease the Crease of which the start and end point get added or replaced
     */
    private void addOrMergePoints(Crease crease) {
        crease.getLine().setStart(addPoint(crease.getLine().getStart()));
        crease.getLine().setEnd(addPoint(crease.getLine().getEnd()));
    }

    /**
     * Either adds p to the points Set or returns a very near Point to p
     * @param p the point to be added and returned if no other point is closer
     * @return a very near Point if one exists (distance <= EPS) or p
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
     * @param scaleX scales the GraphicsContext in the x amount (default = 1)
     * @param scaleY scales the GraphicsContext in the y amount (default = 1)
     */
    public void drawOnCanvas(Canvas canvas, double scaleX, double scaleY) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        graphicsContext.setLineWidth(2);

        graphicsContext.scale(scaleX, scaleY);
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

    /**
     * Creates CreasePattern from the file given by
     * reading through it line by line and assigning the correct
     * CreaseTypes and coordinates to the returned CreasePattern,
     * utilizes StreamTokenizer
     * @param file the .cp file in which the CreasePattern is described
     * @return a CreasePattern based on the instructions in the file
     */
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
