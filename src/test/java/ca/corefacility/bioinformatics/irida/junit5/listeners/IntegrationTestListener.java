package ca.corefacility.bioinformatics.irida.junit5.listeners;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

/**
 * Global settings for REST API integration tests.
 * A listener is also required for Galaxy integration tests
 *
 */
public class IntegrationTestListener implements TestExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        logger.debug("Setting up RestAssured.");

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setPort(Integer.valueOf(System.getProperty("server.port")))
                .build();
    }
}
