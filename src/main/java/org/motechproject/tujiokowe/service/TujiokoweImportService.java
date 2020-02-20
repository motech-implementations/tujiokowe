package org.motechproject.tujiokowe.service;

import org.joda.time.LocalDate;

public interface TujiokoweImportService {

  void fetchCSVUpdates();

  void fetchCSVUpdates(LocalDate startDate);
}
