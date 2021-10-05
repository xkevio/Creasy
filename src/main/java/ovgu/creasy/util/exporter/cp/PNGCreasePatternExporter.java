package ovgu.creasy.util.exporter.cp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import ovgu.creasy.ui.elements.CreasePatternCanvas;
import ovgu.creasy.util.exporter.base.AbstractCreasePatternExporter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PNGCreasePatternExporter extends AbstractCreasePatternExporter {

    private final CreasePatternCanvas resizableCanvas;

    public PNGCreasePatternExporter(CreasePatternCanvas resizableCanvas) {
        this.resizableCanvas = resizableCanvas;
    }

    @Override
    public boolean export(File file) {
        WritableImage image = resizableCanvas.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
