package ca.corefacility.bioinformatics.irida.web.controller.test.integration;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for sequence files.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectSequenceFilesIntegrationTest {

    @Test
    public void testAddSequenceFileToProject() throws IOException {
        String projectUri = "http://localhost:8080/api/projects/6b80820f-38f8-4c73-83a6-12d17dc2c31c";

        // prepare a file for sending to the server
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());

        // get the project
        String projectJson = get(projectUri).asString();
        // get the uri for adding new sequence files
        String sequenceFileUri = from(projectJson).get("resource.links.find{it.rel == 'project/sequenceFiles'}.href");

        // post the uri with the form data
        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value())
                .when().post(sequenceFileUri);

        String responseBody = r.getBody().asString();

        // confirm that the location and relationship links look okay
        String location = r.getHeader(HttpHeaders.LOCATION);
        String linkLocation = r.getHeader(HttpHeaders.LINK);

        assertNotNull(location);
        assertTrue(location.matches("^http://localhost:8080/api/sequenceFiles/[a-f0-9\\-]+$"));

        assertNotNull(linkLocation);
        assertTrue(location.matches("^<http://localhost:8080/api/projects/[a-f0-9\\-]+/sequenceFiles/[a-f0-9\\-]+>; rel=relationship$"));

        // clean up after yourself.
        Files.delete(sequenceFile);
    }
}
