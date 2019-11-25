package org.motechproject.tujiokowe.osgi;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tujiokowe.service.ConfigService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

/**
 * Verify that TujiokoweSettingsService is present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TujiokoweConfigServiceIT extends BasePaxIT {

    @Inject
    private ConfigService configService;

    @Test
    public void testTujiokoweSettingsServicePresent() {
        assertNotNull(configService.getConfig());
    }
}
