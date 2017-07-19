package cloud.operon.platform.service.util;

import cloud.operon.platform.domain.FormData;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class that generates a Pdf report for a given composition and {@link cloud.operon.platform.domain.Notification}.
 */
public class PdfReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(PdfReportGenerator.class);

    /**
     * Creates a PDF with information about the movies
     * @param    filename the name of the PDF file that will be created.
     * @throws    DocumentException
     * @throws    IOException
     */
    public static InputStream createPdf(String filename, FormData formData) throws IOException, DocumentException {
        // step 1
        Document document = new Document();
        String reportPath = createTempFile(filename);
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(reportPath));
        // step 3
        document.open();
        // step 4
        document.add(createFormTable(formData));
        // step 5
        document.close();
        log.info("Report path = {}", reportPath);
        // return file as input stream
        return new FileInputStream(reportPath);
    }

    /**
     * Creates the form table from {@link FormData}
     * @return the form table
     */
    public static PdfPTable createFormTable(FormData formData) {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell for title - with colspan 3
        cell = new PdfPCell(new Phrase(formData.getTitle()));
        cell.setColspan(3);
        table.addCell(cell);
        // now process form items and add cells to table
        formData.getFormItems().forEach(item -> {
            table.addCell(item.getLabel());
            // now add comment in the middle column
            if(item.getComment() != null){
                table.addCell(item.getComment());
            }
            table.addCell(item.getValue());
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
