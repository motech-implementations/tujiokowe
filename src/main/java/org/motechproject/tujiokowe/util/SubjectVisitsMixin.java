package org.motechproject.tujiokowe.util;

import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.util.serializer.CustomVisitListSerializer;

public abstract class SubjectVisitsMixin {

  @JsonSerialize(using = CustomVisitListSerializer.class)
  public abstract List<Visit> getVisits();
}
