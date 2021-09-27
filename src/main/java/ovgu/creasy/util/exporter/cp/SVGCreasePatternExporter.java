package ovgu.creasy.util.exporter.cp;

import org.jfree.svg.SVGGraphics2D;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.util.exporter.base.AbstractCreasePatternExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SVGCreasePatternExporter extends AbstractCreasePatternExporter {

    public SVGCreasePatternExporter(CreasePattern creasePattern) {
        this.creasePattern = creasePattern;
    }

    @Override
    public boolean export(File file) {
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(400, 400);
        creasePattern.drawOnGraphics2D(svgGraphics2D);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(svgGraphics2D.getSVGDocument());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
