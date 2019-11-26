package org.motechproject.tujiokowe.service;

import java.util.List;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.Visit;

public interface TujiokoweEnrollmentService {

  void enrollSubject(String subjectId);

  void enrollSubjectToCampaign(String subjectId, String campaignName);

  void enrollOrReenrollSubject(Subject subject);

  void enrollOrReenrollVisits(List<Visit> visits);

  void unenrollSubject(String subjectId);

  void unenrollSubject(String subjectId, String campaignName);

  void completeCampaign(Visit visit);

  void completeCampaign(String subjectId, String campaignName);

  void createEnrollmentOrReenrollCampaign(Visit visit, boolean rollbackCompleted);

  void unenrollAndRemoveEnrollment(Visit visit);
}
