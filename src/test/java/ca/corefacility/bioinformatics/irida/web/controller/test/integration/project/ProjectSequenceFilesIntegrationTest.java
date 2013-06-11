package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItems;
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

        // confirm that the location and relationship links look okay
        String location = r.getHeader(HttpHeaders.LOCATION);
        String linkLocation = r.getHeader(HttpHeaders.LINK);

        assertNotNull(location);
        assertTrue(location.matches("^http://localhost:8080/api/sequenceFiles/[a-f0-9\\-]+$"));

        assertNotNull(linkLocation);
        assertTrue(linkLocation.matches("^<http://localhost:8080/api/projects/[a-f0-9\\-]+/sequenceFiles/[a-f0-9\\-]+>;" +
                " rel=relationship$"));

        // clean up after yourself.
        Files.delete(sequenceFile);
    }

    @Test
    public void testRemoveSequenceFileFromProject() throws IOException {
        String sequenceFilesUri = "http://localhost:8080/api/projects/6b80820f-38f8-4c73-83a6-12d17dc2c31c/sequenceFiles";

        // add the sequence file, then remove it
        Path sequenceFile = Files.createTempFile("null", "null");
        Files.write(sequenceFile, ">test read\nACTGTAGCTAGTCGAGC".getBytes());

        // submit the file
        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
                .expect().statusCode(HttpStatus.CREATED.value()).when().post(sequenceFilesUri);

        String link = r.getHeader(HttpHeaders.LINK);
        // the link header needs to be parsed out to get the URL that we want, since only one header is sent back, we should
        // just parse out the only link that's between < and >.
        link = link.substring(1, link.indexOf('>', 1));

        expect().body("resource.links.rel", hasItems("project", "project/sequenceFiles")).and()
                .statusCode(HttpStatus.OK.value()).when().delete(link);
    }
}
