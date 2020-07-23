package org.motechproject.tujiokowe.helper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.domain.enums.VisitType;

public class CampaignNameHelper {

  public static String getCampaignName(Subject subject, Visit visit) {
    if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
      return visit.getType().getDisplayValue();
    }

    if (StringUtils.isBlank(subject.getSlot())) {
      return visit.getType().getDisplayValue();
    }

    return visit.getType().getDisplayValue() + " - Slot " + subject.getSlot();
  }
}
