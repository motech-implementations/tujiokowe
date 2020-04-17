package org.motechproject.tujiokowe.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;

public interface ExportService {

  void exportEntityToPDF(OutputStream outputStream, Class<?> entityDtoType, Class<?> entityType,
      Map<String, String> headerMap, String lookup, String lookupFields, QueryParams queryParams)
      throws IOException;

  void exportEntityToCSV(Writer writer, Class<?> entityDtoType, Class<?> entityType,
      Map<String, String> headerMap,
      String lookup, String lookupFields, QueryParams queryParams) throws IOException;

  void exportEntityToExcel(OutputStream outputStream, Class<?> entityDtoType, Class<?> entityType,
      Map<String, String> headerMap, String lookup, String lookupFields, QueryParams queryParams)
      throws IOException;

  <T> void exportEntity(List<T> entities, Map<String, String> headerMap, TableWriter tableWriter)
      throws IOException;

}
