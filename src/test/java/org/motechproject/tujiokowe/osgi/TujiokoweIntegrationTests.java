package org.motechproject.tujiokowe.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Tujiokowe bundle integration tests suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TujiokoweWebIT.class,
        TujiokoweConfigServiceIT.class,
        LookupServiceIT.class,
        SubjectServiceIT.class
})
public class TujiokoweIntegrationTests {
}
