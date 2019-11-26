package org.motechproject.tujiokowe.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TujiokoweScheduler {

  private MotechSchedulerService motechSchedulerService;

  @Autowired
  public TujiokoweScheduler(MotechSchedulerService motechSchedulerService) {
    this.motechSchedulerService = motechSchedulerService;
  }

  public void scheduleDailyReportJob(DateTime startDate) {
    Period period = Period.days(1);

    Map<String, Object> eventParameters = new HashMap<>();
    eventParameters.put(TujiokoweConstants.DAILY_REPORT_EVENT_START_DATE, startDate);

    MotechEvent event = new MotechEvent(TujiokoweConstants.DAILY_REPORT_EVENT, eventParameters);

    RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(),
        null, period, true);
    motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
  }

  public void unscheduleDailyReportJob() {
    motechSchedulerService.safeUnscheduleAllJobs(TujiokoweConstants.DAILY_REPORT_EVENT);
  }
}
