package ca.corefacility.bioinformatics.irida.web.controller.test.integration;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for project samples.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectSamplesIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesIntegrationTest.class);

    @Test
    public void testAddSampleToProject() {
        Map<String, String> sample = new HashMap<>();
        sample.put("sampleName", "sample 1");

        // load a project
        String projectsJson = get("/projects").asString();
        String projectUri = from(projectsJson).get("resource.resources[0].links[0].href");
        // get the uri for creating samples associated with the project.
        String projectJson = get(projectUri).asString();
        String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

        // post that uri
        Response r = given().body(sample).expect().response().statusCode(HttpStatus.CREATED.value()).when().post(samplesUri);

        // check that the locations are set appropriately.
        String location = r.getHeader(HttpHeaders.LOCATION);
        String linkLocation = r.getHeader(HttpHeaders.LINK);

        assertNotNull(location);
        assertTrue(location.matches("^http://localhost:8080/api/samples/[a-f0-9\\-]+$"));

        assertNotNull(linkLocation);
        assertTrue(linkLocation.matches("^<http://localhost:8080/api/projects/[a-f0-9\\-]+/samples/[a-f0-9\\-]+>; rel=relationship$"));
    }
}
