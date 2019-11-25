package org.motechproject.tujiokowe.service;

import java.io.IOException;
import org.motechproject.tujiokowe.dto.VisitRescheduleDto;
import org.motechproject.tujiokowe.web.domain.GridSettings;
import org.motechproject.tujiokowe.web.domain.Records;

public interface VisitRescheduleService {

  Records<VisitRescheduleDto> getVisitsRecords(GridSettings settings) throws IOException;

  VisitRescheduleDto saveVisitReschedule(VisitRescheduleDto visitRescheduleDto,
      Boolean ignoreLimitation);
}
