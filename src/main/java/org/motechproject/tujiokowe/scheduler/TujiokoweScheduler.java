package org.motechproject.tujiokowe.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
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

  public void scheduleFetchCsvJob(DateTime startDate) {
    Period period = Period.days(1);
    MotechEvent event = new MotechEvent(TujiokoweConstants.FETCH_CSV_EVENT);

    RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(),
        null, period, true);
    motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
  }

  public void unscheduleDailyReportJob() {
    motechSchedulerService.safeUnscheduleAllJobs(TujiokoweConstants.DAILY_REPORT_EVENT);
  }

  public void unscheduleFetchCsvJob() {
    motechSchedulerService.safeUnscheduleAllJobs(TujiokoweConstants.FETCH_CSV_EVENT);
  }

  public void scheduleClearExportTasksJob() {
    DateTime startDate =  DateUtil.newDateTime(LocalDate.now().plusDays(1),
        Time.parseTime(TujiokoweConstants.CLEAR_EXPORT_TASKS_EVENT_START_TIME, ":"));
    Period period = Period.days(1);

    MotechEvent event = new MotechEvent(TujiokoweConstants.CLEAR_EXPORT_TASKS_EVENT);

    RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);
    motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
  }

  public void scheduleEmailReportJob(DateTime startDate, Period period) {
    MotechEvent event = new MotechEvent(TujiokoweConstants.SEND_EMAIL_REPORT_EVENT);

    RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);
    motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
  }

  public void unscheduleEmailReportJob() {
    motechSchedulerService.safeUnscheduleAllJobs(TujiokoweConstants.SEND_EMAIL_REPORT_EVENT);
  }

  public void rescheduleEmailReportJob(DateTime startDate, Period period) {
    unscheduleEmailReportJob();
    scheduleEmailReportJob(startDate, period);
  }
}
