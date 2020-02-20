package org.motechproject.tujiokowe.scheduler;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.exception.TujiokoweInitiateCallException;
import org.motechproject.tujiokowe.helper.IvrCallHelper;
import org.motechproject.tujiokowe.service.ReportService;
import org.motechproject.tujiokowe.service.TujiokoweEnrollmentService;
import org.motechproject.tujiokowe.service.TujiokoweImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TujiokoweEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(TujiokoweEventListener.class);

  @Autowired
  private ReportService reportService;

  @Autowired
  private TujiokoweEnrollmentService tujiokoweEnrollmentService;

  @Autowired
  private IvrCallHelper ivrCallHelper;

  @Autowired
  private TujiokoweImportService tujiokoweImportService;

  @MotechListener(subjects = { TujiokoweConstants.FETCH_CSV_EVENT })
  public void fetchCsv(MotechEvent event) {
    LOGGER.info("Started fetching CSV data...");
    tujiokoweImportService.fetchCSVUpdates();
    LOGGER.info("CSV data fetched");
  }

  @MotechListener(subjects = { TujiokoweConstants.DAILY_REPORT_EVENT })
  public void generateDailyReport(MotechEvent event) {
    LOGGER.info("Started generation of daily reports...");
    reportService.generateIvrAndSmsStatisticReports();
    LOGGER.info("Daily Reports generation completed");
  }

  @MotechListener(subjects = EventKeys.CAMPAIGN_COMPLETED)
  public void completeCampaign(MotechEvent event) {
    String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
    String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);

    tujiokoweEnrollmentService.completeCampaign(externalId, campaignName);
  }

  @MotechListener(subjects = EventKeys.SEND_MESSAGE)
  public void initiateIvrCall(MotechEvent event) {
    LOGGER.debug("Handling Motech event {}: {}", event.getSubject(),
        event.getParameters().toString());

    String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);
    String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);

    try {
      ivrCallHelper.initiateIvrCall(messageKey, externalId);
    } catch (TujiokoweInitiateCallException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
