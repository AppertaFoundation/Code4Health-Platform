package cloud.operon.platform.service.util;

import cloud.operon.platform.domain.FormData;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class that generates a Pdf report for a given composition and {@link cloud.operon.platform.domain.Notification}.
 */
public class PdfReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(PdfReportGenerator.class);
    private static Font TITLE_FONT = new Font(Font.HELVETICA, 20, Font.BOLD);
    private static Font BODY_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL);
    private static Font SUBTITLE_FONT = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static Font FOOTER_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL);
    public static final Font LABEL_FONT = new Font(Font.HELVETICA, 12, Font.ITALIC);

    /**
     * Creates a PDF with information about the movies
     * @param    filename the name of the PDF file that will be created.
     * @throws    DocumentException
     * @throws    IOException
     */
    public static String createPdf(String filename, FormData formData) throws IOException, DocumentException {
        // step 1
        Document document = new Document();
        String reportPath = createTempFile(filename);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportPath));
        // set footer
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase(formData.getFooter(), FOOTER_FONT),
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);

        // add title
        Paragraph title = new Paragraph(formData.getTitle(), TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setBackgroundColor(Color.LIGHT_GRAY);
        titleCell.setMinimumHeight(50);
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.addCell(titleCell);
        titleTable.setSpacingAfter(20);
        titleTable.setWidthPercentage(100);
        document.add(titleTable);

        // add form data as a table
        document.add(createFormTable(formData));

        // add conditional text
        Paragraph conditionalText = new Paragraph(formData.getConditionalSubTitle(), SUBTITLE_FONT);
        conditionalText.setAlignment(Element.ALIGN_CENTER);
        PdfPCell conditionalTitleCell = new PdfPCell(conditionalText);
        conditionalTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
        conditionalTitleCell.setMinimumHeight(50);
        conditionalTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        conditionalTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        PdfPTable conditionalTitleTable = new PdfPTable(1);
        conditionalTitleTable.addCell(conditionalTitleCell);
        conditionalTitleTable.setSpacingAfter(20);
        conditionalTitleTable.setWidthPercentage(100);
        document.add(conditionalTitleTable);

        // loop through conditional body items and add as paragraph
        formData.getConditionalBody().keySet().forEach(key -> {
            try {
                Paragraph cBodyPara = new Paragraph(formData.getConditionalBody().get(key), BODY_FONT);
                cBodyPara.setSpacingAfter(20);
                document.add(cBodyPara);
            } catch (DocumentException e) {
                log.error("Unable to add conditional body. Nested exception is : ", e);
            }
        });
        // step 5
        document.close();
        log.info("Report path = {}", reportPath);
        // return file as input stream
        return reportPath;
    }

    /**
     * Creates the form table from {@link FormData}
     * @return the form table
     */
    public static PdfPTable createFormTable(FormData formData) {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        // now process form items and add cells to table
        PdfPCell cell = new PdfPCell();
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(2);
        formData.getFormItems().forEach(item -> {
            cell.setPhrase(new Phrase(item.getLabel(), LABEL_FONT));
            table.addCell(cell);
            // now add comment in the middle column
            if(item.getComment() != null){
                cell.setPhrase(new Phrase(item.getComment(), BODY_FONT));
                table.addCell(cell);
            }
            //add value
            cell.setPhrase(new Phrase(item.getValue(), BODY_FONT));
            table.addCell(cell);
            // if item has child items, then we add them too but we ident the label
            item.getChildItems().forEach(childItem -> {
                cell.setPhrase(new Phrase(childItem.getLabel(), LABEL_FONT));
                cell.setPaddingLeft(10);
                table.addCell(cell);
                // now reset left padding
                cell.setPaddingLeft(2);
                // now add comment in the middle column
                if(childItem.getComment() != null){
                    cell.setPhrase(new Phrase(childItem.getComment(), BODY_FONT));
                    table.addCell(cell);
                }
                // add child item value
                cell.setPhrase(new Phrase(childItem.getValue(), BODY_FONT));
                table.addCell(cell);
            });
        });

        return table;
    }

    /**
     *     Creates a temporary file that will be deleted on JVM exit.
     */
    private static String createTempFile(String fileName) throws IOException {
        Path path = Files.createTempFile(fileName, ".pdf");
        File file = path.toFile();
        // specify file should be deleted on exit - automatic removal
        file.deleteOnExit();
        return file.getAbsolutePath();
    }

}
