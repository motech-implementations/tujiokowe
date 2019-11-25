package org.motechproject.tujiokowe.dto;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.domain.enums.VisitType;
import org.motechproject.tujiokowe.util.serializer.CustomDateSerializer;
import org.motechproject.tujiokowe.util.serializer.CustomSubjectSerializer;
import org.motechproject.tujiokowe.util.serializer.CustomVisitTypeSerializer;

@JsonAutoDetect
public class MissedVisitsReportDto {

  @JsonProperty
  @Getter
  private int noOfDaysExceededVisit;

  @JsonProperty
  @JsonSerialize(using = CustomSubjectSerializer.class)
  @Getter
  private Subject subject;

  @JsonProperty
  @JsonSerialize(using = CustomVisitTypeSerializer.class)
  @Getter
  private VisitType type;

  @JsonProperty
  @JsonSerialize(using = CustomDateSerializer.class)
  @Getter
  private LocalDate dateProjected;

  public MissedVisitsReportDto(Visit entityObject) {
    dateProjected = entityObject.getDateProjected();
    if (dateProjected == null) {
      noOfDaysExceededVisit = 0;
    } else {
      noOfDaysExceededVisit = Days.daysBetween(dateProjected, LocalDate.now()).getDays();
    }

    subject = entityObject.getSubject();
    type = entityObject.getType();
    dateProjected = entityObject.getDateProjected();
  }

  @JsonSerialize(using = CustomDateSerializer.class)
  public LocalDate getPlanedVisitDate() {
    return dateProjected;
  }
}
