package ovgu.creasy.util;

import javafx.scene.Parent;
import ovgu.creasy.ui.ResizableCanvas;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class AbstractExporter {
    protected List<ResizableCanvas> history;
    public abstract Optional<File> open(Parent root);
    public abstract boolean export(File file);
}
