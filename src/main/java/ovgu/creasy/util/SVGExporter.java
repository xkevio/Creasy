package ovgu.creasy.util;

import javafx.scene.Parent;
import javafx.stage.FileChooser;
import ovgu.creasy.ui.ResizableCanvas;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.jfree.svg.*;

public class SVGExporter extends AbstractExporter {

    public SVGExporter(List<ResizableCanvas> history) {
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


        this.history.forEach(canvas -> {
            SVGGraphics2D svg = new SVGGraphics2D(200,200);

            // TODO: see PDFExporter, it is similar

        });


        return true;
    }
}
