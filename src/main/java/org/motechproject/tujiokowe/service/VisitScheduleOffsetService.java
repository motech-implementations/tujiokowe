package org.motechproject.tujiokowe.service;

import java.util.List;
import java.util.Map;
import org.motechproject.tujiokowe.domain.VisitScheduleOffset;
import org.motechproject.tujiokowe.domain.enums.VisitType;


public interface VisitScheduleOffsetService {

  VisitScheduleOffset findByVisitType(VisitType visitType);

  List<VisitScheduleOffset> getAll();

  Map<VisitType, VisitScheduleOffset> getAllAsMap();
}
