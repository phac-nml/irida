package ca.corefacility.bioinformatics.irida.web.controller.test.integration;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

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

    @Test
    public void testDeleteSampleFromProject() {
        String projectUri = "http://localhost:8080/api/projects/6b80820f-38f8-4c73-83a6-12d17dc2c31c";

        // load the project
        String projectJson = get(projectUri).asString();
        String sampleLabel = from(projectJson).get("relatedResources.samples.resources[0].label");
        // get the uri for a specific sample associated with a project
        String sampleUri = from(projectJson).get("relatedResources.samples.resources[0].links.find{it.rel == 'self'}.href");
        // issue a delete against the service
        Response r = expect().statusCode(HttpStatus.OK.value()).when().delete(sampleUri);

        // check that the response body contains links to the project and samples collection
        String responseBody = r.getBody().asString();
        String projectUriRel = from(responseBody).get("resource.links.find{it.rel == 'project'}.href");
        assertNotNull(projectUriRel);
        assertEquals(projectUri, projectUriRel);

        String samplesUri = from(responseBody).get("resource.links.find{it.rel == 'project/samples'}.href");
        assertNotNull(samplesUri);
        assertEquals(projectUri + "/samples", samplesUri);

        // now confirm that the sample is not there anymore
        expect().body("relatedResources.samples.resources.label", not(hasItem(sampleLabel))).when().get(projectUri);
    }

    //@Test
    public void testUpdateProjectSample() {
        String projectUri = "http://localhost:8080/api/projects/6b80820f-38f8-4c73-83a6-12d17dc2c31c";
        String projectSampleUri = projectUri + "/samples/c6ce0cfa-2676-48fe-bd0c-c31d97c77d5c";
        Map<String, String> updatedFields = new HashMap<>();
        String updatedName = "Totally different sample name.";
        updatedFields.put("sampleName", updatedName);

        Response r = given().body(updatedFields).expect().statusCode(HttpStatus.OK.value()).when().patch(projectSampleUri);

        String responseBody = r.getBody().asString();
        String updatedUri = from(responseBody).get("resource.links.find{it.rel == 'project/sample'}.href");
        assertNotNull(updatedUri);
        assertEquals(projectSampleUri, updatedUri);

        // now confirm that the sample name was updated
        expect().body("relatedResources.samples.resources.label", hasItem(updatedName)).when().get(projectUri);
    }
}
