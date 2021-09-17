package ovgu.creasy.util;

import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import ovgu.creasy.geom.Point;
import ovgu.creasy.origami.Crease;
import ovgu.creasy.ui.ResizableCanvas;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PDFExporter extends AbstractExporter {

    public PDFExporter(List<ResizableCanvas> history) {
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

        List<ResizableCanvas> resizableCanvas = this.history;
        for (int i = 0; i < this.history.size(); i++) {
            ResizableCanvas canvas = resizableCanvas.get((resizableCanvas.size() - 1) - i);
            PDPage page = new PDPage(PDRectangle.A4);
            try {
                PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 400, 400);
                pdfBoxGraphics2D.setColor(Color.BLACK);

                for (Crease crease : canvas.getCp().getCreases()) {
                    Color color = switch (crease.getType()) {
                        case EDGE -> Color.BLACK;
                        case VALLEY -> Color.BLUE;
                        case MOUNTAIN -> Color.RED;
                    };

                    pdfBoxGraphics2D.setColor(color);
                    // TODO: draw difference over it with thicker stroke

                    Point start = crease.getLine().getStart();
                    Point end = crease.getLine().getEnd();
                    pdfBoxGraphics2D.draw(new Line2D.Double(start.getX() + 200, start.getY() + 200,
                            end.getX() + 200, end.getY() + 200));
                }
                pdfBoxGraphics2D.dispose();

                PDFormXObject xObject = pdfBoxGraphics2D.getXFormObject();
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                Matrix translate = new Matrix();
                translate.translate(100, 200);

                contentStream.transform(translate);
                contentStream.drawForm(xObject);

                drawText((i + 1) + ". step", contentStream);

                contentStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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

    private void drawText(String text, PDPageContentStream contentStream) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);

        contentStream.newLineAtOffset(25, 500);
        contentStream.showText(text);

        contentStream.endText();
    }
}
