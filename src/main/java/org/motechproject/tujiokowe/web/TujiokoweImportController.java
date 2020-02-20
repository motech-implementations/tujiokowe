package org.motechproject.tujiokowe.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.mds.util.Constants;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.service.TujiokoweImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TujiokoweImportController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TujiokoweImportController.class);

  @Autowired
  private TujiokoweImportService tujiokoweImportService;

  @RequestMapping(value = "/fetch-csv", method = RequestMethod.POST)
  @PreAuthorize(Constants.Roles.HAS_DATA_ACCESS)
  @ResponseBody
  public ResponseEntity<String> fetchCsv(@RequestBody String startDate) {
    if (StringUtils.isNotBlank(startDate)) {
      try {
        tujiokoweImportService.fetchCSVUpdates(
            DateTimeFormat.forPattern(TujiokoweConstants.CSV_DATE_FORMAT).parseLocalDate(startDate));
      } catch (IllegalArgumentException e) {
        LOGGER.error("Invalid date format", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
      }
    } else {
      tujiokoweImportService.fetchCSVUpdates();
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
