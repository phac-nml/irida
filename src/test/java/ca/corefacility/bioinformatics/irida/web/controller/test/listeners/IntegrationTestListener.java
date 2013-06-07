package ca.corefacility.bioinformatics.irida.web.controller.test.listeners;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jayway.restassured.RestAssured.preemptive;

/**
 * Global settings for all integration tests.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class IntegrationTestListener extends RunListener {

    private static final String USERNAME = "fbristow";
    private static final String PASSWORD = "password1";
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestListener.class);

    /**
     * {@inheritDoc}
     */
    public void testRunStarted(Description description) throws Exception {
        logger.debug("Setting up RestAssured.");
        RestAssured.authentication = preemptive().basic(USERNAME, PASSWORD);
        RestAssured.requestContentType(ContentType.JSON);
    }
}
