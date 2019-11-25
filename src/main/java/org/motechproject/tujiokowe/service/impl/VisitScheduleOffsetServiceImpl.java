package org.motechproject.tujiokowe.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.motechproject.tujiokowe.domain.VisitScheduleOffset;
import org.motechproject.tujiokowe.domain.enums.VisitType;
import org.motechproject.tujiokowe.repository.VisitScheduleOffsetDataService;
import org.motechproject.tujiokowe.service.VisitScheduleOffsetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("visitScheduleOffsetService")
public class VisitScheduleOffsetServiceImpl implements VisitScheduleOffsetService {

  @Autowired
  private VisitScheduleOffsetDataService visitScheduleOffsetDataService;

  @Override
  public VisitScheduleOffset findByVisitType(VisitType visitType) {
    return visitScheduleOffsetDataService.findByVisitType(visitType);
  }

  @Override
  public List<VisitScheduleOffset> getAll() {
    return visitScheduleOffsetDataService.retrieveAll();
  }

  @Override
  public Map<VisitType, VisitScheduleOffset> getAllAsMap() {
    Map<VisitType, VisitScheduleOffset> visitTypeMap = new HashMap<>();
    List<VisitScheduleOffset> visitScheduleOffsetList = visitScheduleOffsetDataService
        .retrieveAll();

    if (visitScheduleOffsetList != null) {
      for (VisitScheduleOffset offset : visitScheduleOffsetList) {
        visitTypeMap.put(offset.getVisitType(), offset);
      }
    }

    return visitTypeMap;
  }
}
