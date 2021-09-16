package ovgu.creasy.util;

import ovgu.creasy.ui.ResizableCanvas;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class PDFExporter extends AbstractExporter {

    public PDFExporter(List<ResizableCanvas> history) {
        this.history = history;
    }

    @Override
    public Optional<File> open() {
        return Optional.empty();
    }

    @Override
    public boolean export() {
        return false;
    }
}
