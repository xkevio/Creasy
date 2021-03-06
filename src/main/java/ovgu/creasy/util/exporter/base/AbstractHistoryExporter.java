package ovgu.creasy.util.exporter.base;

import javafx.scene.Parent;
import ovgu.creasy.ui.elements.CreasePatternCanvas;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHistoryExporter implements IExporter {
    protected List<CreasePatternCanvas> history;

    public abstract Optional<File> open(Parent root);

    @Override
    public abstract boolean export(File file);
}
