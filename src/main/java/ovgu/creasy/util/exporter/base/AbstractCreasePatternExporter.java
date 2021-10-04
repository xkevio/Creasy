package ovgu.creasy.util.exporter.base;

import ovgu.creasy.origami.basic.CreasePattern;

import java.io.File;

public abstract class AbstractCreasePatternExporter implements IExporter {

    protected CreasePattern creasePattern;

    @Override
    public abstract boolean export(File file);
}
