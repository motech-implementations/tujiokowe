package org.motechproject.tujiokowe.service;

import org.motechproject.tujiokowe.domain.Subject;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

  Subject findSubjectBySubjectId(String subjectId);

  Subject create(Subject subject);

  Subject update(Subject subject);

  Subject update(Subject subject, Subject oldSubject);

  void subjectDataChanged(Subject subject);
}
