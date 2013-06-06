package ca.corefacility.bioinformatics.irida.web.controller.test.integration;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;

/**
 * Integration tests for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectIntegrationTest {

    private static final String USERNAME = "fbristow";
    private static final String PASSWORD = "password1";

    @Before
    public void setUp() {
        RestAssured.authentication = preemptive().basic(USERNAME, PASSWORD);
        RestAssured.requestContentType(ContentType.JSON);
    }

    /**
     * If I try to issue a create request for an object with an invalid field name, the server should respond with 400.
     */
    @Test
    public void testCreateProjectBadFieldName() {
        given().body("{ \"projectName\": \"some stupid project\" }").
                expect().response().statusCode(HttpStatus.BAD_REQUEST.value()).when().post("/projects");
    }
}
