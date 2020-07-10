package org.motechproject.tujiokowe.service;

import java.util.List;
import java.util.UUID;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tujiokowe.dto.ExportField;
import org.motechproject.tujiokowe.dto.ExportStatusResponse;

public interface ExportService {

  UUID exportEntity(String outputFormat, String fileName, Class<?> entityDtoType, Class<?> entityType,  //NO CHECKSTYLE ParameterNumber
      List<ExportField> exportFields, String lookup, String lookupFields, QueryParams queryParams);

  UUID exportEntity(String outputFormat, String fileName, String entityName,
      List<ExportField> exportFields, String lookup, String lookupFields, QueryParams queryParams);

  ExportStatusResponse getExportStatus(UUID exportId);

  void cancelExport(UUID exportId);

  void cancelAllExportTasks();
}
