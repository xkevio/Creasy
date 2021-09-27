package ovgu.creasy.util.exporter.cp;

import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import ovgu.creasy.origami.CreasePattern;
import ovgu.creasy.util.exporter.base.AbstractCreasePatternExporter;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class PDFCreasePatternExporter extends AbstractCreasePatternExporter {

    public PDFCreasePatternExporter(CreasePattern creasePattern) {
        this.creasePattern = creasePattern;
    }

    @Override
    public boolean export(File file) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        try {
            PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 400, 400);
            creasePattern.drawOnGraphics2D(pdfBoxGraphics2D);
            pdfBoxGraphics2D.dispose();

            PDFormXObject xObject = pdfBoxGraphics2D.getXFormObject();
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.transform(new Matrix(AffineTransform.getTranslateInstance(100, 200)));
            contentStream.drawForm(xObject);

            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        document.addPage(page);
        try {
            document.save(file.getPath());
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
