package ovgu.creasy.util;

import ovgu.creasy.ui.ResizableCanvas;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class AbstractExporter {
    protected List<ResizableCanvas> history;
    public abstract Optional<File> open();
    public abstract boolean export();
}
