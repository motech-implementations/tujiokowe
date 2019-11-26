package org.motechproject.tujiokowe.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;

public class Config {

  @Getter
  @Setter
  private Boolean enableReportJob = false;

  @Getter
  @Setter
  private String reportCalculationStartTime = TujiokoweConstants.DAILY_REPORT_EVENT_START_HOUR;

  @Getter
  @Setter
  private String lastCalculationDateForIvrReports;

  @Getter
  @Setter
  private Set<String> ivrAndSmsStatisticReportsToUpdate = new HashSet<>();

  @Getter
  @Setter
  private Boolean sendIvrCalls = false;

  @Getter
  @Setter
  private String ivrSettingsName;

  @Getter
  @Setter
  private String apiKey;

  @Getter
  @Setter
  private String statusCallbackUrl;

  @Getter
  @Setter
  private Boolean sendSmsIfVoiceFails = false;

  @Getter
  @Setter
  private Boolean detectVoiceMail = true;

  @Getter
  @Setter
  private Integer retryAttempts;

  @Getter
  @Setter
  private Integer retryDelay;

  @Getter
  @Setter
  private String ivrLanguageId;

  @Getter
  @Setter
  private String voiceSenderId;

  @Getter
  @Setter
  private String smsSenderId;
}
