package ovgu.creasy.origami;

public class OrigamiModel {
    private CreasePattern finishedCp;
    private Diagram diagram;

    public OrigamiModel(CreasePattern finishedCp) {
        this.finishedCp = finishedCp;
        this.diagram = new Diagram();
    }

    public CreasePattern getFinishedCp() {
        return finishedCp;
    }

    public void setFinishedCp(CreasePattern finishedCp) {
        this.finishedCp = finishedCp;
    }

    public Diagram getDiagram() {
        return diagram;
    }
}
