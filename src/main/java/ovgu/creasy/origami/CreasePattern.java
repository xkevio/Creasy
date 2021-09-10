package ovgu.creasy.origami;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;
import ovgu.creasy.ui.ResizableCanvas;
import ovgu.creasy.util.TextLogger;

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
    private final Set<Crease> creases;
    /**
     * all points in the Crease pattern.
     */
    private final Set<Point> points;

    /**
     * for any Point p in points, this contains a List of creases
     * that end or start in p, sorted by angle
     */
    private final HashMap<Point, List<Crease>> adjacentCreases;

    public CreasePattern(Set<Crease> creases, Set<Point> points) {
        this.creases = creases;
        this.points = points;
        this.adjacentCreases = new HashMap<>();
    }

    public CreasePattern() {
        this(new HashSet<>(), new HashSet<>());
    }

    /**
     * Creates CreasePattern from the file given by
     * reading through it line by line and assigning the correct
     * CreaseTypes and coordinates to the returned CreasePattern,
     * utilizes StreamTokenizer
     *
     * @param file the .cp file in which the CreasePattern is described as an InputStream
     * @return a CreasePattern based on the instructions in the file
     */
    public static CreasePattern createFromFile(InputStream file) {
        CreasePattern cp = new CreasePattern();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file));
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

    public void removeCrease(Crease crease) {
        removeAdjacentCrease(crease.getLine().getStart(), crease);
        removeAdjacentCrease(crease.getLine().getEnd(), crease);
        creases.remove(crease);
    }

    private void removeAdjacentCrease(Point p, Crease crease) {
        if (adjacentCreases.containsKey(p)) {
            adjacentCreases.get(p).remove(crease);
            if (adjacentCreases.get(p).isEmpty()) {
                adjacentCreases.remove(p);
                points.remove(p);
            }
        }
    }

    public void removeLinearPoints() {
        Set<Crease> creasesToRemove = new HashSet<>();
        Set<Crease> creasesToAdd = new HashSet<>();
        for (Point point : getPoints()) {
            var adj = getAdjacentCreases(point);
            if (adj.size() == 2) {
                var line1 = adj.get(0);
                var line2 = adj.get(1);
                if (line1.getType() != line2.getType()) {
                    continue;
                }
                double slope1 = Math.abs(line1.getLine().getSlope());
                double slope2 = Math.abs(line2.getLine().getSlope());
                if (slope2 < 0) {
                    slope2 = -slope2;
                }
                if (Math.abs(slope1-slope2) < 0.00001) {
                    creasesToRemove.add(line1);
                    creasesToRemove.add(line2);
                    Point p1, p2;
                    HashSet<Point> points = new HashSet<>();
                    points.add(line1.getLine().getStart());
                    points.add(line1.getLine().getEnd());
                    points.add(line2.getLine().getStart());
                    points.add(line2.getLine().getEnd());
                    points.remove(point);
                    Object[] p = points.toArray();
                    Crease c = new Crease(new Line((Point)p[0], (Point)p[1]), line1.getType());
                    creasesToAdd.add(c);
                }
            }
        }

        creasesToRemove.forEach(this::removeCrease);
        creasesToAdd.forEach(this::addCrease);
    }

    public void addCrease(Crease crease) {
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
     *
     * @param crease the Crease of which the start and end point get added or replaced
     */
    private void addOrMergePoints(Crease crease) {
        crease.getLine().setStart(addPoint(crease.getLine().getStart()));
        crease.getLine().setEnd(addPoint(crease.getLine().getEnd()));
    }

    /**
     * Either adds p to the points Set or returns a very near Point to p
     *
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
     *
     * @param canvas the Canvas to draw the Crease Pattern on
     * @param scaleX scales the GraphicsContext in the x amount (default = 1)
     * @param scaleY scales the GraphicsContext in the y amount (default = 1)
     */
    public void drawOnCanvas(ResizableCanvas canvas, double scaleX, double scaleY, int cellSize) {
        canvas.setCpScaleX(scaleX);
        canvas.setCpScaleY(scaleY);

        canvas.setCp(this);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        if (canvas.getId() != null && canvas.getId().equals("main")) {
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        graphicsContext.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        graphicsContext.setLineWidth(2);

        drawCreasePattern(canvas, scaleX, scaleY, graphicsContext);
        if (canvas.getId() != null && canvas.getId().equals("main")) canvas.drawGrid(cellSize);
    }

    public void drawOnCanvas(ResizableCanvas canvas, double scaleX, double scaleY) {
        drawOnCanvas(canvas, scaleX, scaleY, 50);
    }

    public void drawOverCanvas(ResizableCanvas canvas, double scaleX, double scaleY) {
        canvas.setCpScaleX(scaleX);
        canvas.setCpScaleY(scaleY);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        graphicsContext.setLineWidth(6);

        drawCreasePattern(canvas, scaleX, scaleY, graphicsContext);
    }

    private void drawCreasePattern(ResizableCanvas canvas, double scaleX, double scaleY, GraphicsContext graphicsContext) {
        for (Crease crease : creases) {
            Color currentColor = switch (crease.getType()) {
                case EDGE -> Color.BLACK;
                case VALLEY -> Color.BLUE;
                case MOUNTAIN -> Color.RED;
            };

            graphicsContext.setStroke(currentColor);

            Point start = crease.getLine().getStart();
            Point end = crease.getLine().getEnd();

            graphicsContext.strokeLine(start.getX() * scaleX, start.getY() * scaleY,
                    end.getX() * scaleX, end.getY() * scaleY);
        }

        graphicsContext.translate(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
    }

    public CreasePattern getDifference(CreasePattern other) {
        CreasePattern diff = new CreasePattern();

        Set<Crease> intersection = new HashSet<>(this.creases);
        intersection.removeAll(other.creases);

        intersection.forEach(crease -> {
            if (crease.getType() != Crease.Type.EDGE) {
                diff.addCrease(crease);
            }
        });
        return diff;
    }

    public CreasePattern copy() {
        CreasePattern copy = new CreasePattern();
        creases.forEach(copy::addCrease);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreasePattern that = (CreasePattern) o;
        return creases.equals(that.creases) && points.equals(that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creases, points);
    }
}
