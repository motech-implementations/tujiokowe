package org.motechproject.tujiokowe.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.tujiokowe.domain.Config;
import org.motechproject.tujiokowe.scheduler.TujiokoweScheduler;
import org.motechproject.tujiokowe.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ConfigController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

  @Autowired
  @Qualifier("configService")
  private ConfigService configService;

  @Autowired
  private TujiokoweScheduler tujiokoweScheduler;

  @RequestMapping(value = "/tujiokowe-config", method = RequestMethod.GET)
  @ResponseBody
  public Config getConfig() {
    return configService.getConfig();
  }

  @RequestMapping(value = "/tujiokowe-config", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Config updateConfig(@RequestBody Config config) {
    configService.updateConfig(config);

    tujiokoweScheduler.unscheduleDailyReportJob();
    tujiokoweScheduler.unscheduleZetesImportJob();
    scheduleJobs();

    return configService.getConfig();
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleException(Exception e) {
    LOGGER.error("Error while updating configs", e);
    return e.getMessage();
  }

  private void scheduleJobs() {
    if (configService.getConfig().getEnableReportJob()) {
      DateTime reportStartDate = DateUtil.newDateTime(LocalDate.now(),
          Time.parseTime(configService.getConfig().getReportCalculationStartTime(), ":"));
      if (reportStartDate.isBeforeNow()) {
        reportStartDate = reportStartDate.plusDays(1);
      }
      tujiokoweScheduler.scheduleDailyReportJob(reportStartDate);
    }

    if (configService.getConfig().getEnableZetesImportJob()) {
      DateTime importStartDate = DateUtil.newDateTime(LocalDate.now(),
          Time.parseTime(configService.getConfig().getZetesImportStartTime(), ":"));
      if (importStartDate.isBeforeNow()) {
        importStartDate = importStartDate.plusDays(1);
      }
      tujiokoweScheduler.scheduleZetesImportJob(importStartDate);
    }
  }
}
