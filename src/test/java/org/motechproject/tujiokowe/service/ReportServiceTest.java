package org.motechproject.tujiokowe.service;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.tujiokowe.repository.VisitDataService;
import org.motechproject.tujiokowe.service.impl.ReportServiceImpl;

public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService = new ReportServiceImpl();

    @Mock
    private VisitDataService visitDataService;

    @Mock
    private LookupService lookupService;

    @Before
    public void setUp() {
        initMocks(this);
    }
}
