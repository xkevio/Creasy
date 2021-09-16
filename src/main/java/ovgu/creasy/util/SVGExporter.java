package ovgu.creasy.util;

import ovgu.creasy.ui.ResizableCanvas;

import java.util.List;

public class SVGExporter extends AbstractExporter {

    public SVGExporter(List<ResizableCanvas> history) {
        this.history = history;
    }

    @Override
    public boolean export() {
        return false;
    }
}
