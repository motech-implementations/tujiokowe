package org.motechproject.tujiokowe.service;

import org.joda.time.LocalDate;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.Visit;

/**
 * Service interface for CRUD on Visit
 */
public interface VisitService {

  Visit create(Visit visit);

  Visit update(Visit visit);

  void delete(Visit visit);

  void createVisitsForSubject(Subject subject);

  void calculateVisitsPlannedDates(Subject subject);

  void recalculateBoostRelatedVisitsPlannedDates(Subject subject);

  void removeVisitsPlannedDates(Subject subject);

  void recalculateVisitsForHoliday(LocalDate holidayDate);
}
