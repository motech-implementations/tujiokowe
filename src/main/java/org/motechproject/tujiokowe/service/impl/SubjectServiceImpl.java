package org.motechproject.tujiokowe.service.impl;

import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.domain.enums.VisitType;
import org.motechproject.tujiokowe.helper.IvrHelper;
import org.motechproject.tujiokowe.repository.SubjectDataService;
import org.motechproject.tujiokowe.service.SubjectService;
import org.motechproject.tujiokowe.service.TujiokoweEnrollmentService;
import org.motechproject.tujiokowe.service.VisitService;
import org.motechproject.tujiokowe.util.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

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

  @Autowired
  private IvrHelper ivrHelper;

  @Override
  public Subject findSubjectBySubjectId(String subjectId) {
    return subjectDataService.findBySubjectId(subjectId);
  }

  @Override
  public Subject create(Subject subject) {
    String ivrId = ivrHelper.createSubscriber(subject);

    subject.setIvrId(ivrId);

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
  public void subjectDataChanged(final Subject subject) {
    subjectDataService.doInTransaction(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Subject oldSubject = subjectDataService.findBySubjectId(subject.getSubjectId());
        subjectDataChanged(subject, oldSubject, oldSubject);
      }
    });
  }

  private void subjectDataChanged(Subject newSubject, Subject oldSubject, Subject subject) { //NO CHECKSTYLE CyclomaticComplexity
    if (oldSubject != null) {
      if (oldSubject.getPrimeVaccinationDate() != null && newSubject.getPrimeVaccinationDate() == null) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        visitService.removeVisitsPlannedDates(subject);
      } else if (!Objects.equals(oldSubject.getPrimeVaccinationDate(), newSubject.getPrimeVaccinationDate())) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        subject.setBoostVaccinationDate(newSubject.getBoostVaccinationDate());
        subject.setBoostPlannedDate(newSubject.getBoostPlannedDate());
        visitService.calculateVisitsPlannedDates(subject);
      }

      if (!Objects.equals(oldSubject.getBoostVaccinationDate(), newSubject.getBoostVaccinationDate())
          || !Objects.equals(oldSubject.getBoostPlannedDate(), newSubject.getBoostPlannedDate())) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        subject.setBoostVaccinationDate(newSubject.getBoostVaccinationDate());
        subject.setBoostPlannedDate(newSubject.getBoostPlannedDate());
        visitService.recalculateBoostRelatedVisitsPlannedDates(subject);
      }

      checkSlotChange(newSubject, oldSubject, subject);

      if (PhoneValidator.isNotValid(oldSubject.getPhoneNumber())
          && PhoneValidator.isValid(newSubject.getPhoneNumber())) {
        subject.setPrimeVaccinationDate(newSubject.getPrimeVaccinationDate());
        subject.setBoostVaccinationDate(newSubject.getBoostVaccinationDate());
        subject.setSlot(newSubject.getSlot());

        tujiokoweEnrollmentService.enrollOrReenrollSubject(subject);
      } else if (PhoneValidator.isValid(oldSubject.getPhoneNumber())
          && PhoneValidator.isNotValid(newSubject.getPhoneNumber())) {
        tujiokoweEnrollmentService.unenrollAndRemoveEnrollment(subject);
      }

      updateSubscriber(newSubject, oldSubject);
    }
  }

  private void checkSlotChange(Subject newSubject, Subject oldSubject, Subject subject) {
    if (newSubject.getPrimeVaccinationDate() != null && newSubject.getBoostVaccinationDate() == null
        && PhoneValidator.isValid(newSubject.getPhoneNumber())) {
      if (!StringUtils.equals(oldSubject.getSlot(), newSubject.getSlot())) {
        for (Visit visit : subject.getVisits()) {
          if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
            String newSlot = newSubject.getSlot();

            subject.setSlot(oldSubject.getSlot());
            tujiokoweEnrollmentService.unenrollAndRemoveEnrollment(visit);

            subject.setSlot(newSlot);
            tujiokoweEnrollmentService.enrollOrReenrollSubject(subject);
          }
        }
      }
    }
  }

  private void updateSubscriber(Subject newSubject, Subject oldSubject) {
    if (!StringUtils.equals(oldSubject.getPhoneNumber(), newSubject.getPhoneNumber())
        || !StringUtils.equals(oldSubject.getName(), newSubject.getName())) {
      String ivrId = ivrHelper.updateSubscriber(newSubject);
      newSubject.setIvrId(ivrId);
    }
  }
}
