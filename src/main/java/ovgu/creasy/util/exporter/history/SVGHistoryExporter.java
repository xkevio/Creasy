package ovgu.creasy.util.exporter.history;

import javafx.scene.Parent;
import javafx.stage.FileChooser;
import org.jfree.svg.SVGGraphics2D;
import ovgu.creasy.origami.basic.CreasePattern;
import ovgu.creasy.ui.elements.CreasePatternCanvas;
import ovgu.creasy.ui.elements.ResizableCanvas;
import ovgu.creasy.util.exporter.base.AbstractHistoryExporter;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SVGHistoryExporter extends AbstractHistoryExporter {

    public SVGHistoryExporter(List<CreasePatternCanvas> history) {
        this.history = history;
    }

    @Override
    public Optional<File> open(Parent root) {
        FileChooser exportSvg = new FileChooser();
        exportSvg.setTitle("Save as .svg");
        exportSvg.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scalable Vector Graphics", "*.svg"));

        File fileSvg = exportSvg.showSaveDialog(root.getScene().getWindow());

        return fileSvg != null ? Optional.of(fileSvg) : Optional.empty();
    }

    @Override
    public boolean export(File file) {
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(this.history.size() * 450, 400);

        List<CreasePatternCanvas> resizableCanvas = this.history;
        for (int i = 0, resizableCanvasSize = resizableCanvas.size(); i < resizableCanvasSize; i++) {
            CreasePatternCanvas canvas = resizableCanvas.get((resizableCanvasSize - 1) - i);
            canvas.getCp().drawOnGraphics2D(svgGraphics2D);

            if (i > 0) {
                CreasePattern prev = resizableCanvas.get((resizableCanvasSize - 1) - i + 1).getCp();
                CreasePattern diff = canvas.getCp().getDifference(prev);

                svgGraphics2D.setStroke(new BasicStroke(5));
                diff.drawOnGraphics2D(svgGraphics2D);
                svgGraphics2D.setStroke(new BasicStroke(1));
            }

            if (i < resizableCanvasSize - 1) {
                drawArrow(svgGraphics2D);
                svgGraphics2D.translate(450, 0);
            }
        }

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

    private void drawArrow(SVGGraphics2D svgGraphics2D) {
        svgGraphics2D.setColor(Color.BLACK);
        svgGraphics2D.fillRect(400, 200, 45, 10);
        svgGraphics2D.fillPolygon(new int[] {445, 450, 445}, new int[] {195, 205, 215}, 3);
    }
}
