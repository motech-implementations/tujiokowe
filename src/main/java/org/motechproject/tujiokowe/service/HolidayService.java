package org.motechproject.tujiokowe.service;

import org.joda.time.LocalDate;
import org.motechproject.tujiokowe.domain.Holiday;

public interface HolidayService {

  Holiday findByDate(LocalDate date);

  Holiday create(Holiday holiday);

  Holiday update(Holiday holiday);

  void dataChanged(Holiday holiday);
}
