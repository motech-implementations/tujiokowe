package org.motechproject.tujiokowe.service.impl;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.repository.SubjectDataService;
import org.motechproject.tujiokowe.service.SubjectService;
import org.motechproject.tujiokowe.service.TujiokoweEnrollmentService;
import org.motechproject.tujiokowe.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link org.motechproject.tujiokowe.service.SubjectService} interface. Uses
 * {@link org.motechproject.tujiokowe.repository.SubjectDataService} in order to retrieve and
 * persist records.
 */
@Service("subjectService")
public class SubjectServiceImpl implements SubjectService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubjectServiceImpl.class);

  @Autowired
  private SubjectDataService subjectDataService;

  @Autowired
  private VisitService visitService;

  @Autowired
  private TujiokoweEnrollmentService tujiokoweEnrollmentService;

  @Override
  public Subject findSubjectBySubjectId(String subjectId) {
    return subjectDataService.findBySubjectId(subjectId);
  }

  @Override
  public Subject create(Subject subject) {
    subjectDataService.create(subject);
    visitService.createVisitsForSubject(subject);

    return subject;
  }

  @Override
  public Subject update(Subject subject) {
    subjectDataChanged(subject);

    return subjectDataService.update(subject);
  }

  @Override
  public Subject update(Subject subject, Subject oldSubject) {
    subjectDataChanged(subject, oldSubject, subject);

    return subjectDataService.update(subject);
  }

  @Override
  public void subjectDataChanged(Subject subject) {
    Subject oldSubject = findSubjectBySubjectId(subject.getSubjectId());
    subjectDataChanged(subject, oldSubject, oldSubject);
  }

  private void subjectDataChanged(Subject newSubject, Subject oldSubject, Subject subject) {
    if (oldSubject != null) {
      if (oldSubject.getPrimeVaccinationDate() != null
          && newSubject.getPrimeVaccinationDate() == null) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        visitService.removeVisitsPlannedDates(subject);
      } else if (!Objects
          .equals(oldSubject.getPrimeVaccinationDate(), newSubject.getPrimeVaccinationDate())) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        visitService.calculateVisitsPlannedDates(subject);
      }

      if (!Objects
          .equals(oldSubject.getBoostVaccinationDate(), newSubject.getBoostVaccinationDate())) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        subject.setBoostVaccinationDate(newSubject.getBoostVaccinationDate());
        visitService.recalculateBoostRelatedVisitsPlannedDates(subject);
      }

      if ((StringUtils.isBlank(oldSubject.getPhoneNumber()))
          && StringUtils.isNotBlank(newSubject.getPhoneNumber())) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        subject.setBoostVaccinationDate(newSubject.getBoostVaccinationDate());

        tujiokoweEnrollmentService.enrollOrReenrollSubject(subject);
      } else if (StringUtils.isNotBlank(oldSubject.getPhoneNumber())
          && StringUtils.isBlank(newSubject.getPhoneNumber())) {
        tujiokoweEnrollmentService.unenrollAndRemoveEnrollment(subject);
      }
    }
  }
}
