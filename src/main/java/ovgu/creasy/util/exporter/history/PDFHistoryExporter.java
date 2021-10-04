package ovgu.creasy.util.exporter.history;

import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import ovgu.creasy.origami.basic.CreasePattern;
import ovgu.creasy.ui.elements.CreasePatternCanvas;
import ovgu.creasy.ui.elements.ResizableCanvas;
import ovgu.creasy.util.exporter.base.AbstractHistoryExporter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PDFHistoryExporter extends AbstractHistoryExporter {

    public PDFHistoryExporter(List<CreasePatternCanvas> history) {
        this.history = history;
    }

    @Override
    public Optional<File> open(Parent root) {
        FileChooser exportPdf = new FileChooser();
        exportPdf.setTitle("Save as .pdf");
        exportPdf.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"));

        File filePdf = exportPdf.showSaveDialog(root.getScene().getWindow());

        return filePdf != null ? Optional.of(filePdf) : Optional.empty();
    }

    @Override
    public boolean export(File file) {
        PDDocument document = new PDDocument();

        List<CreasePatternCanvas> resizableCanvas = this.history;
        for (int i = 0; i < this.history.size(); i++) {
            CreasePatternCanvas canvas = resizableCanvas.get((resizableCanvas.size() - 1) - i);
            PDPage page = new PDPage(PDRectangle.A4);
            try {
                PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 400, 400);

                // first draw normal crease pattern
                canvas.getCp().drawOnGraphics2D(pdfBoxGraphics2D);
                // then draw thick difference over it
                if (i > 0) {
                    pdfBoxGraphics2D.setStroke(new BasicStroke(5));

                    CreasePattern prev = resizableCanvas.get(resizableCanvas.size() - 1 - i + 1).getCp();
                    CreasePattern diff = canvas.getCp().getDifference(prev);

                    diff.drawOnGraphics2D(pdfBoxGraphics2D);
                }

                pdfBoxGraphics2D.dispose();

                PDFormXObject xObject = pdfBoxGraphics2D.getXFormObject();
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                Matrix translate = new Matrix();
                translate.translate(100, 200);

                contentStream.transform(translate);
                contentStream.drawForm(xObject);

                drawText((i + 1) + ". step", 25, 500, contentStream);

                if (i == 0) {
                    drawText("Made with Creasy", 300, 500, contentStream);
                    drawText("Mountain Fold", 320, 460, contentStream);
                    drawText("Valley Fold", 320, 440, contentStream);

                    contentStream.setNonStrokingColor(Color.RED);
                    contentStream.addRect(300, 460, 10, 10);
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.BLUE);
                    contentStream.addRect(300, 440, 10, 10);
                    contentStream.fill();
                }

                contentStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            document.addPage(page);
        }

        try {
            document.save(file.getPath());
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void drawText(String text, int x, int y, PDPageContentStream contentStream) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);

        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);

        contentStream.endText();
    }
}
