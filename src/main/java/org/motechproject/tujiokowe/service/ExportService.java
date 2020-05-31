package org.motechproject.tujiokowe.service;

import java.util.Map;
import java.util.UUID;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tujiokowe.dto.ExportResult;
import org.motechproject.tujiokowe.dto.ExportStatusResponse;

public interface ExportService {

  UUID exportEntity(String outputFormat, String fileName, Class<?> entityDtoType, Class<?> entityType,  //NO CHECKSTYLE ParameterNumber
      Map<String, String> headerMap, String lookup, String lookupFields, QueryParams queryParams);

  UUID exportEntity(String outputFormat, String fileName, String entityName,
      Map<String, String> headerMap, String lookup, String lookupFields, QueryParams queryParams);

  ExportStatusResponse getExportStatus(UUID exportId);

  ExportResult getExportResults(UUID exportId);

  void cancelExport(UUID exportId);

  void cancelAllExportTasks();
}
