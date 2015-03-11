package ca.corefacility.bioinformatics.irida.web.controller.test.listeners;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Global settings for unit tests.
 *
 */
public class UnitTestListener extends RunListener {
    private static final Logger logger = LoggerFactory.getLogger(UnitTestListener.class);

    /**
     * {@inheritDoc}
     */
    public void testRunStarted(Description description) throws Exception {
        logger.debug("Configuring Spring MockHTTPServletRequest.");
        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
    }
}
