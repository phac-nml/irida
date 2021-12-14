package ca.corefacility.bioinformatics.irida.web.controller.test.listeners;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

/**
 * Global settings for REST API integration tests.
 * A listener is also required for Service and Galaxy integration tests
 *
 */
public class IntegrationTestListener extends RunListener {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestListener.class);

    /**
     * {@inheritDoc}
     */
    public void testRunStarted(Description description) throws Exception {
        logger.debug("Setting up RestAssured.");

        RestAssured.requestContentType(ContentType.JSON);
        RestAssured.port = Integer.valueOf(System.getProperty("server.port"));
    }
}
