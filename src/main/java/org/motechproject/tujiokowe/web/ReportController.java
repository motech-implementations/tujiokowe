package org.motechproject.tujiokowe.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.IvrAndSmsStatisticReport;
import org.motechproject.tujiokowe.domain.SubjectEnrollments;
import org.motechproject.tujiokowe.domain.Visit;
import org.motechproject.tujiokowe.dto.IvrAndSmsStatisticReportDto;
import org.motechproject.tujiokowe.dto.MissedVisitsReportDto;
import org.motechproject.tujiokowe.dto.OptsOutOfMotechMessagesReportDto;
import org.motechproject.tujiokowe.exception.TujiokoweLookupException;
import org.motechproject.tujiokowe.helper.DtoLookupHelper;
import org.motechproject.tujiokowe.service.LookupService;
import org.motechproject.tujiokowe.service.ReportService;
import org.motechproject.tujiokowe.util.QueryParamsBuilder;
import org.motechproject.tujiokowe.web.domain.GridSettings;
import org.motechproject.tujiokowe.web.domain.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@PreAuthorize(TujiokoweConstants.HAS_REPORTS_TAB_ROLE)
public class ReportController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

  @Autowired
  private ReportService reportService;

  @Autowired
  private LookupService lookupService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @RequestMapping(value = "/generateIvrAndSmsStatisticReports", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public ResponseEntity<String> generateIvrAndSmsStatisticReports(@RequestBody String startDate) {

    try {
      if (StringUtils.isNotBlank(startDate)) {
        LocalDate date = LocalDate.parse(startDate, DateTimeFormat.forPattern(
            TujiokoweConstants.REPORT_DATE_FORMAT));
        reportService.generateIvrAndSmsStatisticReportsFromDate(date);
        LOGGER.info("Reports generated by custom request from date: {}",
            date.toString(DateTimeFormat.forPattern(TujiokoweConstants.REPORT_DATE_FORMAT)));
      } else {
        reportService.generateIvrAndSmsStatisticReportsFromDate(null);
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid date format", e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOGGER.error("Fatal error raised during creating reports", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/getReport/{reportType}", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public Records<?> getReport(@PathVariable String reportType, GridSettings settings) {
    switch (reportType) {
      case "dailyClinicVisitScheduleReport":
        return getDailyClinicVisitScheduleReport(settings);
      case "followupsMissedClinicVisitsReport":
        return getFollowupsMissedClinicVisitsReport(settings);
      case "MandEMissedClinicVisitsReport":
        return getMandEMissedClinicVisitsReport(settings);
      case "optsOutOfMotechMessagesReport":
        return getOptsOutOfMotechMessagesReport(settings);
      case "ivrAndSmsStatisticReport":
        return getIvrAndSmsStatisticReport(settings);
      default:
        return null;
    }
  }

  @RequestMapping(value = "/getReportModel/{reportType}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public String getReportModel(@PathVariable String reportType) throws IOException {
    String json = IOUtils.toString(getClass().getResourceAsStream("/reportModels.json"),
        StandardCharsets.UTF_8);
    Map<String, Object> modelsMap = getFields(json);
    return objectMapper.writeValueAsString(modelsMap.get(reportType));
  }

  @RequestMapping(value = "/getLookupsForDailyClinicVisitScheduleReport", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public List<LookupDto> getLookupsForDailyClinicVisitScheduleReport() {
    List<LookupDto> ret = new ArrayList<>();
    List<LookupDto> availableLookups;
    try {
      availableLookups = lookupService.getAvailableLookups(Visit.class.getName());
    } catch (TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
    List<String> lookupList = TujiokoweConstants.AVAILABLE_LOOKUPS_FOR_DAILY_CLINIC_VISIT_SCHEDULE_REPORT;
    for (LookupDto lookupDto : availableLookups) {
      if (lookupList.contains(lookupDto.getLookupName())) {
        ret.add(lookupDto);
      }
    }
    return ret;
  }

  @RequestMapping(value = "/getLookupsForFollowupsMissedClinicVisitsReport", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public List<LookupDto> getLookupsForFollowupsMissedClinicVisitsReport() {
    List<LookupDto> ret = new ArrayList<>();
    List<LookupDto> availableLookups;
    try {
      availableLookups = lookupService.getAvailableLookups(Visit.class.getName());
    } catch (TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
    List<String> lookupList = TujiokoweConstants.AVAILABLE_LOOKUPS_FOR_FOLLOWUPS_MISSED_CLINIC_VISITS_REPORT;
    for (LookupDto lookupDto : availableLookups) {
      if (lookupList.contains(lookupDto.getLookupName())) {
        ret.add(lookupDto);
      }
    }
    return ret;
  }

  @RequestMapping(value = "/getLookupsForMandEMissedClinicVisitsReport", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public List<LookupDto> getLookupsForMandEMissedClinicVisitsReport() {
    List<LookupDto> ret = new ArrayList<>();
    List<LookupDto> availableLookups;
    try {
      availableLookups = lookupService.getAvailableLookups(Visit.class.getName());
    } catch (TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
    List<String> lookupList = TujiokoweConstants.AVAILABLE_LOOKUPS_FOR_M_AND_E_MISSED_CLINIC_VISITS_REPORT;
    for (LookupDto lookupDto : availableLookups) {
      if (lookupList.contains(lookupDto.getLookupName())) {
        ret.add(lookupDto);
      }
    }
    return ret;
  }

  @RequestMapping(value = "/getLookupsForOptsOutOfMotechMessagesReport", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public List<LookupDto> getLookupsForOptsOutOfMotechMessagesReport() {
    List<LookupDto> ret = new ArrayList<>();
    List<LookupDto> availableLookups;
    try {
      availableLookups = lookupService.getAvailableLookups(SubjectEnrollments.class.getName());
    } catch (TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
    List<String> lookupList = TujiokoweConstants.AVAILABLE_LOOKUPS_FOR_OPTS_OUT_OF_MOTECH_MESSAGES_REPORT;

    for (LookupDto lookupDto : availableLookups) {
      if (lookupList.contains(lookupDto.getLookupName())) {
        ret.add(lookupDto);
      }
    }
    return ret;
  }

  @RequestMapping(value = "/getLookupsForIvrAndSmsStatisticReport", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageTujiokowe')")
  @ResponseBody
  public List<LookupDto> getLookupsForIvrAndSmsStatisticReport() {
    List<LookupDto> ret = new ArrayList<>();
    List<LookupDto> availableLookups;
    try {
      availableLookups = lookupService
          .getAvailableLookups(IvrAndSmsStatisticReport.class.getName());
    } catch (TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
    List<String> lookupList = TujiokoweConstants.AVAILABLE_LOOKUPS_FOR_IVR_AND_SMS_STATISTIC_REPORT;
    for (LookupDto lookupDto : availableLookups) {
      if (lookupList.contains(lookupDto.getLookupName())) {
        ret.add(lookupDto);
      }
    }
    return ret;
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleException(Exception e) {
    LOGGER.error(e.getMessage(), e);
    return e.getMessage();
  }

  private Records<?> getDailyClinicVisitScheduleReport(GridSettings settings) {
    try {
      QueryParams queryParams = QueryParamsBuilder
          .buildQueryParams(settings, getFields(settings.getFields()));
      return lookupService
          .getEntities(Visit.class, settings.getLookup(), settings.getFields(), queryParams);
    } catch (IOException | TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return new Records<Object>(null);
    }
  }

  private Records<?> getFollowupsMissedClinicVisitsReport(GridSettings settings) {
    GridSettings newSettings = settings;
    try {
      newSettings = DtoLookupHelper
          .changeLookupAndOrderForFollowupsMissedClinicVisitsReport(settings);
      if (newSettings == null) {
        return new Records<Object>(null);
      }
      QueryParams queryParams = QueryParamsBuilder
          .buildQueryParams(newSettings, getFields(newSettings.getFields()));
      return lookupService
          .getEntities(MissedVisitsReportDto.class, Visit.class, newSettings.getLookup(),
              newSettings.getFields(), queryParams);
    } catch (IOException | TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return new Records<Object>(null);
    }
  }

  private Records<?> getMandEMissedClinicVisitsReport(GridSettings settings) {
    GridSettings newSettings = settings;
    try {
      newSettings = DtoLookupHelper.changeLookupAndOrderForMandEMissedClinicVisitsReport(settings);
      if (newSettings == null) {
        return new Records<Object>(null);
      }
      QueryParams queryParams = QueryParamsBuilder
          .buildQueryParams(newSettings, getFields(newSettings.getFields()));
      return lookupService
          .getEntities(MissedVisitsReportDto.class, Visit.class, newSettings.getLookup(),
              newSettings.getFields(), queryParams);
    } catch (IOException | TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return new Records<Object>(null);
    }
  }

  private Records<?> getOptsOutOfMotechMessagesReport(GridSettings settings) {
    GridSettings newSettings = settings;
    try {
      newSettings = DtoLookupHelper.changeLookupAndOrderForOptsOutOfMotechMessagesReport(settings);
      if (newSettings == null) {
        return new Records<Object>(null);
      }
      QueryParams queryParams = QueryParamsBuilder
          .buildQueryParams(newSettings, getFields(newSettings.getFields()));
      return lookupService
          .getEntities(OptsOutOfMotechMessagesReportDto.class, SubjectEnrollments.class,
              newSettings.getLookup(), newSettings.getFields(), queryParams);
    } catch (IOException | TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return new Records<Object>(null);
    }
  }

  private Records<?> getIvrAndSmsStatisticReport(GridSettings settings) {
    try {
      QueryParams queryParams = QueryParamsBuilder
          .buildQueryParams(settings, getFields(settings.getFields()));
      return lookupService
          .getEntities(IvrAndSmsStatisticReportDto.class, IvrAndSmsStatisticReport.class,
              settings.getLookup(), settings.getFields(), queryParams);
    } catch (IOException | TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return new Records<Object>(null);
    }
  }


  private Map<String, Object> getFields(String json) throws IOException {
    if (json == null) {
      return null;
    } else {
      return objectMapper.readValue(json, new TypeReference<LinkedHashMap>() {
      }); //NO CHECKSTYLE WhitespaceAround
    }
  }
}
