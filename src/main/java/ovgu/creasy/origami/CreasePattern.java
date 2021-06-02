package ovgu.creasy.origami;

import oripa.OriLineProxy;
import oripa.DataSet;
import ovgu.creasy.geom.Line;
import ovgu.creasy.geom.Point;
import ovgu.creasy.origami.Crease;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.sql.SQLOutput;
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
                    case 2:
                        type = Crease.Type.MOUNTAIN;
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
