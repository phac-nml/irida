package ca.corefacility.bioinformatics.irida.junit5.listeners;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
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
public class UnitTestListener implements TestExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(UnitTestListener.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testPlanExecutionStarted(TestPlan testPlan) {
		logger.debug("Configuring Spring MockHTTPServletRequest.");
		// fake out the servlet response so that the URI builder will work.
		RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
		RequestContextHolder.setRequestAttributes(ra);
	}
}
