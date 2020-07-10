package org.motechproject.tujiokowe.util;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tujiokowe.domain.Visit;

public abstract class SubjectVisitsMixin {

  @JsonIgnore
  public abstract List<Visit> getVisits();
}
