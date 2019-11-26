package org.motechproject.tujiokowe.service;

import java.util.List;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tujiokowe.web.domain.GridSettings;
import org.motechproject.tujiokowe.web.domain.Records;

public interface LookupService {

  <T> List<T> getEntities(Class<T> entityType, GridSettings settings, QueryParams queryParams);

  <T> Records<T> getEntities(Class<T> entityType, String lookup,
      String lookupFields, QueryParams queryParams);

  <T> Records<T> getEntities(Class<T> entityDtoType, Class<?> entityType, String lookup,
      String lookupFields, QueryParams queryParams);

  <T> Records<T> getEntities(String entityClassName, String lookup,
      String lookupFields, QueryParams queryParams);

  List<LookupDto> getAvailableLookups(String entityName);
}