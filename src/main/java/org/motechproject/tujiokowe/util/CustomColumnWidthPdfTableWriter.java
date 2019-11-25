package org.motechproject.tujiokowe.util;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.template.PdfBasicTemplate;

public class CustomColumnWidthPdfTableWriter extends PdfTableWriter {

  private static final float MARGIN = 5f;
  // Object initialization for log
  private static Logger log = Logger.getLogger(CustomColumnWidthPdfTableWriter.class.getName());
  private float totalWidth;

  public CustomColumnWidthPdfTableWriter(PdfBasicTemplate template) {
    super(template);
    totalWidth = template.getNextPageRectangle().getWidth() - 2 * MARGIN;
  }

  @Override
  public void writeHeader(String[] headers) throws IOException {
    super.writeHeader(headers);
    dataTable.setTotalWidth(totalWidth);
    try {
      dataTable.setWidths(calculateColumnWidths(headers));
    } catch (DocumentException e) {
      log.error("writeHeader - DocumentException - Reason : " + e.getLocalizedMessage(), e);
    }
  }

  private float[] calculateColumnWidths(String[] headers) throws DocumentException {
    float spaceForTheRestOfColumns = dataTable.getTotalWidth();
    int numberOfColumnsWithFixedWidth = 0;
    for (int i = 0; i < dataTable.getNumberOfColumns(); i++) {
      if (TujiokoweConstants.REPORT_COLUMN_WIDTHS.containsKey(headers[i])) {
        spaceForTheRestOfColumns -= TujiokoweConstants.REPORT_COLUMN_WIDTHS.get(headers[i]);
        numberOfColumnsWithFixedWidth++;
      }
    }

    float relativeWidth = spaceForTheRestOfColumns
        / (dataTable.getNumberOfColumns() - numberOfColumnsWithFixedWidth);
    float[] allWidths = new float[dataTable.getNumberOfColumns()];
    for (int i = 0; i < dataTable.getNumberOfColumns(); i++) {
      if (TujiokoweConstants.REPORT_COLUMN_WIDTHS.containsKey(headers[i])) {
        allWidths[i] = TujiokoweConstants.REPORT_COLUMN_WIDTHS.get(headers[i]);
      } else {
        allWidths[i] = relativeWidth;
      }
    }

    return allWidths;
  }
}
