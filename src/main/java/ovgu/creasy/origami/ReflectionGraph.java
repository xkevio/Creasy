package ovgu.creasy.origami;

import ovgu.creasy.geom.Point;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ReflectionGraph {
    private static double EPS = 0.0000001;
    private CreasePattern cp;
    private Map<Crease, Collection<Crease>> reflectionCreases;

    public ReflectionGraph(CreasePattern cp) {
        this.cp = cp;
        for (Point point : cp.getPoints()) {
            findReflectionCreases(cp.getAdjacentCreases(point), point);
        }
    }

    private void findReflectionCreases(List<Crease> adjacentCreases, Point commonPoint) {
        for (int i = 0; i < adjacentCreases.size(); i++) {
            double alternatingAngle = 0;
            int j = i+1;
            while (j < i) {
                double angle = getAngle(
                        getPrevious(j, adjacentCreases),
                        adjacentCreases.get(j),
                        commonPoint);
                if (i % 2 == 0) {
                    alternatingAngle += angle;
                } else {
                    alternatingAngle -= angle;
                }
                if (Math.abs(alternatingAngle) <= EPS) {
                    addReflectionCrease(adjacentCreases.get(i), adjacentCreases.get(j));
                }
                j++;
                j %= adjacentCreases.size();
            }
        }
    }

    private Crease getPrevious(int index, List<Crease> creases) {
        return creases.get((index+creases.size()-1)%creases.size());
    }

    private double getAngle(Crease crease1, Crease crease2, Point commonPoint) {
        Point p2 = crease1.getLine().getEnd().equals(commonPoint) ? crease1.getLine().getStart() : crease1.getLine().getEnd();
        Point p3 = crease2.getLine().getEnd().equals(commonPoint) ? crease2.getLine().getStart() : crease2.getLine().getEnd();
        double x1 = p2.getX() - commonPoint.getX();
        double y1 = p2.getY() - commonPoint.getY();
        double x2 = p3.getX() - commonPoint.getX();
        double y2 = p3.getY() - commonPoint.getY();
        return Math.acos(
                (x1*x2 + y1*y2) /
                Math.sqrt(x1*x1+y1*y1) * Math.sqrt(x2*x2+y2*y2)
        );
    }

    private void addReflectionCrease(Crease crease1, Crease crease2) {
        if (!reflectionCreases.containsKey(crease1)) {
            reflectionCreases.put(crease1, new HashSet<>());
        }
        reflectionCreases.get(crease1).add(crease2);
        if (!reflectionCreases.containsKey(crease2)) {
            reflectionCreases.put(crease2, new HashSet<>());
        }
        reflectionCreases.get(crease2).add(crease1);
    }
}
