package org.motechproject.tujiokowe.utils;

import org.motechproject.tujiokowe.domain.Subject;

public final class SubjectUtil {

    private SubjectUtil() {
    }

    public static Subject createSubject(String subjectId, String name, String phoneNumber) {
        Subject subject = new Subject();
        subject.setSubjectId(subjectId);
        subject.setName(name);
        subject.setPhoneNumber(phoneNumber);
        return subject;
    }
}
