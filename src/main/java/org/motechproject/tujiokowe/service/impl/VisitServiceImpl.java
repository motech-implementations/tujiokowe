package org.motechproject.tujiokowe.service.impl;

import java.util.List;
import java.util.Map;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.motechproject.tujiokowe.domain.Holiday;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.domain.VisitScheduleOffset;
import org.motechproject.tujiokowe.domain.enums.VisitType;
import org.motechproject.tujiokowe.repository.HolidayDataService;
import org.motechproject.tujiokowe.repository.VisitDataService;
import org.motechproject.tujiokowe.service.TujiokoweEnrollmentService;
import org.motechproject.tujiokowe.service.VisitScheduleOffsetService;
import org.motechproject.tujiokowe.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link org.motechproject.tujiokowe.service.VisitService} interface. Uses
 * {@link org.motechproject.tujiokowe.repository.VisitDataService} in order to retrieve and persist
 * records.
 */
@Service("visitService")
public class VisitServiceImpl implements VisitService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VisitServiceImpl.class);

  @Autowired
  private VisitDataService visitDataService;

  @Autowired
  private VisitScheduleOffsetService visitScheduleOffsetService;

  @Autowired
  private TujiokoweEnrollmentService tujiokoweEnrollmentService;

  @Autowired
  private HolidayDataService holidayDataService;

  @Override
  public Visit create(Visit visit) {
    return visitDataService.create(visit);
  }

  @Override
  public Visit update(Visit visit) {
    return visitDataService.update(visit);
  }

  @Override
  public void delete(Visit visit) {
    visitDataService.delete(visit);
  }

  @Override
  public void createVisitsForSubject(Subject subject) {
    for (VisitType visitType : VisitType.values()) {
      subject.addVisit(visitDataService.create(new Visit(subject, visitType)));
    }

    calculateVisitsPlannedDates(subject);
  }

  @Override
  public void calculateVisitsPlannedDates(Subject subject) {
    LocalDate primeVacDate = subject.getPrimeVaccinationDate();
    LocalDate boostVacDate = subject.getBoostVaccinationDate();

    if (primeVacDate != null) {
      if (boostVacDate == null) {
        boostVacDate = calculateBoostVacDate(primeVacDate);
      }

      for (Visit visit : subject.getVisits()) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
          visit.setDate(primeVacDate);
        } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
          if (subject.getBoostVaccinationDate() != null) {
            visit.setDate(boostVacDate);

            tujiokoweEnrollmentService.completeCampaign(visit);
          } else {
            visit.setDate(null);
            visit.setDateProjected(boostVacDate);
          }
        }

        visitDataService.update(visit);
      }

      if (subject.getBoostVaccinationDate() != null) {
        tujiokoweEnrollmentService.enrollOrReenrollCampaignCompletedCampaign(subject);
      } else {
        tujiokoweEnrollmentService.removeCampaignCompletedCampaign(subject.getSubjectId());
      }

      tujiokoweEnrollmentService.enrollOrReenrollSubject(subject);
    }
  }

  @Override
  public void recalculateBoostRelatedVisitsPlannedDates(Subject subject) {
    LocalDate boostVacDate = subject.getBoostVaccinationDate();
    LocalDate primeVacDate = subject.getPrimeVaccinationDate();

    if (primeVacDate != null) {
      if (boostVacDate == null) {
        boostVacDate = calculateBoostVacDate(primeVacDate);
      }

      for (Visit visit : subject.getVisits()) {
        if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
          if (subject.getBoostVaccinationDate() != null) {
            visit.setDate(boostVacDate);

            tujiokoweEnrollmentService.completeCampaign(visit);
          } else {
            visit.setDate(null);
            visit.setDateProjected(boostVacDate);
          }

          visitDataService.update(visit);
        }
      }

      if (subject.getBoostVaccinationDate() != null) {
        tujiokoweEnrollmentService.enrollOrReenrollCampaignCompletedCampaign(subject);
      } else {
        tujiokoweEnrollmentService.removeCampaignCompletedCampaign(subject.getSubjectId());
      }

      tujiokoweEnrollmentService.enrollOrReenrollSubject(subject);
    }
  }

  @Override
  public void removeVisitsPlannedDates(Subject subject) {
    for (Visit visit : subject.getVisits()) {
      if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
        visit.setDate(null);
      } else {
        visit.setDateProjected(null);
      }

      tujiokoweEnrollmentService.unenrollAndRemoveEnrollment(visit);
      visitDataService.update(visit);
    }
  }

  @Override
  public void recalculateVisitsForHoliday(LocalDate holidayDate) {
    List<Visit> visits = visitDataService
        .findByVisitTypeAndActualDateAndPlannedDate(VisitType.BOOST_VACCINATION_DAY, null,
            holidayDate);

    for (Visit visit : visits) {
      LocalDate plannedDate = recalculateBoostVacDateForHolidays(holidayDate.plusDays(1));
      visit.setDateProjected(plannedDate);
      visitDataService.update(visit);
    }

    tujiokoweEnrollmentService.enrollOrReenrollVisits(visits);
  }

  private LocalDate calculateBoostVacDate(LocalDate primeVacDate) {
    Map<VisitType, VisitScheduleOffset> offsetMap = visitScheduleOffsetService.getAllAsMap();
    VisitScheduleOffset offset = offsetMap.get(VisitType.BOOST_VACCINATION_DAY);

    LocalDate boostVacDate = primeVacDate.plusDays(offset.getTimeOffset());

    return recalculateBoostVacDateForHolidays(boostVacDate);
  }

  private LocalDate recalculateBoostVacDateForHolidays(LocalDate date) {
    LocalDate boostVacDate = date;
    if (DateTimeConstants.SATURDAY == boostVacDate.getDayOfWeek()) {
      boostVacDate = boostVacDate.plusDays(2);
    } else if (DateTimeConstants.SUNDAY == boostVacDate.getDayOfWeek()) {
      boostVacDate = boostVacDate.plusDays(1);
    }

    Holiday holiday = holidayDataService.findByHolidayDate(boostVacDate);

    if (holiday != null) {
      return recalculateBoostVacDateForHolidays(boostVacDate.plusDays(1));
    }

    return boostVacDate;
  }
}
