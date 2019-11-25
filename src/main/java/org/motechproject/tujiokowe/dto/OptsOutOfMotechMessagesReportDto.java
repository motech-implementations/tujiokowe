package org.motechproject.tujiokowe.dto;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.SubjectEnrollments;
import org.motechproject.tujiokowe.util.serializer.CustomDateSerializer;
import org.motechproject.tujiokowe.util.serializer.CustomSubjectSerializer;

@JsonAutoDetect
public class OptsOutOfMotechMessagesReportDto {

  @JsonProperty
  @JsonSerialize(using = CustomSubjectSerializer.class)
  @Getter
  private Subject subject;

  @JsonProperty
  @JsonSerialize(using = CustomDateSerializer.class)
  @Getter
  private LocalDate dateOfUnenrollment;

  public OptsOutOfMotechMessagesReportDto(SubjectEnrollments subjectEnrollments) {
    subject = subjectEnrollments.getSubject();
    dateOfUnenrollment = subjectEnrollments.getDateOfUnenrollment();
  }
}
