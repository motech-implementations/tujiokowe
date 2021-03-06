package org.motechproject.tujiokowe.service.impl;

import static org.motechproject.tujiokowe.constants.TujiokoweConstants.SIMPLE_DATE_FORMATTER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.repository.CallDetailRecordDataService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.Config;
import org.motechproject.tujiokowe.domain.IvrAndSmsStatisticReport;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.exception.TujiokoweReportException;
import org.motechproject.tujiokowe.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.tujiokowe.service.ConfigService;
import org.motechproject.tujiokowe.service.ReportService;
import org.motechproject.tujiokowe.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

  private static final int HUNDRED_PERCENT = 100;
  private static final int DAYS_WAIT_FOR_SMS = 4;

  @Autowired
  private SubjectService subjectService;

  @Autowired
  private ConfigService configService;

  @Autowired
  private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

  @Autowired
  private CallDetailRecordDataService callDetailRecordDataService;

  @Override
  public void generateIvrAndSmsStatisticReports() {
    Config config = configService.getConfig();

    if (StringUtils.isNotBlank(config.getLastCalculationDateForIvrReports())) {
      LocalDate startDate = SIMPLE_DATE_FORMATTER
          .parseLocalDate(config.getLastCalculationDateForIvrReports());
      generateIvrAndSmsStatisticReportsFromDate(startDate);
    } else {
      generateIvrAndSmsStatisticReportsFromDate(null);
    }

    config = configService.getConfig();
    config.setLastCalculationDateForIvrReports(LocalDate.now().toString(SIMPLE_DATE_FORMATTER));
    configService.updateConfig(config);
  }

  @Override
  public void generateIvrAndSmsStatisticReportsFromDate(LocalDate startDate) {
    List<CallDetailRecord> callDetailRecords = new ArrayList<>();

    if (startDate == null) {
      callDetailRecords = callDetailRecordDataService.findByCallStatus(
          TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED);
    } else {
      LocalDate now = DateUtil.now().toLocalDate();

      for (LocalDate date = startDate; date.isBefore(now); date = date.plusDays(1)) {
        String dateString = SIMPLE_DATE_FORMATTER.print(date);
        callDetailRecords.addAll(callDetailRecordDataService
            .findByMotechTimestampAndCallStatus(dateString,
                TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED));
      }
    }

    Config config = configService.getConfig();
    Set<String> reportsToUpdate = config.getIvrAndSmsStatisticReportsToUpdate();
    config.setIvrAndSmsStatisticReportsToUpdate(null);
    configService.updateConfig(config);

    if (startDate != null && !reportsToUpdate.isEmpty()) {
      callDetailRecords.addAll(callDetailRecordDataService.findByMotechCallIds(reportsToUpdate));
    }

    for (CallDetailRecord callDetailRecord : callDetailRecords) {
      try {
        createIvrAndSmsStatisticReport(callDetailRecord);
        reportsToUpdate.remove(callDetailRecord.getMotechCallId());
      } catch (TujiokoweReportException e) {
        LOGGER.warn(e.getMessage());
      }
    }

    config = configService.getConfig();
    reportsToUpdate.addAll(config.getIvrAndSmsStatisticReportsToUpdate());
    config.setIvrAndSmsStatisticReportsToUpdate(reportsToUpdate);
    configService.updateConfig(config);
  }

  private void createIvrAndSmsStatisticReport(CallDetailRecord initialRecord) { //NO CHECKSTYLE CyclomaticComplexity
    DateTimeFormatter motechTimestampFormatter = DateTimeFormat.forPattern(
        TujiokoweConstants.IVR_CALL_DETAIL_RECORD_TIME_FORMAT);
    DateTimeFormatter votoTimestampFormatter = DateTimeFormat.forPattern(
        TujiokoweConstants.VOTO_TIMESTAMP_FORMAT).withZoneUTC();

    String providerCallId = initialRecord.getProviderCallId();
    Map<String, String> providerExtraData = initialRecord.getProviderExtraData();

    if (StringUtils.isBlank(providerCallId)) {
      throw new TujiokoweReportException(
          "Cannot generate report for Call Detail Record with Motech Call Id: %s, because Provider Call Id is empty",
          initialRecord.getMotechCallId());
    }
    if (providerExtraData == null || providerExtraData.isEmpty()) {
      throw new TujiokoweReportException(
          "Cannot generate report for Call Detail Record with Motech Call Id: %s, because Provider Extra Data Map is empty",
          initialRecord.getMotechCallId());
    }

    String subjectId = providerExtraData.get(TujiokoweConstants.SUBJECT_ID);

    if (StringUtils.isBlank(subjectId)) {
      throw new TujiokoweReportException(
          "Cannot generate report for Call Detail Record with Motech Call Id: %s, because no ParticipantId found In Provider Extra Data Map",
          initialRecord.getMotechCallId());
    }

    Subject subject = subjectService.findSubjectBySubjectId(subjectId.trim());

    if (subject == null) {
      throw new TujiokoweReportException(
          "Cannot generate report for Call Detail Record with Motech Call Id: %s, because No Participant found with Id: %s",
          initialRecord.getMotechCallId(), subjectId);
    }

    List<CallDetailRecord> callDetailRecords = callDetailRecordDataService
        .findByExactProviderCallId(providerCallId,
            QueryParams.ascOrder(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_MOTECH_TIMESTAMP_FIELD));

    List<CallDetailRecord> endRecords = new ArrayList<>();

    boolean sms = false;
    boolean smsFailed = false;
    String messageId = providerExtraData.get(TujiokoweConstants.MESSAGE_ID);
    DateTime sendDate = DateTime
        .parse(initialRecord.getMotechTimestamp(), motechTimestampFormatter);
    int attempts = 0;
    DateTime receivedDate = null;
    DateTime smsReceivedDate = null;
    double expectedDuration = 0;
    double timeListenedTo = 0;
    double callLength = 0;
    double messagePercentListened = 0;

    String smsDeliveryLogId = null;
    String callDeliveryLogId = null;
    CallDetailRecord callRecord = null;
    CallDetailRecord smsRecord = null;

    for (CallDetailRecord callDetailRecord : callDetailRecords) {
      if (callDetailRecord.getCallStatus() == null) {
        continue;
      }

      if (callDetailRecord.getCallStatus()
          .contains(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED)) {
        sms = true;
        smsDeliveryLogId = callDetailRecord.getProviderExtraData().get(TujiokoweConstants.IVR_DELIVERY_LOG_ID);
      } else if (callDetailRecord.getCallStatus().contains(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_IN_PROGRESS)) {
        callDeliveryLogId = callDetailRecord.getProviderExtraData().get(TujiokoweConstants.IVR_DELIVERY_LOG_ID);
      } else if (callDetailRecord.getCallStatus().contains(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED)
          || callDetailRecord.getCallStatus().contains(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED)) {
        endRecords.add(callDetailRecord);
      }
    }

    if (endRecords.isEmpty()) {
      throw new TujiokoweReportException(
          "Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because there is no finished/failed record",
          providerCallId, subjectId);
    }

    if (sms) {
      if (StringUtils.isBlank(smsDeliveryLogId)) {
        throw new TujiokoweReportException(
            "Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because SMS delivery log is empty",
            providerCallId, subjectId);
      }

      for (CallDetailRecord callDetailRecord : endRecords) {
        if (smsDeliveryLogId.equals(
            callDetailRecord.getProviderExtraData().get(TujiokoweConstants.IVR_DELIVERY_LOG_ID))) {
          smsRecord = callDetailRecord;
        } else {
          callRecord = callDetailRecord;
        }
      }
    } else if (StringUtils.isNotBlank(callDeliveryLogId)) {
      for (CallDetailRecord callDetailRecord : endRecords) {
        if (callDeliveryLogId.equals(callDetailRecord.getProviderExtraData().get(TujiokoweConstants.IVR_DELIVERY_LOG_ID))) {
          callRecord = callDetailRecord;
        } else {
          smsRecord = callDetailRecord;
        }
      }
    } else if (endRecords.size() < 2) {
      callRecord = endRecords.get(0);
    } else {
      for (CallDetailRecord callDetailRecord : endRecords) {
        if (StringUtils.isNotBlank(callDetailRecord.getCallDuration())
            || StringUtils.isNotBlank(callDetailRecord.getMessagePercentListened())
            || StringUtils.isNotBlank(callDetailRecord.getProviderExtraData().get(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_HANGUP_REASON))) {
          callRecord = callDetailRecord;
        }
      }

      for (CallDetailRecord callDetailRecord : endRecords) {
        if (callRecord != null && callDetailRecord != callRecord) {
          smsRecord = callDetailRecord;
        } else {
          callRecord = callDetailRecord;
        }
      }
    }

    DateTime maxSmsWaitDate = sendDate.plusDays(DAYS_WAIT_FOR_SMS);

    if (smsRecord != null) {
      sms = true;

      if (smsRecord.getCallStatus()
          .contains(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED)) {
        smsFailed = true;
      } else {
        String providerTimestamp = smsRecord.getProviderExtraData()
            .get(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP);

        if (StringUtils.isBlank(providerTimestamp)) {
          throw new TujiokoweReportException(
              "Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because end_timestamp for SMS Record not found",
              providerCallId, subjectId);
        }

        smsReceivedDate = DateTime.parse(providerTimestamp, votoTimestampFormatter)
            .toDateTime(DateTimeZone.getDefault());
      }
    } else if (maxSmsWaitDate.isAfterNow()) {
      LOGGER.warn(
          "SMS is sent but not yet received for Call Detail Record with Provider Call Id: {} for Providers with Ids {}",
          providerCallId, subjectId);

      Config config = configService.getConfig();
      config.getIvrAndSmsStatisticReportsToUpdate().add(initialRecord.getMotechCallId());
      configService.updateConfig(config);
    } else {
      smsFailed = true;
      LOGGER.error("SMS wait time exceeded, marked as Fail for Call Detail Record with Provider Call Id: {} for Providers with Ids {}", providerCallId, subjectId);
    }

    if (callRecord != null) {
      if (callRecord.getCallStatus()
          .contains(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED)) {
        String providerTimestamp = callRecord.getProviderExtraData()
            .get(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP);

        if (StringUtils.isBlank(providerTimestamp)) {
          throw new TujiokoweReportException(
              "Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because start_timestamp for Call Record not found",
              providerCallId, subjectId);
        }

        receivedDate = DateTime.parse(providerTimestamp, votoTimestampFormatter)
            .toDateTime(DateTimeZone.getDefault());

        if (StringUtils.isNotBlank(callRecord.getCallDuration())) {
          callLength = Double.parseDouble(callRecord.getCallDuration());
        }

        String messageSecondsCompleted = callRecord.getProviderExtraData().get(
            TujiokoweConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED);

        if (StringUtils.isNotBlank(messageSecondsCompleted)) {
          timeListenedTo = Double.parseDouble(messageSecondsCompleted);
        }

        if (StringUtils.isNotBlank(callRecord.getMessagePercentListened())) {
          messagePercentListened = Double.parseDouble(callRecord.getMessagePercentListened());
        }

        if (messagePercentListened > 0) {
          expectedDuration = timeListenedTo * HUNDRED_PERCENT / messagePercentListened;
        }
      }

      String attemptsString = callRecord.getProviderExtraData()
          .get(TujiokoweConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS);
      if (StringUtils.isNotBlank(attemptsString)) {
        attempts = Integer.parseInt(attemptsString);
      }
    }

    IvrAndSmsStatisticReport ivrAndSmsStatisticReport = ivrAndSmsStatisticReportDataService
        .findByProviderCallIdAndSubjectId(providerCallId, subject.getSubjectId());
    if (ivrAndSmsStatisticReport == null) {
      ivrAndSmsStatisticReport = new IvrAndSmsStatisticReport(providerCallId, subject, messageId,
          sendDate,
          expectedDuration, timeListenedTo, callLength, messagePercentListened, receivedDate, attempts, sms,
          smsFailed, smsReceivedDate);
      ivrAndSmsStatisticReportDataService.create(ivrAndSmsStatisticReport);
    } else {
      ivrAndSmsStatisticReport
          .updateReportData(providerCallId, subject, messageId, sendDate, expectedDuration,
              timeListenedTo, callLength, messagePercentListened, receivedDate, attempts, sms, smsFailed,
              smsReceivedDate);
      ivrAndSmsStatisticReportDataService.update(ivrAndSmsStatisticReport);
    }
  }
}
