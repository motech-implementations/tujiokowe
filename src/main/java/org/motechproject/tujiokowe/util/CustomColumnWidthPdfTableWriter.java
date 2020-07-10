package org.motechproject.tujiokowe.util;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.io.OutputStream;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomColumnWidthPdfTableWriter extends PdfTableWriter {

  private static final float MARGIN = 5f;
  // Object initialization for log
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomColumnWidthPdfTableWriter.class);

  private static final float TABLE_WIDTH = 550f;

  public CustomColumnWidthPdfTableWriter(OutputStream outputStream) {
    super(outputStream);
  }

  @Override
  public void writeHeader(String[] headers) throws IOException {
    super.writeHeader(headers);
    dataTable.setTotalWidth(TABLE_WIDTH);

    try {
      dataTable.setWidths(calculateColumnWidths(headers));
    } catch (DocumentException e) {
      LOGGER.error("writeHeader - DocumentException - Reason : " + e.getLocalizedMessage(), e);
    }
  }

  private float[] calculateColumnWidths(String[] headers) {
    float spaceForTheRestOfColumns = TABLE_WIDTH;
    int fixedWidthColumnsCount = 0;
    for (int i = 0; i < dataTable.getNumberOfColumns(); i++) {
      if (TujiokoweConstants.REPORT_COLUMN_WIDTHS.containsKey(headers[i])) {
        spaceForTheRestOfColumns -= TujiokoweConstants.REPORT_COLUMN_WIDTHS.get(headers[i]);
        fixedWidthColumnsCount++;
      }
    }

    float relativeWidth = spaceForTheRestOfColumns
        / (dataTable.getNumberOfColumns() - fixedWidthColumnsCount);
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
