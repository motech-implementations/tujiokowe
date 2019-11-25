package org.motechproject.tujiokowe.web;

import static org.motechproject.tujiokowe.constants.TujiokoweConstants.AVAILABLE_LOOKUPS_FOR_SUBJECT_ENROLLMENTS;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.Enrollment;
import org.motechproject.tujiokowe.domain.SubjectEnrollments;
import org.motechproject.tujiokowe.exception.TujiokoweEnrollmentException;
import org.motechproject.tujiokowe.exception.TujiokoweException;
import org.motechproject.tujiokowe.exception.TujiokoweLookupException;
import org.motechproject.tujiokowe.repository.EnrollmentDataService;
import org.motechproject.tujiokowe.service.LookupService;
import org.motechproject.tujiokowe.service.TujiokoweEnrollmentService;
import org.motechproject.tujiokowe.web.domain.GridSettings;
import org.motechproject.tujiokowe.web.domain.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize(TujiokoweConstants.HAS_ENROLLMENTS_TAB_ROLE)
public class TujiokoweEnrollmentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TujiokoweEnrollmentController.class);

  @Autowired
  private TujiokoweEnrollmentService tujiokoweEnrollmentService;

  @Autowired
  private EnrollmentDataService enrollmentDataService;

  @Autowired
  private LookupService lookupService;

  @RequestMapping(value = "/getEnrollments", method = RequestMethod.POST)
  @ResponseBody
  public Records<?> getEnrollments(GridSettings settings) {
    Order order = null;
    if (!settings.getSortColumn().isEmpty()) {
      order = new Order(settings.getSortColumn(), settings.getSortDirection());
    }

    QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

    try {
      return lookupService
          .getEntities(SubjectEnrollments.class, settings.getLookup(), settings.getFields(),
              queryParams);
    } catch (TujiokoweLookupException e) {
      LOGGER.debug(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(value = "/getLookupsForEnrollments", method = RequestMethod.GET)
  @ResponseBody
  public List<LookupDto> getLookupsForEnrollments() {
    List<LookupDto> ret = new ArrayList<>();
    List<LookupDto> availableLookups;

    try {
      availableLookups = lookupService.getAvailableLookups(SubjectEnrollments.class.getName());
    } catch (TujiokoweLookupException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }

    for (LookupDto lookupDto : availableLookups) {
      if (AVAILABLE_LOOKUPS_FOR_SUBJECT_ENROLLMENTS.contains(lookupDto.getLookupName())) {
        ret.add(lookupDto);
      }
    }
    return ret;
  }

  @PreAuthorize(TujiokoweConstants.HAS_MANAGE_ENROLLMENTS_ROLE)
  @RequestMapping(value = "/checkAdvancedPermissions", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> checkAdvancedPermissions() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PreAuthorize(TujiokoweConstants.HAS_MANAGE_ENROLLMENTS_ROLE)
  @RequestMapping(value = "/getEnrollmentAdvanced/{subjectId}", method = RequestMethod.POST)
  @ResponseBody
  public Records<?> getEnrollmentAdvanced(@PathVariable String subjectId, GridSettings settings) {
    Order order = null;
    if (!settings.getSortColumn().isEmpty()) {
      order = new Order(settings.getSortColumn(), settings.getSortDirection());
    }

    QueryParams queryParams = new QueryParams(null, null, order);

    long recordCount;
    int rowCount;

    recordCount = enrollmentDataService.countFindBySubjectId(subjectId);
    rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

    List<Enrollment> enrollments = enrollmentDataService.findBySubjectId(subjectId, queryParams);

    return new Records<>(settings.getPage(), rowCount, (int) recordCount, enrollments);
  }

  @RequestMapping(value = "/enrollSubject", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> enrollSubject(@RequestBody String subjectId) {
    if (StringUtils.isBlank(subjectId)) {
      return new ResponseEntity<>("Participant id cannot be empty", HttpStatus.BAD_REQUEST);
    }

    try {
      tujiokoweEnrollmentService.enrollSubject(subjectId);
    } catch (TujiokoweEnrollmentException e) {
      LOGGER.debug(e.getMessage(), e);
      return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/unenrollSubject", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> unenrollSubject(@RequestBody String subjectId) {
    if (StringUtils.isBlank(subjectId)) {
      return new ResponseEntity<>("Participant id cannot be empty", HttpStatus.BAD_REQUEST);
    }

    try {
      tujiokoweEnrollmentService.unenrollSubject(subjectId);
    } catch (TujiokoweEnrollmentException e) {
      LOGGER.debug(e.getMessage(), e);
      return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PreAuthorize(TujiokoweConstants.HAS_MANAGE_ENROLLMENTS_ROLE)
  @RequestMapping(value = "/enrollCampaign/{subjectId}/{campaignName}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> enrollCampaign(@PathVariable String subjectId,
      @PathVariable String campaignName) {

    if (StringUtils.isBlank(subjectId)) {
      return new ResponseEntity<>("Participant id cannot be empty", HttpStatus.BAD_REQUEST);
    }

    if (StringUtils.isBlank(campaignName)) {
      return new ResponseEntity<>("Campaign name cannot be empty", HttpStatus.BAD_REQUEST);
    }

    try {
      tujiokoweEnrollmentService.enrollSubjectToCampaign(subjectId, campaignName);
    } catch (TujiokoweEnrollmentException e) {
      LOGGER.debug(e.getMessage(), e);
      return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PreAuthorize(TujiokoweConstants.HAS_MANAGE_ENROLLMENTS_ROLE)
  @RequestMapping(value = "/unenrollCampaign/{subjectId}/{campaignName}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> unenrollCampaign(@PathVariable String subjectId,
      @PathVariable String campaignName) {

    if (StringUtils.isBlank(subjectId)) {
      return new ResponseEntity<>("Participant id cannot be empty", HttpStatus.BAD_REQUEST);
    }

    if (StringUtils.isBlank(campaignName)) {
      return new ResponseEntity<>("Campaign name cannot be empty", HttpStatus.BAD_REQUEST);
    }

    try {
      tujiokoweEnrollmentService.unenrollSubject(subjectId, campaignName);
    } catch (TujiokoweEnrollmentException e) {
      LOGGER.debug(e.getMessage(), e);
      return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  private String getMessageFromException(TujiokoweException e) {
    return e.getMessage();
  }
}
