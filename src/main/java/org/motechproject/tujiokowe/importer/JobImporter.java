package org.motechproject.tujiokowe.importer;

import java.util.Map;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tujiokowe.scheduler.TujiokoweScheduler;
import org.springframework.stereotype.Component;

@Component
public class JobImporter implements OsgiServiceLifecycleListener {

    private MotechSchedulerService motechSchedulerService;

    @Override
    public void bind(Object o, Map map) throws Exception {
        this.motechSchedulerService = (MotechSchedulerService) o;
        clearExportTasksJob();
    }

    @Override
    public void unbind(Object o, Map map) throws Exception {
        this.motechSchedulerService = null;
    }

    private void clearExportTasksJob() {
        TujiokoweScheduler tujiokoweScheduler = new TujiokoweScheduler(motechSchedulerService);
        tujiokoweScheduler.scheduleClearExportTasksJob();
    }
}
