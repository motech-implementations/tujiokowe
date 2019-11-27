package org.motechproject.tujiokowe.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.domain.enums.VisitType;
import org.motechproject.tujiokowe.util.serializer.CustomDateDeserializer;
import org.motechproject.tujiokowe.util.serializer.CustomDateSerializer;
import org.motechproject.tujiokowe.util.serializer.CustomVisitTypeDeserializer;
import org.motechproject.tujiokowe.util.serializer.CustomVisitTypeSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class VisitRescheduleDto {

  @Getter
  @Setter
  private String participantId;

  @Getter
  @Setter
  private String siteId;

  @JsonSerialize(using = CustomVisitTypeSerializer.class)
  @JsonDeserialize(using = CustomVisitTypeDeserializer.class)
  @Getter
  @Setter
  private VisitType visitType;

  @JsonDeserialize(using = CustomDateDeserializer.class)
  @JsonSerialize(using = CustomDateSerializer.class)
  @Getter
  @Setter
  private LocalDate actualDate;

  @JsonDeserialize(using = CustomDateDeserializer.class)
  @JsonSerialize(using = CustomDateSerializer.class)
  @Getter
  @Setter
  private LocalDate plannedDate;

  @Getter
  @Setter
  private Long visitId;

  @JsonDeserialize(using = CustomDateDeserializer.class)
  @JsonSerialize(using = CustomDateSerializer.class)
  @Getter
  @Setter
  private LocalDate earliestDate;

  @JsonDeserialize(using = CustomDateDeserializer.class)
  @JsonSerialize(using = CustomDateSerializer.class)
  @Getter
  @Setter
  private LocalDate latestDate;

  @Getter
  @Setter
  private Boolean ignoreDateLimitation;

  @Getter
  @Setter
  private Boolean notVaccinated;

  public VisitRescheduleDto(Visit visit) {
    setParticipantId(visit.getSubject().getSubjectId());
    setSiteId(visit.getSubject().getSiteId());
    setVisitType(visit.getType());
    setActualDate(visit.getDate());
    setPlannedDate(visit.getDateProjected());
    setVisitId(visit.getId());
    if (visit.getIgnoreDateLimitation() != null) {
      setIgnoreDateLimitation(visit.getIgnoreDateLimitation());
    } else {
      setIgnoreDateLimitation(false);
    }
  }

  public VisitRescheduleDto(Visit visit, Range<LocalDate> dateRange, Boolean notVaccinated) {
    this(visit);
    this.notVaccinated = notVaccinated;
    calculateEarliestAndLatestDate(dateRange);
  }

  private void calculateEarliestAndLatestDate(Range<LocalDate> dateRange) {
    if (dateRange != null) {
      earliestDate = dateRange.getMin();
      latestDate = dateRange.getMax();
    }
  }
}
