package ovgu.creasy.util;

import ovgu.creasy.ui.ResizableCanvas;

import java.util.List;

public abstract class AbstractExporter {

    protected List<ResizableCanvas> history;
    public abstract boolean export();
}
