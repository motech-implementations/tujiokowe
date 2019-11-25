package org.motechproject.tujiokowe.repository;

import org.joda.time.LocalDate;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tujiokowe.domain.Holiday;

public interface HolidayDataService extends MotechDataService<Holiday> {

  @Lookup
  Holiday findByHolidayDate(@LookupField(name = "holidayDate") LocalDate date);
}
