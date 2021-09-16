package ovgu.creasy.util;

import ovgu.creasy.ui.ResizableCanvas;

import java.util.List;

public class PDFExporter extends AbstractExporter {

    public PDFExporter(List<ResizableCanvas> history) {
        this.history = history;
    }

    @Override
    public boolean export() {
        return false;
    }
}
