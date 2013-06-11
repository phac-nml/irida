package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for working with sequence files and samples.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleSequenceFilesIntegrationTest {

    @Test
    public void testAddSequenceFileToSample() throws IOException {
        String sampleUri = "http://localhost:8080/api//projects/6b80820f-38f8-4c73-83a6-12d17dc2c31c" +
                "/samples/8a3d677e-d309-4db6-9087-09d88cbea793";
        Response response = expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
        String sampleBody = response.getBody().asString();
        String sequenceFileUri = from(sampleBody).getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
        // prepare a file for sending to the server
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());

        Response r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value())
                .when().post(sequenceFileUri);

        // check that the location and link headers were created:
        String location = r.getHeader(HttpHeaders.LOCATION);
        String link = r.getHeader(HttpHeaders.LINK);

        assertNotNull(location);
        assertTrue(location.matches("http://localhost:8080/api/sequenceFiles/[a-f0-9\\-]+"));

        assertNotNull(link);
        assertTrue(link.matches("<http://localhost:8080/api/projects/[a-f0-9\\-]+/samples/[a-f0-9\\-]+" +
                "/sequenceFiles/[a-f0-9\\-]+>; rel=relationship"));

        // clean up
        Files.delete(sequenceFile);
    }
}
