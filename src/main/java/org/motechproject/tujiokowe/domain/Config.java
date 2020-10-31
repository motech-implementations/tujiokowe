package org.motechproject.tujiokowe.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.enums.EmailSchedulePeriod;

public class Config {

  @Getter
  @Setter
  private Boolean fetchCsvData = false;

  @Getter
  @Setter
  private String fetchCsvDataStartTime = TujiokoweConstants.FETCH_CSV_EVENT_START_HOUR;

  @Getter
  @Setter
  private String knownHostsFile;

  @Getter
  @Setter
  private String ftpsHost;

  @Getter
  @Setter
  private Integer ftpsPort;

  @Getter
  @Setter
  private String ftpsDirectory;

  @Getter
  @Setter
  private String ftpsUsername;

  @Getter
  @Setter
  private String ftpsPassword;

  @Getter
  @Setter
  private String lastCsvUpdate;

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
  private String ivrUrl;

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

  @Getter
  @Setter
  private String emailReportHost;

  @Getter
  @Setter
  private Integer emailReportPort;

  @Getter
  @Setter
  private String emailReportAddress;

  @Getter
  @Setter
  private String emailReportPassword;

  @Getter
  @Setter
  private Boolean enableEmailReportJob = false;

  @Getter
  @Setter
  private String emailReportStartDate;

  @Getter
  @Setter
  private EmailSchedulePeriod emailSchedulePeriod;

  @Getter
  @Setter
  private String emailRecipients;

  @Getter
  @Setter
  private String emailSubject;

  @Getter
  @Setter
  private String emailBody;
}
