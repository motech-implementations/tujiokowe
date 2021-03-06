package org.motechproject.tujiokowe.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.tujiokowe.dto.ExportField;

public final class TujiokoweConstants {

  public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

  public static final String PDF_EXPORT_FORMAT = "pdf";
  public static final String CSV_EXPORT_FORMAT = "csv";
  public static final String XLS_EXPORT_FORMAT = "xls";

  public static final String TEXT_CSV_CONTENT = "text/csv";
  public static final String APPLICATION_PDF_CONTENT = "application/pdf";

  public static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormat
      .forPattern("yyyy-MM-dd");

  public static final Map<String, Float> REPORT_COLUMN_WIDTHS = new LinkedHashMap<String, Float>() {
    {
      put("Participant ID", 64f); //NO CHECKSTYLE MagicNumber
      put("SMS", 32f);
    }
  };

  public static final String CLEAR_EXPORT_TASKS_EVENT = "clear_export_tasks_event";
  public static final String CLEAR_EXPORT_TASKS_EVENT_START_TIME = "03:00";

  public static final String REPORT_DATE_FORMAT = "yyyy-MM-dd";
  public static final String DAILY_REPORT_EVENT = "daily_report_event";
  public static final String DAILY_REPORT_EVENT_START_DATE = "daily_report_event_start_date";
  public static final String DAILY_REPORT_EVENT_START_HOUR = "00:01";

  public static final String SEND_EMAIL_REPORT_EVENT = "send_email_report_event";

  public static final String FETCH_CSV_EVENT = "fetch_csv_event";
  public static final String FETCH_CSV_EVENT_START_HOUR = "00:01";
  public static final String FTP_FILE_SEPARATOR = "/";
  public static final Pattern CSV_FILENAME_PATTERN = Pattern.compile(".*_(\\d{4}-\\d{2}-\\d{2})\\.csv");
  public static final String CSV_DATE_FORMAT = "yyyy-MM-dd";

  public static final String API_KEY = "api_key";
  public static final String MESSAGE_ID = "message_id";
  public static final String WEBHOOK_URL = "webhook_url";
  public static final String SEND_TO_SUBSCRIBERS = "send_to_subscribers";
  public static final String SEND_SMS_IF_VOICE_FAILS = "send_sms_if_voice_fails";
  public static final String DETECT_VOICEMAIL = "detect_voicemail_action";
  public static final String RETRY_ATTEMPTS_SHORT = "retry_attempts_short";
  public static final String RETRY_DELAY_SHORT = "retry_delay_short";
  public static final String RETRY_ATTEMPTS_LONG = "retry_attempts_long";
  public static final String RETRY_ATTEMPTS_LONG_DEFAULT = "1";
  public static final String SUBJECT_ID = "participant_id";
  public static final String SUBJECT_PHONE_NUMBER = "subscriber_phone";
  public static final String PREFERRED_LANGUAGE = "preferred_language";
  public static final String RECEIVE_VOICE = "receive_voice";
  public static final String RECEIVE_SMS = "receive_sms";
  public static final String VOICE_SENDER_ID = "voice_sender_id";
  public static final String SMS_SENDER_ID = "sms_sender_id";
  public static final String PHONE = "phone";
  public static final String NAME_PROPERTY = "property[name]";
  public static final String SUBJECT_ID_PROPERTY = "property[participant_id]";

  public static final String SUBSCRIBERS_URL = "/subscribers";

  public static final String IVR_CALL_DETAIL_RECORD_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSS";
  public static final String IVR_CALL_DETAIL_RECORD_MOTECH_TIMESTAMP_FIELD = "motechTimestamp";

  public static final String IVR_CALL_DETAIL_RECORD_STATUS_INITIATED = "INITIATED";
  public static final String IVR_CALL_DETAIL_RECORD_STATUS_FINISHED = "Finished";
  public static final String IVR_CALL_DETAIL_RECORD_STATUS_FAILED = "Failed";
  public static final String IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED = "Submitted";
  public static final String IVR_CALL_DETAIL_RECORD_STATUS_IN_PROGRESS = "In Progress";
  public static final String IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS = "attempts";
  public static final String IVR_CALL_DETAIL_RECORD_END_TIMESTAMP = "end_timestamp";
  public static final String IVR_CALL_DETAIL_RECORD_START_TIMESTAMP = "start_timestamp";
  public static final String IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED = "message_seconds_completed";
  public static final String IVR_DELIVERY_LOG_ID = "delivery_log_id";
  public static final String IVR_CALL_DETAIL_RECORD_HANGUP_REASON = "hangup_reason";

  public static final String VOTO_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final String ENROLLMENTS_TAB_PERMISSION = "tujiokoweEnrollmentsTab";
  public static final String REPORTS_TAB_PERMISSION = "tujiokoweReportsTab";
  public static final String SUBJECTS_TAB_PERMISSION = "tujiokoweSubjectsTab";
  public static final String MANAGE_ENROLLMENTS_PERMISSION = "tujiokoweManageEnrollments";
  public static final String IMPORT_SUBJECTS_PERMISSION = "tujiokoweImportSubjects";
  public static final String MANAGE_MODULE = "manageTujiokowe";
  public static final String MANAGE_HOLIDAYS_PERMISSION = "tujiokoweManageHolidays";

  public static final String HAS_ENROLLMENTS_TAB_ROLE =
      "hasRole('" + ENROLLMENTS_TAB_PERMISSION + "')";
  public static final String HAS_REPORTS_TAB_ROLE = "hasRole('" + REPORTS_TAB_PERMISSION + "')";
  public static final String HAS_SUBJECTS_TAB_ROLE = "hasRole('" + SUBJECTS_TAB_PERMISSION + "')";
  public static final String HAS_MANAGE_ENROLLMENTS_ROLE =
      "hasRole('" + MANAGE_ENROLLMENTS_PERMISSION + "')";
  public static final String HAS_IMPORT_SUBJECTS_ROLE =
      "hasRole('" + IMPORT_SUBJECTS_PERMISSION + "')";
  public static final String HAS_MANAGE_MODULE_ROLE = "hasRole('" + MANAGE_MODULE + "')";
  public static final String HAS_MANAGE_HOLIDAYS_ROLE =
      "hasRole('" + MANAGE_HOLIDAYS_PERMISSION + "')";

  public static final String VISIT_RESCHEDULE_NAME = "VisitReschedule";

  public static final String DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME = "DailyClinicVisitScheduleReport";
  public static final String FOLLOW_UPS_MISSED_CLINIC_VISITS_REPORT_NAME = "FollowupsMissedClinicVisitsReport";
  public static final String M_AND_E_MISSED_CLINIC_VISITS_REPORT_NAME = "MandEMissedClinicVisitsReport";
  public static final String OPTS_OUT_OF_MOTECH_MESSAGES_REPORT_NAME = "ParticipantsWhoOptOutOfReceivingMotechMessagesReport";
  public static final String IVR_AND_SMS_STATISTIC_REPORT_NAME = "NumberOfTimesParticipantsListenedToEachMessageReport";
  public static final String SUBJECT_ENROLLMENTS_NAME = "ParticipantEnrollments";

  public static final String DATE_FIELD_TYPE = "org.joda.time.LocalDate";
  public static final String DATE_TIME_FIELD_TYPE = "org.joda.time.DateTime";
  public static final String VISIT_TYPE_FIELD_TYPE = "VisitType";
  public static final String DOUBLE_FIELD_TYPE = "java.lang.Double";
  public static final String INT_FIELD_TYPE = "java.lang.Integer";
  public static final String STRING_FIELD_TYPE = "java.lang.String";

  public static final List<ExportField> VISIT_RESCHEDULE_FIELDS_MAP = Arrays.asList(
      new ExportField("Participant Id", STRING_FIELD_TYPE, "participantId"),
      new ExportField("Visit Type", VISIT_TYPE_FIELD_TYPE, "visitType"),
      new ExportField("Actual Date", DATE_FIELD_TYPE, "actualDate"),
      new ExportField("Planned Date", DATE_FIELD_TYPE, "plannedDate"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "siteId"));

  public static final List<ExportField> DAILY_CLINIC_VISIT_SCHEDULE_REPORT_MAP = Arrays.asList(
      new ExportField("Planned Visit Date", DATE_FIELD_TYPE, "dateProjected"),
      new ExportField("Participant Id", STRING_FIELD_TYPE, "subject", "subjectId"),
      new ExportField("Phone Number", STRING_FIELD_TYPE, "subject", "phoneNumber"),
      new ExportField("Visit type", VISIT_TYPE_FIELD_TYPE, "type"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "subject", "siteId"));

  public static final List<ExportField> FOLLOW_UPS_MISSED_CLINIC_VISITS_REPORT_MAP = Arrays.asList(
      new ExportField("Participant Id", STRING_FIELD_TYPE, "subject", "subjectId"),
      new ExportField("Visit type", VISIT_TYPE_FIELD_TYPE, "type"),
      new ExportField("Planned Visit Date", DATE_FIELD_TYPE, "dateProjected"),
      new ExportField("No Of Days Exceeded Visit", INT_FIELD_TYPE, "noOfDaysExceededVisit"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "subject", "siteId")
  );

  public static final List<ExportField> M_AND_E_MISSED_CLINIC_VISITS_REPORT_MAP = Arrays.asList(
      new ExportField("Participant Id", STRING_FIELD_TYPE, "subject", "subjectId"),
      new ExportField("Phone", STRING_FIELD_TYPE, "subject", "phoneNumber"),
      new ExportField("Visit type", VISIT_TYPE_FIELD_TYPE, "type"),
      new ExportField("Planned Visit Date", DATE_FIELD_TYPE, "dateProjected"),
      new ExportField("No Of Days Exceeded Visit", INT_FIELD_TYPE, "noOfDaysExceededVisit"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "subject", "siteId"));

  public static final List<ExportField> OPTS_OUT_OF_MOTECH_MESSAGES_REPORT_MAP = Arrays.asList(
      new ExportField("Participant Id", STRING_FIELD_TYPE, "subject", "subjectId"),
      new ExportField("Date of Unenrollment", DATE_FIELD_TYPE, "dateOfUnenrollment"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "subject", "siteId"));

  public static final List<ExportField> IVR_AND_SMS_STATISTIC_REPORT_MAP = Arrays.asList(
      new ExportField("Participant Id", STRING_FIELD_TYPE, "subject", "subjectId"),
      new ExportField("Phone", STRING_FIELD_TYPE, "subject", "phoneNumber"),
      new ExportField("Message ID", STRING_FIELD_TYPE, "messageId"),
      new ExportField("Sent Date", DATE_TIME_FIELD_TYPE, "sendDate"),
      new ExportField("Expected Duration", DOUBLE_FIELD_TYPE, "expectedDuration"),
      new ExportField("Time Listened To", DOUBLE_FIELD_TYPE, "timeListenedTo"),
      new ExportField("Call Length", DOUBLE_FIELD_TYPE, "callLength"),
      new ExportField("Percent Listened", DOUBLE_FIELD_TYPE, "messagePercentListened"),
      new ExportField("Received Date", DATE_TIME_FIELD_TYPE, "receivedDate"),
      new ExportField("No. of Attempts", INT_FIELD_TYPE, "numberOfAttempts"),
      new ExportField("SMS", STRING_FIELD_TYPE, "sms"),
      new ExportField("SMS Received Date", DATE_TIME_FIELD_TYPE, "smsReceivedDate"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "subject", "siteId"));

  public static final List<ExportField> SUBJECT_ENROLLMENTS_MAP = Arrays.asList(
      new ExportField("Participant Id", STRING_FIELD_TYPE, "subject", "subjectId"),
      new ExportField("Status", STRING_FIELD_TYPE, "status"),
      new ExportField("Site ID", STRING_FIELD_TYPE, "subject", "siteId"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_SUBJECT_ENROLLMENTS =
      new ArrayList<>(Arrays.asList("Find By Participant Id", "Find By Status",
          "Find By Participant Site Id"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_VISIT_RESCHEDULE = new ArrayList<>(
      Arrays.asList("Find By Visit Planned Date", "Find By Visit Actual Date",
          "Find By Participant Id", "Find By Participant Site Id"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_DAILY_CLINIC_VISIT_SCHEDULE_REPORT =
      new ArrayList<>(Arrays.asList("Find By Planned Visit Date Range",
          "Find By Planned Visit Date Range And Type", "Find By Type",
          "Find By Participant Id", "Find By Participant Site Id"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_FOLLOWUPS_MISSED_CLINIC_VISITS_REPORT =
      new ArrayList<>(Arrays.asList("Find By Planned Visit Date Range",
          "Find By Planned Visit Date Range And Type"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_M_AND_E_MISSED_CLINIC_VISITS_REPORT =
      new ArrayList<>(Arrays.asList("Find By Participant Id", "Find By Planned Visit Date Range",
          "Find By Planned Visit Date Range And Type", "Find By Participant Site Id"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_OPTS_OUT_OF_MOTECH_MESSAGES_REPORT =
      new ArrayList<>(Arrays.asList("Find By Participant Id", "Find By Participant Site Id",
          "Find By Date Of Unenrollment"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_IVR_AND_SMS_STATISTIC_REPORT =
      new ArrayList<>(Arrays.asList("Find By Participant Id", "Find By Participant Phone Number",
          "Find By Participant Site Id", "Find By Sent Date", "Find By Received Date",
          "Find By SMS Status", "Find By SMS Status And Sent Date",
          "Find By Message Id And Sent Date"));

  public static final List<String> AVAILABLE_LOOKUPS_FOR_SUBJECTS =
      new ArrayList<>(Arrays.asList("Find By Actual Prime Date Range",
          "Find By Actual Booster Date Range", "Find By Participant Id", "Find By Site Id",
          "Find By exact Phone Number", "Find By Visit Type And Actual Date Range",
          "Find By New Booster Planned Date", "Find By New Booster Planned Date and Slot"));

  private TujiokoweConstants() {
  }
}
