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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

/**
 * Integration tests for sequence files.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectSequenceFilesIntegrationTest {

    private static final String PROJECT_URI = "http://localhost:8080/api/projects/dd82c9e0-40da-48a7-bb7c-1935f8144dbb";

    @Test
    public void testAddSequenceFileToProject() throws IOException {

        // prepare a file for sending to the server
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());

        // get the project
        String projectJson = get(PROJECT_URI).asString();
        // get the uri for adding new sequence files
        String sequenceFileUri = from(projectJson).get("resource.links.find{it.rel == 'project/sequenceFiles'}.href");

        // post the uri with the form data
        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value())
                .when().post(sequenceFileUri);

        // confirm that the location and relationship links look okay
        String location = r.getHeader(HttpHeaders.LOCATION);

        assertNotNull(location);
        assertTrue(location.matches("^http://localhost:8080/api/projects/[a-f0-9\\-]+/sequenceFiles/[a-f0-9\\-]+$"));

        String sequenceFileIdentifier = location.substring(location.lastIndexOf('/') + 1);

        // now confirm that when you get the project, the sequence file identifier is correct:
        expect().body("relatedResources.sequenceFiles.resources.identifier",
                hasItem(sequenceFileIdentifier)).when().get(PROJECT_URI).asString();


        // clean up after yourself.
        Files.delete(sequenceFile);
    }

    @Test
    public void testRemoveSequenceFileFromProject() throws IOException {
        // add the sequence file, then remove it
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACTGTAGCTAGTCGAGC".getBytes());

        // submit the file
        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
                .expect().statusCode(HttpStatus.CREATED.value()).when().post(PROJECT_URI + "/sequenceFiles");

        String location = r.getHeader(HttpHeaders.LOCATION);

        expect().body("resource.links.rel", hasItems("project", "project/sequenceFiles")).and()
                .statusCode(HttpStatus.OK.value()).when().delete(location);
        Files.delete(sequenceFile);
    }

    @Test
    public void testGetSequenceFileForProject() throws IOException {
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACTACGCATTGCTAGC".getBytes());

        // submit the file
        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
                .expect().statusCode(HttpStatus.CREATED.value()).when().post(PROJECT_URI + "/sequenceFiles");
        String location = r.getHeader(HttpHeaders.LOCATION);

        expect().body("resource.links.rel", hasItems("self", "project")).when().get(location);

        Files.delete(sequenceFile);
    }

    @Test
    public void testGetSequenceFileContentForProject() throws IOException {
        // add the sequence file, then remove it
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACTGTAGCTAGTCGAGC".getBytes());

        // submit the file
        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
                .expect().statusCode(HttpStatus.CREATED.value()).when().post(PROJECT_URI + "/sequenceFiles");
        String location = r.getHeader(HttpHeaders.LOCATION);

        r = given().header(HttpHeaders.ACCEPT, "application/fastq").get(location);
        byte[] fileContents = Files.readAllBytes(sequenceFile);

        assertArrayEquals(fileContents, r.getBody().asByteArray());

        Files.delete(sequenceFile);
    }
}
