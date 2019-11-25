package org.motechproject.tujiokowe.service;

import org.motechproject.tujiokowe.domain.Config;

public interface ConfigService {

  Config getConfig();

  void updateConfig(Config config);
}
